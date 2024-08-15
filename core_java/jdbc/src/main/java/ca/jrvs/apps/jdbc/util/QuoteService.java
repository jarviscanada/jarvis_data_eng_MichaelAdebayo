package ca.jrvs.apps.jdbc.util;

import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Quote;
import java.io.IOException;
import java.util.Optional;

public class QuoteService {

    private QuoteDao dao;
    private QuoteHttpHelper httpHelper;

    public QuoteService(QuoteDao dao, QuoteHttpHelper httpHelper) {
        this.dao = dao;
        this.httpHelper = httpHelper;
    }

    /**
     * Fetches latest quote data from endpoint
     * @param ticker
     * @return Latest quote information or empty optional if ticker symbol not found
     */


    public Optional<Quote> fetchQuoteDataFromAPI(String ticker)  {
        //TO DO
        try {
            Quote quote = httpHelper.fetchQuoteInfo(ticker);
            return Optional.of(quote);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}