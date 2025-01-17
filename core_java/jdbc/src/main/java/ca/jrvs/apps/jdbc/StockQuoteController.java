package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.util.PositionService;
import ca.jrvs.apps.jdbc.util.QuoteService;
import java.util.Optional;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A controller class that handles user interactions with the stock quote application.
 */
public class StockQuoteController {

  private static final Logger logger = LoggerFactory.getLogger(StockQuoteController.class);
  private final QuoteService quoteService;
  private final PositionService positionService;
  private Position position;
  private QuoteDao quoteDao;


  /**
   * Creates a new instance of {@link StockQuoteController}.
   *
   * @param quoteService    the quote service to use
   * @param positionService the position service to use
   */
  public StockQuoteController(QuoteService quoteService, PositionService positionService) {
    this.quoteService = quoteService;
    this.positionService = positionService;
  }

  /**
   * Starts the user interface for the application.
   */
  public void initClient() {
    Scanner scanner = new Scanner(System.in);
    boolean running = true;

    while (running) {
      System.out.println("\nWelcome to the Stock Quote App");
      System.out.println("Please select an option:");
      System.out.println("1. View Stocks");
      System.out.println("2. Buy Stocks");
      System.out.println("3. Sell Stocks");
      System.out.println("4. Sell All Stocks");
      System.out.println("5. View Position");
      System.out.println("6. Exit");

      String choice = scanner.nextLine();
      logger.info("User selected: " + choice);

      switch (choice) {
        case "1":
          viewStockQuote(scanner);
          break;
        case "2":
          buyStock(scanner);
          break;
        case "3":
          sellStock(scanner);
          break;
        case "4":
          sellAllStock(scanner);
          break;
        case "5":
          viewPosition(scanner);
          break;
        case "6":
          running = false;
          System.out.println("Thank you for using the Stock Quote App. Goodbye!");
          logger.info("User exited application");
          break;
        default:
          System.out.println("Invalid option. Please try again.");
          logger.warn(choice + " is not a valid option");
      }
    }
    scanner.close();
  }

  /**
   * Views the stock quote for a given stock symbol.
   *
   * @param scanner the scanner to read user input from
   */
  private void viewStockQuote(Scanner scanner) {
    System.out.print("Enter the stock symbol: ");
    String symbol = scanner.nextLine().toUpperCase();
    logger.info("User entered: " + symbol);
    symbol = symbol.toUpperCase();
    try {
      Optional<Quote> quoteOpt = quoteService.fetchQuoteDataFromAPI(symbol);
      if (quoteOpt.isPresent()) {
        Quote quote = quoteOpt.get();
        System.out.println("Current stock price for " + symbol + ": " + quote.getPrice());
        logger.info("Current stock price for " + symbol + ": " + quote.getPrice());
      } else {
        System.out.println("Stock symbol not found. Please try again.");
        logger.warn("Stock symbol not found. Please try again.");
      }
    } catch (Exception e) {
      logger.error("Error retrieving stock quote Please check the symbol and try again.", e);
    }
  }

  /**
   * Buys stocks for a given stock symbol and number of shares.
   *
   * @param scanner the scanner to read user input from
   */
  private void buyStock(Scanner scanner) {
    System.out.print("Enter the stock symbol: ");
    String symbol = scanner.nextLine().toUpperCase();  // Ensure the symbol is uppercase
    System.out.print("Enter the number of shares: ");
    int shares = Integer.parseInt(scanner.nextLine());

    try {
      Optional<Quote> quoteOpt = quoteService.fetchQuoteDataFromAPI(symbol);
      if (quoteOpt.isPresent()) {
        Quote quote = quoteOpt.get();
        positionService.updateQuoteTable(symbol, quote);
        positionService.buy(symbol, shares, quote.getPrice());
      } else {
        System.out.println("Stock symbol not found. Please try again.");
      }
    } catch (Exception e) {
      logger.error("Error buying stock . Please check the input and try again.", e);
    }
  }


  /**
   * Sells all shares of a given stock symbol.
   *
   * @param scanner the scanner to read user input from
   */
  private void sellAllStock(Scanner scanner) {
    System.out.print("Enter the stock symbol: ");
    String symbol = scanner.nextLine().toUpperCase();
    try {
      positionService.sellAll(symbol);
    } catch (Exception e) {
      logger.error("Error selling stock. Please check the input and try again. ", e);
    }
  }

  /**
   * Sells shares of a given stock symbol.
   *
   * @param scanner the scanner to read user input from
   */

  private void sellStock(Scanner scanner) {
    System.out.print("Enter the stock symbol: ");
    String symbol = scanner.nextLine().toUpperCase();
    System.out.print("Enter the number of shares: ");
    int shares = Integer.parseInt(scanner.nextLine());
    try {
      positionService.sell(symbol, shares);
    } catch (Exception e) {
      logger.error("Error selling stock. Please check the input and try again. ", e);
    }
  }


  /**
   * Views the position for a given stock symbol.
   *
   * @param scanner the scanner to read user input from
   */
  private void viewPosition(Scanner scanner) {
    System.out.print("Enter the stock symbol: ");
    String symbol = scanner.nextLine().toUpperCase();
    try {
      Optional<Position> positionOpt = Optional.ofNullable(positionService.getPosition(symbol));
      if (positionOpt.isPresent()) {
        Position position = positionOpt.get();
        double currentPrice = quoteService.fetchQuoteDataFromAPI(symbol).get().getPrice();
        double currentValue = currentPrice * position.getNumOfShares();
        System.out.println("You own " + position.getNumOfShares() + " shares of " + symbol);
        System.out.println("Total value: " + currentValue);
        System.out.println("Value paid: " + position.getValuePaid());
        System.out.println("Profit/Loss: " + (currentValue - position.getValuePaid()));
      } else {
        System.out.println("No position found for symbol: " + symbol);
      }
    } catch (Exception e) {
      logger.error("Error retrieving position. Please check the symbol and try again.", e);
    }
  }
}
