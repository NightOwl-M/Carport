package app.mapper.admin;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AdminLoginMapper_CheckAdminLoginCredentialsTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setupDatabase() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS test.admin");
            stmt.execute("CREATE TABLE test.admin AS TABLE public.admin WITH NO DATA");

        } catch (SQLException e) {
            fail("Fejl i setup af test-tabellen: " + e.getMessage());
        }
    }

    @BeforeEach
    void clearAndInsertTestData() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM test.admin");
            stmt.execute("INSERT INTO test.admin (username, password) VALUES ('admin', 'password123')");

        } catch (SQLException e) {
            fail("Fejl ved indsættelse af testdata: " + e.getMessage());
        }
    }

    @Test
    void testCheckAdminLoginCredentials_validCredentials_shouldReturnTrue() {
        try {
            boolean result = AdminLoginMapper.checkAdminLoginCredentials("admin", "password123", connectionPool);
            assertTrue(result);
        } catch (DatabaseException e) {
            fail("DatabaseException blev kastet: " + e.getMessage());
        }
    }

    @Test
    void testCheckAdminLoginCredentials_invalidCredentials_shouldReturnFalse() {
        try {
            boolean result = AdminLoginMapper.checkAdminLoginCredentials("admin", "wrongpass", connectionPool);
            assertFalse(result);
        } catch (DatabaseException e) {
            fail("DatabaseException blev kastet: " + e.getMessage());
        }
    }

    @Test
    void testCheckAdminLoginCredentials_nonexistentUser_shouldReturnFalse() {
        try {
            boolean result = AdminLoginMapper.checkAdminLoginCredentials("nonexistent", "password123", connectionPool);
            assertFalse(result);
        } catch (DatabaseException e) {
            fail("DatabaseException blev kastet: " + e.getMessage());
        }
    }
}
