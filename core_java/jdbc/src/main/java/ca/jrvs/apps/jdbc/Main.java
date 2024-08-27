package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.util.PositionService;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.util.QuoteService;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("Starting application");
    // Load properties from file
    Map<String, String> properties = new HashMap<>();
    try (BufferedReader br = new BufferedReader(
        new FileReader("src/main/resources/properties.txt"))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] tokens = line.split(":");
        properties.put(tokens[0], tokens[1]);
      }
      logger.info("Loaded properties");
    } catch (FileNotFoundException e) {
      logger.error("Unable to load properties", e);
    } catch (IOException e) {
      logger.error("Error loading properties", e);
    }

    // Load database driver class
    try {
      Class.forName(properties.get("db-class"));
      logger.info("Loaded database driver");
    } catch (ClassNotFoundException e) {
      logger.error("Class not found", e);
    }

    // Create an OkHttpClient instance
    OkHttpClient client = new OkHttpClient();

    // Construct the database connection URL
    String url =
        "jdbc:postgresql://" + properties.get("server") + ":" + properties.get("port") + "/"
            + properties.get("database");
    logger.info("Database connection is successfully established");

    // Establish a database connection
    try (Connection c = DriverManager.getConnection(url, properties.get("username"),
        properties.get("password"))) {

      // Instantiate DAOs and services
      QuoteDao qRepo = new QuoteDao(c);
      PositionDao pRepo = new PositionDao(c);
      QuoteHttpHelper rcon = new QuoteHttpHelper(properties.get("api-key"));
      QuoteService sQuote = new QuoteService(qRepo, rcon);
      PositionService sPos = new PositionService(pRepo, sQuote, qRepo);

      // Instantiate and initialize the controller
      StockQuoteController con = new StockQuoteController(sQuote, sPos);
      con.initClient();

    } catch (SQLException e) {
      logger.error("Database connection failed", e);
    }
  }
}
