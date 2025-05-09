package app.mapper.order;

import app.entities.Customer;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.*;

public class OrderMapper {

    public static Order insertOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (customer_id, carport_width, carport_length, roof, customer_text, status_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getCustomerId());
            ps.setInt(2, order.getCarportWidth());
            ps.setInt(3, order.getCarportLength());
            ps.setString(4, order.getRoof());
            ps.setString(5, order.getCustomerText());
            ps.setInt(6, order.getStatusId());

            ps.executeUpdate();
            return order;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl under indsættelse af ordre: " + e.getMessage(), e);
        }
    }


    public static void updateOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
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


    public static void saveSessionOrder(Order order, ConnectionPool connectionPool) throws DatabaseException {
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

    public static Order getOrderAndCustomerInfoByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders \n" +
                "JOIN customer USING(customer_id)  \n" +
                "JOIN order_status USING(status_id)\n" +
                "JOIN zipcode ON customer.customer_zipcode = zipcode.zipcode\n" +
                "WHERE order_id = ?";

        //TODO lav eventulet til et view
        //TODO tilføj eventuelt String orderStatusText til Order entity, da det er mere sigende end int statusId

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