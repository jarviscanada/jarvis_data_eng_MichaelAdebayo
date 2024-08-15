package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.util.DatabaseConnectionManager;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.util.PositionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;


import static org.junit.Assert.*;

public class PositionService_IntTest {
    private PositionDao positionDao;
    private PositionService positionService;
    private Connection c;
    private static DatabaseConnectionManager dcm;

    @Before
    public void setUp() {
        dcm = new DatabaseConnectionManager("localhost", "stock_quote", "postgres", "newpassword");
        try {
            c = dcm.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        positionDao = new PositionDao(c);
        //positionService = new PositionService(positionDao, null);
    }

    @After
    public void tearDown() throws SQLException {
        if (c != null && !c.isClosed()) {
            c.close();
        }
    }

    @Test
    public void testBuyPosition() throws Exception {
        String ticker = "AAPL";
        int numberOfShares = 10;
        double price = 100.0;

        // Call the method being tested
        Position position = positionService.buy(ticker, numberOfShares, price);

        // Verify the returned position object
        assertNotNull(position);
        assertEquals(ticker, position.getsymbol());
        assertEquals(numberOfShares, position.getNumOfShares());
        assertEquals(price, position.getValuePaid(), 0.01);
    }

    @Test
    public void testSellPosition() throws Exception {
        String ticker = "MSFT";

        // First buy a position to ensure there's something to sell
        positionService.buy(ticker, 10, 100.0);

        // Call the sell() method to sell the position
        positionService.sellAll(ticker);

        // Verify that the position is no longer present in the database
        assertFalse(positionDao.findById(ticker).isPresent());
    }
}
