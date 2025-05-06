package app.mapper.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.*;

public class OrderMapper {

    public static Order insertOrder(int userId, int width, int length, String roof, String userText, int status, double price, Timestamp createdAt, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (user_id, carport_width, carport_length, roof, user_text, status, sales_price, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Order newOrder = null;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setInt(2, width);
            ps.setInt(3, length);
            ps.setString(4, roof);
            ps.setString(5, userText);
            ps.setInt(6, status);
            ps.setDouble(7, price);
            ps.setTimestamp(8, createdAt);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int orderId = rs.getInt(1);
                        newOrder = new Order(orderId, userId, width, length, roof, userText, status, price, createdAt);
                    }
                }
            } else {
                throw new DatabaseException("Ordren blev ikke oprettet.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Databasefejl: " + e.getMessage());
        }

        return newOrder;
    }
}


