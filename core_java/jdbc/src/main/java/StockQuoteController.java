import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.util.PositionService;
import ca.jrvs.apps.jdbc.util.QuoteService;
import java.util.Optional;
import java.util.Scanner;

/**
 * A controller class that handles user interactions with the stock quote application.
 */
public class StockQuoteController {

  private QuoteService quoteService;
  private PositionService positionService;
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
      System.out.println("3. Sell All Stocks");
      System.out.println("4. View Position");
      System.out.println("5. Exit");

      String choice = scanner.nextLine();

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
          viewPosition(scanner);
          break;
        case "5":
          running = false;
          System.out.println("Thank you for using the Stock Quote App. Goodbye!");
          break;
        default:
          System.out.println("Invalid option. Please try again.");
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
    String symbol = scanner.nextLine();
    symbol = symbol.toUpperCase();
    try {
      Optional<Quote> quoteOpt = quoteService.fetchQuoteDataFromAPI(symbol);
      if (quoteOpt.isPresent()) {
        Quote quote = quoteOpt.get();
        System.out.println("Current stock price for " + symbol + ": " + quote.getPrice());
      } else {
        System.out.println("Stock symbol not found. Please try again.");
      }
    } catch (Exception e) {
      System.out.println("Error retrieving stock quote. Please check the symbol and try again.");
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
        System.out.println("Successfully bought " + shares + " shares of " + symbol);
      } else {
        System.out.println("Stock symbol not found. Please try again.");
      }
    } catch (Exception e) {
      System.out.println("Error buying stock. Please check the input and try again.");
      e.printStackTrace();  // For debugging purposes
    }
  }


  /**
   * Sells all shares of a given stock symbol.
   *
   * @param scanner the scanner to read user input from
   */
  private void sellStock(Scanner scanner) {
    System.out.print("Enter the stock symbol: ");
    String symbol = scanner.nextLine();
    try {
      positionService.sellAll(symbol);
    } catch (Exception e) {
      System.out.println("Error selling stock. Please check the input and try again.");
    }
  }

  /**
   * Views the position for a given stock symbol.
   *
   * @param scanner the scanner to read user input from
   */
  private void viewPosition(Scanner scanner) {
    System.out.print("Enter the stock symbol: ");
    String symbol = scanner.nextLine();
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
      System.out.println("Error retrieving position. Please check the symbol and try again.");
    }
  }
}
