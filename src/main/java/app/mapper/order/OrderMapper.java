package app.mapper.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static List<Order> getOrdersByStatusId(int status, ConnectionPool connectionPool) throws DatabaseException {

        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM orders o " +
                "JOIN order_status s ON o.status_id = s.status_id " +
                "JOIN customers c ON o.order_id = c.order_id " +
                "WHERE o.status_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, status);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int orderId = rs.getInt("order_id");
                int customerId = rs.getInt("user_id");
                int carportWidth = rs.getInt("carport_width");
                int carportLength = rs.getInt("carport_length");
                String roof = rs.getString("roof");
                String customerText = rs.getString("user_text");
                int statusId = rs.getInt("status_id");
                double salesPrice = rs.getDouble("sales_price");

                Order order = new Order(orderId, customerId, carportWidth, carportLength, roof, customerText, statusId, salesPrice);
                orders.add(order);

            }

            return orders;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af ordrer: " + e.getMessage());
        }
    }

}
