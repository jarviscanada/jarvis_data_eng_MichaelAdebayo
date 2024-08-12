package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.util.DatabaseConnectionManager;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.util.QuoteService;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QuoteService_IntTest {
    private QuoteDao quoteDao;
    private QuoteService quoteService;
    private static Connection c;
    private static DatabaseConnectionManager dcm;

    @Before
    public void setUpClass() {
        dcm = new DatabaseConnectionManager("localhost", "stock_quote", "postgres", "newpassword");
        try {
            c = dcm.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        quoteDao = new QuoteDao(c);
        QuoteHttpHelper quoteHttpHelper = new QuoteHttpHelper("JV614M7E20SIEUO3");
        quoteService = new QuoteService(quoteDao, quoteHttpHelper);
    }

    @Test
    public void fetchQuoteDataFromAPI() {
        String ticker = "MSFT";
        Optional<Quote> quote = quoteService.fetchQuoteDataFromAPI(ticker);
        assertTrue(quote.isPresent());
        assertEquals(ticker, quote.get().getSymbol());
    }
}
