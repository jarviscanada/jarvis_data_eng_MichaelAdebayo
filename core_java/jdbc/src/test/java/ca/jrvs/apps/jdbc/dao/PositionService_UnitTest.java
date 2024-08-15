package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.util.PositionService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;


public class PositionService_UnitTest {
    @Mock
    private PositionDao positionDao;

    @InjectMocks
    private PositionService positionService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void buyStock() {
        // Define test variables
        String ticker = "AAPL";
        int numberOfShares = 50;
        double price = 160.00;

        // Create a mock position object
        Position existingPosition = new Position();
        existingPosition.setsymbol(ticker);
        existingPosition.setNumOfShares(100);
        existingPosition.setValuePaid(150.00);

        // Set up the mock behavior of PositionDao.findById
        when(positionDao.findById(ticker)).thenReturn(Optional.of(existingPosition));

        // Call the method being tested
        Position position = positionService.buy(ticker, numberOfShares, price);

        // Verify the returned position object
        assertEquals(ticker, position.getsymbol());
        assertEquals(150, position.getNumOfShares());
        assertEquals(153.33, position.getValuePaid(), 0.01);

        // Verify that the PositionDao.save method was called once with the correct position object
        verify(positionDao, times(1)).save(position);

    }


    @Test
    public void sellStock() {
        // Define test variables
        String ticker = "GOOGL";

        // Call the method under test
        positionService.sellAll(ticker);

        // Verify that positionDao.deleteById was called once with the correct ticker
        verify(positionDao, times(1)).deleteById(ticker);
    }
}