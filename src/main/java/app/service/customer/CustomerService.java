package app.service.customer;

import app.entities.Customer;
import app.exceptions.DatabaseException;
import app.mapper.customer.CustomerMapper;
import app.persistence.ConnectionPool;

public class CustomerService {

    public static Customer createCustomer(String name, String email, String address, int zipCode, String phone, ConnectionPool connectionPool) throws Exception {
        Customer customer = new Customer(0, name, email, address, zipCode, phone);
        return CustomerMapper.insertCustomer(customer, connectionPool);
    }

    public static String getCustomerEmailById(int customerId, ConnectionPool connectionPool) throws DatabaseException {
        return CustomerMapper.getCustomerEmailById(customerId, connectionPool);
    }



}
