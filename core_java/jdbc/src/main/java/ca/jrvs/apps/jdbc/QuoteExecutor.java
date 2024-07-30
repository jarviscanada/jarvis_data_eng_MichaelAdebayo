package ca.jrvs.apps.jdbc;
import ca.jrvs.apps.jdbc.dao.Quote;
import ca.jrvs.apps.jdbc.dao.QuoteDao;

import ca.jrvs.apps.jdbc.dao.Position;
import ca.jrvs.apps.jdbc.dao.PositionDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class QuoteExecutor {
    public static void main(String[] args) throws SQLException {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost",
                "stock_quote", "postgres", "password");

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
