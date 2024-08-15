package ca.jrvs.apps.jdbc.util;

import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Quote;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteService {

  private static final Logger logger = LoggerFactory.getLogger(QuoteService.class);
  private final QuoteDao dao;
  private final QuoteHttpHelper httpHelper;

  public QuoteService(QuoteDao dao, QuoteHttpHelper httpHelper) {
    this.dao = dao;
    this.httpHelper = httpHelper;
  }

  /**
   * Fetches latest quote data from endpoint
   *
   * @param ticker
   * @return Latest quote information or empty optional if ticker symbol not found
   */


  public Optional<Quote> fetchQuoteDataFromAPI(String ticker) {
    logger.info("Fetching quote data from API for the ticker : {} ", ticker);
    //TO DO
    try {
      Quote quote = httpHelper.fetchQuoteInfo(ticker);
      logger.info("Fetched quote data from API for the ticker : {} ", ticker);
      return Optional.of(quote);
    } catch (IOException e) {
      logger.error("Error fetching quote data from API for the ticker : {} ", ticker, e);
    }
    return Optional.empty();
  }

}