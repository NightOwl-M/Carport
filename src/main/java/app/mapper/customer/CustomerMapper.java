package app.mapper.customer;

import app.entities.Customer;
import app.persistence.ConnectionPool;

import java.sql.*;

public class CustomerMapper {

    public static Customer insertCustomer(Customer customer, ConnectionPool connectionPool) throws Exception {
        String sql = "INSERT INTO customer (customer_name, customer_email, customer_address, customer_zipcode, customer_phone) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getCustomerName());
            ps.setString(2, customer.getCustomerEmail());
            ps.setString(3, customer.getCustomerAddress());
            ps.setInt(4, customer.getCustomerZipcode());
            ps.setString(5, customer.getCustomerPhone());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int customerId = rs.getInt(1);
                        return new Customer(
                                customerId,
                                customer.getCustomerName(),
                                customer.getCustomerEmail(),
                                customer.getCustomerAddress(),
                                customer.getCustomerZipcode(),
                                customer.getCustomerPhone()
                        );
                    }
                }
            }

            throw new Exception("Kunde kunne ikke indsættes i databasen.");
        } catch (SQLException e) {
            throw new Exception("Databasefejl ved indsættelse af kunde: " + e.getMessage());
        }
    }
}
