package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.util.DatabaseConnectionManager;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.util.PositionService;
import ca.jrvs.apps.jdbc.util.QuoteService;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PositionService_IntTest {
    private PositionDao positionDao;
    private PositionService positionService;
    private QuoteDao quoteDao;
    private QuoteService quoteService;
    private QuoteHttpHelper quoteHttpHelper;
    private Connection c;
    private static DatabaseConnectionManager dcm;

    @Before
    public void setUp() {
        dcm = new DatabaseConnectionManager("localhost", "stock_quote", "postgres", "newpassword");
        try {
            c = dcm.getConnection();
            c.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Map<String, String> properties = new HashMap<>();
    try (BufferedReader br = new BufferedReader(
        new FileReader("src/main/resources/properties.txt"))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] tokens = line.split(":");
        properties.put(tokens[0], tokens[1]);
      }
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    }
        quoteHttpHelper = new QuoteHttpHelper(properties.get("api-key"));
        positionDao = new PositionDao(c);
        quoteDao = new QuoteDao(c);
        quoteService = new QuoteService( quoteDao, quoteHttpHelper);

        positionService = new PositionService(positionDao, quoteService , quoteDao);
    }

    @After
    public void tearDown() throws SQLException {
        if (c != null && !c.isClosed()) {
            c.rollback();
            c.close();
        }
    }

    @Test
    public void testBuyPosition() throws Exception {
        String ticker = "TSLA";
        int numberOfShares = 10;
        double price = 100.0;

        // Call the method being tested
        Position position = positionService.buy(ticker, numberOfShares, price);

        // Verify the returned position object
        assertEquals(ticker, position.getsymbol());
        assertEquals(numberOfShares , position.getNumOfShares());
        assertEquals(price, position.getValuePaid(), 0.01);
    }



    @Test
    public void testSellPosition() throws Exception {
        String ticker = "AAPL";

        // First buy a position to ensure there's something to sell
        positionService.buy(ticker, 10, 100.0);

        // Call the sell() method to sell the position
        positionService.sellAll(ticker);

        // Verify that the position is no longer present in the database
        assertFalse(positionDao.findById(ticker).isPresent());
    }
}
