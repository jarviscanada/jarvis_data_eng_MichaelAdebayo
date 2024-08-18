package ca.jrvs.apps.jdbc.dao;

import ca.jrvs.apps.jdbc.dto.Position;
import ca.jrvs.apps.jdbc.dto.Quote;
import ca.jrvs.apps.jdbc.util.PositionService;
import ca.jrvs.apps.jdbc.util.QuoteService;
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

    @Mock
    private QuoteService quoteService;

    @InjectMocks
    private PositionService positionService;

    @Mock
    private QuoteDao quoteDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void buyStock() {
        String ticker = "GOOGL";
        int numberOfShares = 50;
        double price = 160.00;

        Quote mockQuote = new Quote();
        mockQuote.setSymbol(ticker);
        when(quoteService.fetchQuoteDataFromAPI(ticker)).thenReturn(Optional.of(mockQuote));
        when(positionDao.findById(ticker)).thenReturn(Optional.empty());

        Position position = positionService.buy(ticker, numberOfShares, price);


        assertNotNull(position);
        assertEquals(ticker, position.getsymbol());
        assertEquals(numberOfShares, position.getNumOfShares());
        assertEquals(price, position.getValuePaid(), 0.001);
        verify(positionDao, times(1)).save(position);
        verify(quoteDao, times(1)).save(mockQuote);
    }




    @Test
    public void sellAllStock() {
        // Arrange
        String ticker = "GOOGL";
        Position mockPosition = new Position();
        mockPosition.setsymbol(ticker);
        mockPosition.setNumOfShares(50);
        mockPosition.setValuePaid(160.00);
        when(positionDao.findById(ticker)).thenReturn(Optional.of(mockPosition));

        // Act
        positionService.sellAll(ticker);

        // Assert
        verify(positionDao, times(1)).deleteById(ticker);
    }



    @Test
    public void sellStock() {
        // Define test variables
        String ticker = "GOOGL";
        int initialShares = 50;
        int sharesToSell = 1;
        double price = 160.00;

        // Create initial position
        Position initialPosition = new Position();
        initialPosition.setsymbol(ticker);
        initialPosition.setNumOfShares(initialShares);
        initialPosition.setValuePaid(price);

        // Create updated position
        Position updatedPosition = new Position();
        updatedPosition.setsymbol(ticker);
        updatedPosition.setNumOfShares(initialShares - sharesToSell);
        updatedPosition.setValuePaid(price);

        // Set up mock behavior
        when(positionDao.findById(ticker)).thenReturn(Optional.of(initialPosition));

        // Act
        positionService.sell(ticker, sharesToSell);

        // Assert
        verify(positionDao, times(1)).save(argThat(position ->
            position.getsymbol().equals(ticker) &&
                position.getNumOfShares() == initialShares - sharesToSell &&
                position.getValuePaid() == price
        ));
        verify(positionDao, times(0)).deleteById(ticker); // Should not delete as shares are remaining
    }

}