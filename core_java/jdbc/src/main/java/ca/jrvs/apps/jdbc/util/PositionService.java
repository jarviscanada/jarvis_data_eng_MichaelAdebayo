package ca.jrvs.apps.jdbc.util;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import java.sql.Timestamp;

public class PositionService {

  private final PositionDao positionDao;
  private final QuoteDao quoteDao;
  private final QuoteService quoteService;
  private Quote quote;

  /*
      public PositionService(PositionDao positionDao, QuoteService quoteService) {
          this.positionDao = positionDao;
          this.quoteService = quoteService;

      }

   */
  public PositionService(PositionDao positionDao, QuoteService quoteService, QuoteDao quoteDao) {
    this.positionDao = positionDao;
    this.quoteService = quoteService;
    this.quoteDao = quoteDao;  // Initialize QuoteDao
  }

  public Position getPosition(String ticker) {
    return positionDao.findById(ticker).orElse(null);
  }

  /**
   * Processes a buy order and updates the database accordingly
   *
   * @param ticker
   * @param numberOfShares
   * @param price
   * @return The position in our database after processing the buy
   */
  public Position buy(String ticker, int numberOfShares, double price) {
    // Convert ticker to uppercase for consistency
    ticker = ticker.toUpperCase();

    // Fetch the latest quote data from the QuoteService
    Quote quote = quoteService.fetchQuoteDataFromAPI(ticker).get();  // Fetch the latest quote

    // Fetch the latest quote data and update the quote table
    updateQuoteTable(ticker, quote);

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


  public void updateQuoteTable(String ticker, Quote quote) {

    quote.setTimestamp(new Timestamp(System.currentTimeMillis()));
    quoteDao.save(quote);
  }


  /**
   * Sells all shares of the given ticker symbol
   *
   * @param ticker
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
   * Sells a specified number of shares of the given ticker symbol
   *
   * @param ticker
   * @param numberOfShares
   */
  public void sell(String ticker, int numberOfShares) {
    Position currentPosition = positionDao.findById(ticker).orElse(null);
    if (currentPosition != null && currentPosition.getNumOfShares() >= numberOfShares) {
      double amountSoldFor = numberOfShares * currentPosition.getValuePaid();
      System.out.println(
          "Sold " + numberOfShares + " shares of " + ticker + " for $" + amountSoldFor);
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