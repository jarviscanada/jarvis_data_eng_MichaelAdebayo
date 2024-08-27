package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.dto.Position;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PositionDao implements CrudDao<Position, String> {

  private static final String INSERT = "INSERT INTO position (symbol, number_of_shares, value_paid) VALUES (?, ?, ?)";
  private static final String DELETE = "DELETE FROM position WHERE symbol = ?";
  private static final String SELECT_ALL = "SELECT * FROM position";
  private static final String SELECT_ONE = "SELECT * FROM position WHERE symbol = ?";
  private static final String DELETE_ALL = "DELETE FROM position";
  private static final String UPDATE = "UPDATE position SET number_of_shares = ?, value_paid = ? WHERE symbol = ?";
  private final Connection c;

  public PositionDao(Connection c) {
    this.c = c;
  }

  @Override
  public Position save(Position entity) throws IllegalArgumentException {
    try {
      try (PreparedStatement selectStatement = this.c.prepareStatement(SELECT_ONE)) {
        selectStatement.setString(1, entity.getsymbol());
        try (ResultSet rs = selectStatement.executeQuery()) {
          if (rs.next()) {
            try (PreparedStatement updateStatement = this.c.prepareStatement(UPDATE)) {
              updateStatement.setInt(1, entity.getNumOfShares());
              updateStatement.setDouble(2, entity.getValuePaid());
              updateStatement.setString(3, entity.getsymbol());
              updateStatement.executeUpdate();
            }
          } else {
            // Insert new record
            try (PreparedStatement insertStatement = this.c.prepareStatement(INSERT)) {
              insertStatement.setString(1, entity.getsymbol());
              insertStatement.setInt(2, entity.getNumOfShares());
              insertStatement.setDouble(3, entity.getValuePaid());
              insertStatement.executeUpdate();
            }
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Database error occurred", e);
    }
    return entity;
  }

  @Override
  public Optional<Position> findById(String s) throws IllegalArgumentException {
    Position position = new Position();
    try (PreparedStatement statement = this.c.prepareStatement(SELECT_ONE)) {
      statement.setString(1, s);
      ResultSet rs = statement.executeQuery();
      if (rs.next()) {
        position.setsymbol(rs.getString("symbol"));
        position.setNumOfShares(rs.getInt("number_of_shares"));
        position.setValuePaid(rs.getDouble("value_paid"));

        return Optional.of(position);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Database error occurred", e);
    }
    return Optional.empty();
  }

  @Override
  public Iterable<Position> findAll() {
    List<Position> positions = new ArrayList<>();

    try (PreparedStatement statement = this.c.prepareStatement(SELECT_ALL);
        ResultSet rs = statement.executeQuery()) {

      while (rs.next()) {
        Position position = new Position();
        position.setsymbol(rs.getString("symbol"));
        position.setNumOfShares(rs.getInt("number_of_shares"));
        position.setValuePaid(rs.getDouble("value_paid"));
        positions.add(position);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Database error occurred", e);
    }

    return positions;
  }

  @Override
  public void deleteById(String s) throws IllegalArgumentException {
    try (PreparedStatement statement = this.c.prepareStatement(DELETE)) {
      statement.setString(1, s);
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
