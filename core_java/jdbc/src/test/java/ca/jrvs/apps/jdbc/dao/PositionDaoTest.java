package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.util.DatabaseConnectionManager;
import ca.jrvs.apps.jdbc.dto.Position;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class PositionDaoTest {

    private Connection connection;
    private QuoteDao quoteDao;
    private PositionDao positionDao;


    @Before
    public void setUp() throws Exception {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost", "stock_quote", "postgres", "newpassword");
        try {
            connection = dcm.getConnection();
            connection.setAutoCommit(false);
            quoteDao = new QuoteDao(connection);
            positionDao = new PositionDao(connection);

        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
            connection.close();
        }
    }




    @Test
    public void save() {
        Position newPosition = new Position();
        newPosition.setsymbol("AAPL");
        newPosition.setNumOfShares(100);
        newPosition.setValuePaid(10000.00);

        // Save the new Position
        positionDao.save(newPosition);

        // Retrieve the saved Position and verify its fields
        Position savedPosition = positionDao.findById("AAPL").orElse(null);
        assertNotNull(savedPosition);
        assertEquals("AAPL", savedPosition.getsymbol());
        assertEquals(100, savedPosition.getNumOfShares());
        assertEquals(10000.00, savedPosition.getValuePaid(), 0.01);

    }

    @Test
    public void findById() {
        Position position = positionDao.findById("AAPL").orElse(null);
        assertNotNull(position);
        assertEquals("AAPL", position.getsymbol());

    }

    @Test
    public void findAll() {

        // Fetch all records and verify the results
        Iterable<Position> positions = positionDao.findAll();
        int count = 0;
        for (Position position : positions) {
            count++;
        }
        assertEquals(1, count);
    }

    @Test
    public void deleteById() {
        positionDao.deleteById("AAPL");
        Position position = positionDao.findById("AAPL").orElse(null);
        assertNull(position);
    }

    @Test
    public void deleteAll() {
        positionDao.deleteAll();
        Iterable<Position> positions = positionDao.findAll();
        int count = 0;
        for (Position position : positions) {
            count++;
        }
        assertEquals(0, count);
    }
}