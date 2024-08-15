package ca.jrvs.apps.jdbc.util;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * Service class for handling position related operations.
 */
public class PositionService {

  private final PositionDao positionDao;
  private final QuoteDao quoteDao;
  private final QuoteService quoteService;

  /**
   * Constructor for PositionService.
   *
   * @param positionDao  the position dao
   * @param quoteService the quote service
   * @param quoteDao     the quote dao
   */
  public PositionService(PositionDao positionDao, QuoteService quoteService, QuoteDao quoteDao) {
    this.positionDao = positionDao;
    this.quoteService = quoteService;
    this.quoteDao = quoteDao;  // Initialize QuoteDao
  }

  /**
   * Retrieves the position associated with the given ticker symbol.
   *
   * @param ticker The ticker symbol of the position to retrieve.
   * @return The position associated with the given ticker symbol, or null if no position is found.
   */
  public Position getPosition(String ticker) {
    return positionDao.findById(ticker).orElse(null);
  }

  /**
   * Processes a buy order and updates the database accordingly.
   *
   * @param ticker         The ticker symbol of the stock to buy
   * @param numberOfShares The number of shares to buy
   * @param price          The price per share of the stock
   * @return The position in our database after processing the buy
   */
  public Position buy(String ticker, int numberOfShares, double price) {
    // Convert ticker to uppercase for consistency
    ticker = ticker.toUpperCase();

    // Fetch the latest quote data from the QuoteService
    Optional<Quote> quote = quoteService.fetchQuoteDataFromAPI(ticker);  // Fetch the latest quote

    // Fetch the latest quote data and update the quote table
    updateQuoteTable(ticker, quote.get());

    // Fetch the current position from the database
    Position currentPosition = positionDao.findById(ticker).orElse(null);

    if (currentPosition == null) {
      // Create a new position if it doesn't exist
      currentPosition = new Position();
      currentPosition.setsymbol(ticker);
      currentPosition.setNumOfShares(numberOfShares);
      currentPosition.setValuePaid(price);
      positionDao.save(currentPosition);
      System.out.println("Bought " + numberOfShares + " shares of " + ticker + " at $" + price);
    } else {
      // Update the existing position
      int newShares = currentPosition.getNumOfShares() + numberOfShares;
      double totalPaid =
          (currentPosition.getNumOfShares() * currentPosition.getValuePaid()) + (numberOfShares
              * price);
      double newAveragePrice = totalPaid / newShares;
      currentPosition.setNumOfShares(newShares);
      currentPosition.setValuePaid(newAveragePrice);
      positionDao.save(currentPosition);
      System.out.println(
          "Updated position: Bought " + numberOfShares + " more shares of " + ticker + " at $"
              + price);
    }

    return currentPosition;
  }

  /**
   * Updates the quote table with the latest quote data from the QuoteService.
   *
   * @param ticker the ticker symbol of the stock
   * @param quote  the latest quote data for the given ticker
   */
  public void updateQuoteTable(String ticker, Quote quote) {
    quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
    quoteDao.save(quote);
  }

  /**
   * Sells all shares of the given ticker symbol.
   *
   * @param ticker The ticker symbol of the stock to sell
   */
  public void sellAll(String ticker) {
    Position currentPosition = positionDao.findById(ticker).orElse(null);
    if (currentPosition != null) {
      double totalValue = currentPosition.getNumOfShares() * currentPosition.getValuePaid();
      System.out.println("Sold all shares of " + ticker + " for a total of $" + totalValue);
      positionDao.deleteById(ticker);
    } else {
      System.out.println("No position found for " + ticker);
    }
  }

  /**
   * Sells a specified number of shares of the given ticker symbol.
   *
   * @param ticker         The ticker symbol of the stock to sell
   * @param numberOfShares The number of shares to sell
   */
  public void sell(String ticker, int numberOfShares) {
    Position currentPosition = positionDao.findById(ticker).orElse(null);
    if (currentPosition != null && currentPosition.getNumOfShares() >= numberOfShares) {
      double amountSoldFor = numberOfShares * currentPosition.getValuePaid();
      System.out.println(
          "Sold " + numberOfShares + " shares of " + ticker + " for $" + String.format("%.2f",
              amountSoldFor));
      int remainingShares = currentPosition.getNumOfShares() - numberOfShares;
      currentPosition.setNumOfShares(remainingShares);
      if (remainingShares == 0) {
        positionDao.deleteById(ticker);
      } else {
        positionDao.save(currentPosition);
      }
    } else {
      System.out.println("Not enough shares to sell or no position found for " + ticker);
    }
  }

}
