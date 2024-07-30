package ca.jrvs.apps.jdbc.dao;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuoteHttpHelperTest {

    @Test
    public void getApiKey() {
    }

    @Test
    public void setApiKey() {
    }

    @Test
    public void getClient() {
    }

    @Test
    public void setClient() {
    }

    @Test
    public void fetchQuoteInfoSymbol() {
        Quote quote = new Quote();
        quote.setSymbol("MSFT");
        assertEquals("MSFT", quote.getSymbol());
    }
}