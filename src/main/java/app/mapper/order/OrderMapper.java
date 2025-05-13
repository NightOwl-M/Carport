package app.mapper.order;

import app.entities.Customer;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class OrderMapper {

    // Gemmer customers order i databasen
    public static Order saveSessionOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (customer_id, carport_width, carport_length, roof, customer_text, status_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getCustomerId());
            ps.setInt(2, order.getCarportWidth());
            ps.setInt(3, order.getCarportLength());
            ps.setString(4, order.getRoof());
            ps.setString(5, order.getCustomerText());
            ps.setInt(6, 1); // Standard status ved oprettelse

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setOrderId(rs.getInt(1));
                }
            }

            return order;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl under indsættelse af ordre: " + e.getMessage(), e);
        }
    }

    // updatere customers order med Admins tilbud
    public static void updateOrderForSeller(Order order, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET carport_width = ?, carport_length = ?, roof = ?, customer_text = ?, admin_text = ?, sales_price = ?, status_id = ? WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, order.getCarportWidth());
            ps.setInt(2, order.getCarportLength());
            ps.setString(3, order.getRoof());
            ps.setString(4, order.getCustomerText());
            ps.setString(5, order.getAdminText());
            ps.setDouble(6, order.getSalesPrice());
            ps.setInt(7, order.getStatusId());
            ps.setInt(8, order.getOrderId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Fejl under opdatering af ordre: " + e.getMessage(), e);
        }
    }

    // Ændre statuskode
    public static void updateOrderStatus(int orderId, int statusId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET status_id = ? WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, statusId);
            ps.setInt(2, orderId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Fejl under opdatering af ordrestatus: " + e.getMessage(), e);
        }
    }
    public static Order getOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                            rs.getInt("order_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("carport_width"),
                            rs.getInt("carport_length"),
                            rs.getString("roof"),
                            rs.getString("customer_text"),
                            rs.getString("admin_text"),
                            rs.getInt("status_id"),
                            rs.getDouble("sales_price"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af ordre: " + e.getMessage(), e);
        }
        return null;
    }

    public static Order getUnprocessedOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT o.order_id, o.customer_id, o.carport_width, o.carport_length, o.roof, o.customer_text, o.created_at, " +
                "c.customer_name, c.customer_email, c.customer_address, c.customer_zipcode, c.customer_phone " +
                "FROM orders o " +
                "JOIN customer c ON o.customer_id = c.customer_id " +
                "WHERE o.order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int orderIdResult = rs.getInt("order_id");
                    int customerId = rs.getInt("customer_id");
                    int carportWidth = rs.getInt("carport_width");
                    int carportLength = rs.getInt("carport_length");
                    String roof = rs.getString("roof");
                    String customerText = rs.getString("customer_text");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    // Kundeoplysninger
                    String customerName = rs.getString("customer_name");
                    String customerEmail = rs.getString("customer_email");
                    String customerAddress = rs.getString("customer_address");
                    int customerZipcode = rs.getInt("customer_zipcode");
                    String customerPhone = rs.getString("customer_phone");

                    // Opret Customer objekt
                    Customer customer = new Customer(customerId, customerName, customerEmail, customerAddress, customerZipcode, customerPhone);

                    // Opret Order objekt uden adminText og salesPrice
                    return new Order(orderIdResult, carportWidth, carportLength, roof, customerText, createdAt, customer);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af unprocessed ordre: " + e.getMessage(), e);
        }
        return null;
    }

    public static List<Order> getOrderSummariesByStatus(int statusId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT o.order_id, c.customer_email, o.created_at " +
                "FROM orders o " +
                "JOIN customer c ON o.customer_id = c.customer_id " +
                "WHERE o.status_id = ?";

        List<Order> orders = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, statusId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_email"),
                        rs.getTimestamp("created_at")
                ));
            }

            return orders;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af ordreoversigter med status ID: " + statusId, e);
        }
    }


    public static Order getOrderAndCustomerInfoByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders \n" +
                "JOIN customer USING(customer_id)  \n" +
                "JOIN order_status USING(status_id)\n" +
                "JOIN zipcode ON customer.customer_zipcode = zipcode.zipcode\n" +
                "WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String customerName = rs.getString("customer_name");
                String customerEmail = rs.getString("customer_email");
                String customerAddress = rs.getString("customer_address");
                int customerZipcode = rs.getInt("customer_zipcode");
                String customerPhone = rs.getString("customer_phone");

                int carportWidth = rs.getInt("carport_width");
                int carportLength = rs.getInt("carport_length");
                String roof = rs.getString("roof");
                String customerText = rs.getString("customer_text");
                String adminText = rs.getString("admin_text");
                int statusId = rs.getInt("status_id");
                double salesPrice = rs.getDouble("sales_price");
                Timestamp createdAt = rs.getTimestamp("created_at");


                Customer customer = new Customer(customerId, customerName, customerEmail, customerAddress, customerZipcode, customerPhone);
                Order order = new Order(orderId, carportWidth, carportLength, roof, customerText, adminText, statusId, salesPrice, createdAt, customer);
                return order;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
