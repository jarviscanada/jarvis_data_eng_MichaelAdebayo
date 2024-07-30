package ca.jrvs.apps.jdbc.dao;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ca.jrvs.apps.jdbc.util.JsonParser;
import ca.jrvs.apps.jdbc.dao.Quote;


public class QuoteHttpHelper {


    private String apiKey;
    private OkHttpClient client;

    public QuoteHttpHelper(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }


    /**
     * Fetch latest quote data from Alpha Vantage endpoint
     * @param symbol
     * @return Quote with latest data
     * @throws IllegalArgumentException - if no data was found for the given symbol
     */

    public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException, IOException {

        String url = String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s&datatype=json", symbol, this.apiKey);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String jsonResponse = response.body().string();
            System.out.println("API Response: " + jsonResponse); // Debugging line to print the API response

            JsonNode rootNode = new ObjectMapper().readTree(jsonResponse);
            JsonNode errorMessageNode = rootNode.path("Error Message");
            if (!errorMessageNode.isMissingNode()) {
                throw new IllegalArgumentException("Error from API: " + errorMessageNode.asText());
            }

            JsonNode quoteNode = rootNode.path("Global Quote");
            if (quoteNode.isMissingNode()) {
                throw new IllegalArgumentException("Invalid response from API or symbol not found");
            }

            Quote quote = JsonParser.fromJson(quoteNode.toString(), Quote.class);
            if (quote == null || quote.getSymbol() == null || quote.getSymbol().isEmpty()) {
                throw new IllegalArgumentException("Invalid response from API or symbol not found");
            }

            return quote;
        }
    }

    public static void main (String[] args) throws IOException {
        QuoteHttpHelper helper = new QuoteHttpHelper("JV614M7E20SIEUO3");
        String symbol ="GOOGL";

        try {
            Quote quote = helper.fetchQuoteInfo(symbol);
            System.out.println("Symbol: " + quote.getSymbol());
            System.out.println("Ticker: " + quote.getTicker());
            System.out.println("Open: " + quote.getOpen());
            System.out.println("High: " + quote.getHigh());
            System.out.println("Low: " + quote.getLow());
            System.out.println("Price: " + quote.getPrice());
            System.out.println("Volume: " + quote.getVolume());
            System.out.println("Latest Trading Day: " + quote.getLatestTradingDay());
            System.out.println("Previous Close: " + quote.getPreviousClose());
            System.out.println("Change: " + quote.getChange());
            System.out.println("Change Percent: " + quote.getChangePercent());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }



    }




}