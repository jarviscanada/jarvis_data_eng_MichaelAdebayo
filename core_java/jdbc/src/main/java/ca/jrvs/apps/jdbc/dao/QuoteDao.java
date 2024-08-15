package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.dto.Quote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuoteDao implements CrudDao<Quote, String> {

  private static final String INSERT = "INSERT INTO quote (symbol, open, high, low, price, volume, "
      + "latest_trading_day, previous_close, change, change_percent, timestamp) "
      + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String SELECT_ONE = "SELECT * FROM quote WHERE symbol = ?";
  private static final String SELECT_ALL = "SELECT * FROM quote";
  private static final String UPDATE = "UPDATE quote SET open = ?, high = ?, low = ?, price = ?, "
      + "volume = ?, latest_trading_day = ?, previous_close = ?, change = ?, change_percent = ?, timestamp = ? "
      + "WHERE symbol = ?";
  private static final String DELETE = "DELETE FROM quote WHERE symbol = ?";
  private static final String DELETE_ALL = "DELETE FROM quote";
  private final Connection c;

  public QuoteDao(Connection c) {
    this.c = c;
  }

  @Override
  public Quote save(Quote entity) throws IllegalArgumentException {
    if (entity == null) {
      throw new IllegalArgumentException("entity is null");
    }
    try {
      if (findById(entity.getSymbol()).isPresent()) {
        try (PreparedStatement statement = c.prepareStatement(UPDATE)) {
          statement.setDouble(1, entity.getOpen());
          statement.setDouble(2, entity.getHigh());
          statement.setDouble(3, entity.getLow());
          statement.setDouble(4, entity.getPrice());
          statement.setInt(5, entity.getVolume());
          statement.setDate(6, entity.getLatestTradingDay());
          statement.setDouble(7, entity.getPreviousClose());
          statement.setDouble(8, entity.getChange());
          statement.setString(9, entity.getChangePercent());
          statement.setTimestamp(10, entity.getTimestamp());
          statement.setString(11, entity.getSymbol());
          statement.executeUpdate();
        }
      } else {
        try (PreparedStatement statement = c.prepareStatement(INSERT)) {
          statement.setString(1, entity.getSymbol());
          statement.setDouble(2, entity.getOpen());
          statement.setDouble(3, entity.getHigh());
          statement.setDouble(4, entity.getLow());
          statement.setDouble(5, entity.getPrice());
          statement.setInt(6, entity.getVolume());
          statement.setDate(7, entity.getLatestTradingDay());
          statement.setDouble(8, entity.getPreviousClose());
          statement.setDouble(9, entity.getChange());
          statement.setString(10, entity.getChangePercent());
          statement.setTimestamp(11, entity.getTimestamp());
          statement.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return entity;
  }

  @Override
  public Optional<Quote> findById(String symbol) throws IllegalArgumentException {
    Quote quote = new Quote();
    try (PreparedStatement statement = this.c.prepareStatement(SELECT_ONE)) {
      statement.setString(1, symbol);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        quote.setSymbol(rs.getString("symbol"));
        quote.setOpen(rs.getDouble("open"));
        quote.setHigh(rs.getDouble("high"));
        quote.setLow(rs.getDouble("low"));
        quote.setPrice(rs.getDouble("price"));
        quote.setVolume(rs.getInt("volume"));
        quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
        quote.setPreviousClose(rs.getDouble("previous_close"));
        quote.setChange(rs.getDouble("change"));
        quote.setChangePercent(rs.getString("change_percent"));
        quote.setTimestamp(rs.getTimestamp("timestamp"));
        return Optional.of(quote);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  @Override
  public Iterable<Quote> findAll() {
    List<Quote> quotes = new ArrayList<>();
    try (PreparedStatement statement = this.c.prepareStatement(SELECT_ALL);
        ResultSet rs = statement.executeQuery()) {
      while (rs.next()) {
        Quote quote = new Quote();
        quote.setSymbol(rs.getString("symbol"));
        quote.setOpen(rs.getDouble("open"));
        quote.setHigh(rs.getDouble("high"));
        quote.setLow(rs.getDouble("low"));
        quote.setPrice(rs.getDouble("price"));
        quote.setVolume(rs.getInt("volume"));
        quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
        quote.setPreviousClose(rs.getDouble("previous_close"));
        quote.setChange(rs.getDouble("change"));
        quote.setChangePercent(rs.getString("change_percent"));
        quote.setTimestamp(rs.getTimestamp("timestamp"));
        quotes.add(quote);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return quotes;
  }

  @Override
  public void deleteById(String symbol) throws IllegalArgumentException {
    try (PreparedStatement statement = this.c.prepareStatement(DELETE)) {
      statement.setString(1, symbol);
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteAll() {
    try (PreparedStatement statement = this.c.prepareStatement(DELETE_ALL)) {
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
