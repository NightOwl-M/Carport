package app.mapper.order;

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

    public static List<Order> getOrdersByStatus(int statusId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM orders WHERE status_id = ?";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, statusId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(new Order(
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
                ));
            }

            return orders;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af ordrer med status ID: " + statusId, e);
        }
    }

}
