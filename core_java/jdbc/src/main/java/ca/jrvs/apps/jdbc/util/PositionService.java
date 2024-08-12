package ca.jrvs.apps.jdbc.util;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dao.QuoteDao;
import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;

public class PositionService {

    private PositionDao dao;
    private QuoteDao quoteDao;
    private QuoteService quoteService;



    public PositionService(PositionDao dao) {
        this.dao = dao;
    }

    public Position getPosition(String ticker) {
        return dao.findById(ticker).orElse(null);
    }

    /**
     * Processes a buy order and updates the database accordingly
     * @param ticker
     * @param numberOfShares
     * @param price
     * @return The position in our database after processing the buy
     */
    public Position buy(String ticker, int numberOfShares, double price) {

        updateQuoteTable(ticker);

        // Fetch the current position from the database
        Position currentPosition = dao.findById(ticker).orElse(null);

        if (currentPosition == null) {
            // Create a new position if it doesn't exist
            currentPosition = new Position();
            currentPosition.setsymbol(ticker);
            currentPosition.setNumOfShares(numberOfShares);
            currentPosition.setValuePaid(price);
            dao.save(currentPosition);
        } else {
            // Update the existing position
            int newShares = currentPosition.getNumOfShares() + numberOfShares;
            double newAveragePrice = ((currentPosition.getNumOfShares() * currentPosition.getValuePaid()) + (numberOfShares * price)) / newShares;
            currentPosition.setNumOfShares(newShares);
            currentPosition.setValuePaid(newAveragePrice);
            dao.save(currentPosition);
        }

        return currentPosition;
    }
    private void updateQuoteTable(String ticker) {
        // Fetch the latest quote data from the API
        Quote quote = quoteService.fetchQuoteDataFromAPI(ticker).orElseThrow(() -> new RuntimeException("Failed to fetch quote data"));

        // Update the quote with the latest price
        quoteDao.save(quote);
    }



    /**
     * Sells all shares of the given ticker symbol
     * @param ticker
     */
    public void sellAll(String ticker) {
        Position currentPosition = dao.findById(ticker).orElse(null);
        if (currentPosition != null) {
            double totalValue = currentPosition.getNumOfShares() * currentPosition.getValuePaid();
            System.out.println("Sold all shares of " + ticker + " for a total of $" + totalValue);
            dao.deleteById(ticker);
        } else {
            System.out.println("No position found for " + ticker);
        }
    }

    /**
     * Sells a specified number of shares of the given ticker symbol
     * @param ticker
     * @param numberOfShares
     */
    public void sell(String ticker, int numberOfShares) {
        Position currentPosition = dao.findById(ticker).orElse(null);
        if (currentPosition != null && currentPosition.getNumOfShares() >= numberOfShares) {
            double amountSoldFor = numberOfShares * currentPosition.getValuePaid();
            System.out.println("Sold " + numberOfShares + " shares of " + ticker + " for $" + amountSoldFor);
            int remainingShares = currentPosition.getNumOfShares() - numberOfShares;
            currentPosition.setNumOfShares(remainingShares);
            if (remainingShares == 0) {
                dao.deleteById(ticker);
            } else {
                dao.save(currentPosition);
            }
        } else {
            System.out.println("Not enough shares to sell or no position found for " + ticker);
        }
    }



}
