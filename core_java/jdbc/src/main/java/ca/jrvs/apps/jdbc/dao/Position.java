package ca.jrvs.apps.jdbc.dao;

public class Position {

    private String symbol; //id
    private int numOfShares;
    private double valuePaid; //total amount paid for shares

    public String getsymbol() {
        return symbol;
    }

    public void setsymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getNumOfShares() {
        return numOfShares;
    }

    public void setNumOfShares(int numOfShares) {
        this.numOfShares = numOfShares;
    }

    public double getValuePaid() {
        return valuePaid;
    }

    public void setValuePaid(double valuePaid) {
        this.valuePaid = valuePaid;
    }

    @Override
    public String toString() {
        return "Position{" +
                "symbol='" + symbol + '\'' +
                ", numOfShares=" + numOfShares +
                ", valuePaid=" + valuePaid +
                '}';
    }
}
