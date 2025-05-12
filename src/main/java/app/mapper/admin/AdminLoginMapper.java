package app.mapper.admin;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLoginMapper {

    /**
     * Tjekker admin login credentials i databasen.
     */
    public static boolean checkAdminLoginCredentials(String username, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM admin WHERE username = ? AND password = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved validering af admin login: " + e.getMessage(), e);
        }

        return false;
    }
}
