package ca.jrvs.practice.codingChallenge;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

public class SimpleCalculatorImplTest {

    SimpleCalculator calculator = new SimpleCalculatorImpl();

    @Test
    public void onePlusOneShouldEqualTwo() {

        int expected = 2;
        int actual = calculator.add(1, 1);
        assertEquals(expected, actual);
    }

    @Test
    public void test_subtract() {
        int expected = 0;
        int actual = calculator.subtract(1, 1);
        assertEquals(expected, actual);
    }

    @Test
    public void test_multiply() {
        //write your test here
        int expected = 10;
        int actual = calculator.multiply(5, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void test_divide() {
        //write your test here
        double expected = 5.0;
        double actual = calculator.divide(10, 2);
        double delta = 0.001;
        assertEquals(expected, actual, delta);
    }

}