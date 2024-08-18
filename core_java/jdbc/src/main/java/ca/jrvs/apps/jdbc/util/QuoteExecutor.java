package ca.jrvs.apps.jdbc.util;

import ca.jrvs.apps.jdbc.dao.PositionDao;
import ca.jrvs.apps.jdbc.dto.Position;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class QuoteExecutor {

  public static void main(String[] args) throws SQLException {
    DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost",
        "stock_quote", "postgres", "newpassword");

    try {
      Connection connection = dcm.getConnection();
      PositionDao positionDao = new PositionDao(connection);

      // Create a new Position
      Position newPosition = new Position();
      newPosition.setsymbol("AAPL");

      positionDao.save(newPosition);

      Optional<Position> retrievedPosition = positionDao.findById("AAPL");
      if (retrievedPosition.isPresent()) {
        System.out.println("Position found: " + retrievedPosition.get());
      } else {
        System.out.println("Position not found");
      }

      newPosition.setNumOfShares(200);
      newPosition.setValuePaid(155.0);
      positionDao.save(newPosition);

      Iterable<Position> allPositions = positionDao.findAll();
      for (Position position : allPositions) {
        System.out.println("Position: " + position);
      }


    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


}
