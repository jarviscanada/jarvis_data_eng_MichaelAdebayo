package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.util.QuoteHttpHelper;
import ca.jrvs.apps.jdbc.util.QuoteService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ca.jrvs.apps.jdbc.dto.Quote;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;


public class QuoteService_UnitTest {

    @Mock
    private QuoteDao quoteDao;

    @Mock
    private QuoteHttpHelper quoteHttpHelper;

    @InjectMocks
    private QuoteService quoteService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }






    @Test
    public void fetchQuoteDataFromAPI() throws IOException {
        // Set up the test data
        String symbol = "GOOGL"; // Symbol of the quote to fetch
        Quote quote = new Quote(); // Create a mock quote object
        quote.setSymbol(symbol);
        quote.setOpen(155.50);
        quote.setHigh(164.43);
        quote.setLow(154.93);
        quote.setPrice(159.25);
        quote.setVolume(53630673);
        quote.setChange(-7.41);
        quote.setChangePercent("-4.4462%");

        // Set up the mock behavior of the QuoteHttpHelper
        when(quoteHttpHelper.fetchQuoteInfo(symbol)).thenReturn(quote);

        // Call the method being tested
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(symbol);

        // Verify that the method returned the expected result
        assertTrue(result.isPresent());
        assertEquals(symbol, result.get().getSymbol());
        assertEquals(155.50, result.get().getOpen(), 0);
        assertEquals(164.43, result.get().getHigh(), 0);
        assertEquals(154.93, result.get().getLow(), 0);
        assertEquals(159.25, result.get().getPrice(), 0);
        assertEquals(53630673, result.get().getVolume());
        assertEquals(-7.41, result.get().getChange(), 0);
        assertEquals("-4.4462%", result.get().getChangePercent());

    }



}