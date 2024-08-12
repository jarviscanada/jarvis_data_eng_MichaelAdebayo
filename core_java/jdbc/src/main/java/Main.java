import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.util.PositionService;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.util.QuoteService;
import okhttp3.OkHttpClient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Load properties from file
        Map<String, String> properties = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/properties.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                properties.put(tokens[0], tokens[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load database driver class
        try {
            Class.forName(properties.get("db-class"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Create an OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Construct the database connection URL
        String url = "jdbc:postgresql://" + properties.get("server") + ":" + properties.get("port") + "/" + properties.get("database");

        // Establish a database connection
        try (Connection c = DriverManager.getConnection(url, properties.get("username"), properties.get("password"))) {

            // Instantiate DAOs and services
            QuoteDao qRepo = new QuoteDao(c);
            PositionDao pRepo = new PositionDao(c);
            QuoteHttpHelper rcon = new QuoteHttpHelper(properties.get("api-key"));
            QuoteService sQuote = new QuoteService(qRepo, rcon);
            PositionService sPos = new PositionService(pRepo);

            // Instantiate and initialize the controller
            StockQuoteController con = new StockQuoteController(sQuote, sPos);
            con.initClient();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
