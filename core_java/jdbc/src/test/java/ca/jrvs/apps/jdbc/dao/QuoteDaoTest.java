package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.util.DatabaseConnectionManager;
import ca.jrvs.apps.jdbc.dto.Quote;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.Assert.*;

public class QuoteDaoTest {
    private Connection connection;
    private QuoteDao quoteDao;

    @Before
    public void setUp() throws Exception {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost", "stock_quote", "postgres", "newpassword");
        try {
             connection = dcm.getConnection();
             connection.setAutoCommit(false);
             quoteDao = new QuoteDao(connection);

        } catch (SQLException e){
            e.printStackTrace();
        }

    }


    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
            connection.close();
        }
    }




    @Test
    public void save() {
        Quote newQuote = new Quote();
        newQuote.setSymbol("AAPL");
        newQuote.setOpen(433.58);
        newQuote.setHigh(432.15);
        newQuote.setLow(424.70);
        newQuote.setPrice(426.73);
        newQuote.setVolume(15125836);
        newQuote.setLatestTradingDay(Date.valueOf("2024-07-29"));
        newQuote.setPreviousClose(425.27);
        newQuote.setChange(1.4600);
        newQuote.setChangePercent("0.3433%");
        newQuote.setTimestamp(Timestamp.valueOf("2024-07-30 10:30:40"));

        quoteDao.save(newQuote);

        Optional<Quote> retrievedQuote = quoteDao.findById("AAPL");
        assertTrue(retrievedQuote.isPresent());
        assertEquals(426.73, retrievedQuote.get().getPrice(), 0.0);
    }

    @Test
    public void findById() {
        Optional<Quote> retrievedQuote = quoteDao.findById("AAPL");
        assertTrue(retrievedQuote.isPresent());
        assertEquals("AAPL", retrievedQuote.get().getSymbol());
    }

    @Test
    public void findAll() {
        Iterable<Quote> quotes = quoteDao.findAll();
        int count = 0;
        for (Quote quote : quotes) {
            count++;
        }
        assertEquals(1, count);
    }

    @Test
    public void deleteById() {
        quoteDao.deleteById("AAPL");
        Optional<Quote> retrievedQuote = quoteDao.findById("AAPL");
        assertFalse(retrievedQuote.isPresent());
    }

    @Test
    public void deleteAll() {
        quoteDao.deleteAll();
        Iterable<Quote> quotes = quoteDao.findAll();
        int count = 0;
        for (Quote quote : quotes) {
            count++;
        }
        assertEquals(0, count);
    }
}