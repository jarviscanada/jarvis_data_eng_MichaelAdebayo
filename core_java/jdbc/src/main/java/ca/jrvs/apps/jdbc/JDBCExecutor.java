package ca.jrvs.apps.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCExecutor {
    public static void main(String[] args) {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost",
                "hplussport", "postgres", "password");
        try{
            Connection connection = dcm.getConnection();
            CustomerDAO customerDAO = new CustomerDAO(connection);
            Customer customer = new Customer();
            customer.setFirstName("Michael");
            customer.setLastName("Jordan");
            customer.setEmail("michael@jordan23.com");
            customer.setAddress("GOAT AVE");
            customer.setCity("Chicago");
            customer.setState("IL");
            customer.setPhone("(123) 456-7890");
            customer.setZipCode("12345");


            Customer dbCustomer = customerDAO.create(customer);
            System.out.println(dbCustomer);
            dbCustomer = customerDAO.findById(dbCustomer.getId());
            System.out.println(dbCustomer);
            dbCustomer.setEmail("michael.jordan@goat.com");
            dbCustomer = customerDAO.update(dbCustomer);
            System.out.println(dbCustomer);
            customerDAO.delete(customer.getId());




        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
