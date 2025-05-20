package app.mapper.customer;

import app.entities.Customer;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setupTable() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS test.customer");
            stmt.execute("CREATE TABLE test.customer AS TABLE public.customer WITH NO DATA");

            stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.customer_customer_id_seq");
            stmt.execute("ALTER TABLE test.customer ALTER COLUMN customer_id SET DEFAULT nextval('test.customer_customer_id_seq')");

        } catch (SQLException e) {
            fail("Setup af test-tabellen fejlede: " + e.getMessage());
        }
    }

    @BeforeEach
    void clearTable() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM test.customer");
        } catch (SQLException e) {
            fail("Rydning af test.customer fejlede: " + e.getMessage());
        }
    }

    @Test
    void testSaveSessionCustomer_shouldInsertCustomer() {
        // Arrange
        Customer testCustomer = new Customer("Mikkel Tester", "mikkel@test.dk", "Testervej 42", 4000, "12345678");

        // Act
        try {
            Customer saved = CustomerMapper.saveSessionCustomer(testCustomer, connectionPool);

            // Assert
            assertNotNull(saved);
            assertTrue(saved.getCustomerId() > 0);
            assertEquals("Mikkel Tester", saved.getCustomerName());

            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM test.customer WHERE customer_id = ?")) {
                ps.setInt(1, saved.getCustomerId());
                ResultSet rs = ps.executeQuery();
                assertTrue(rs.next());
                assertEquals("mikkel@test.dk", rs.getString("customer_email"));
            }

        } catch (Exception e) {
            fail("Fejl under oprettelse af kunde: " + e.getMessage());
        }
    }
}
