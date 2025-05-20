package app.service.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setupTestSchema() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS test.orders");
            stmt.execute("DROP TABLE IF EXISTS test.customer");

            stmt.execute("CREATE TABLE test.customer AS TABLE public.customer WITH NO DATA");
            stmt.execute("CREATE TABLE test.orders AS TABLE public.orders WITH NO DATA");

            stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.customer_customer_id_seq");
            stmt.execute("ALTER TABLE test.customer ALTER COLUMN customer_id SET DEFAULT nextval('test.customer_customer_id_seq')");

            stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.orders_order_id_seq");
            stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");

        } catch (SQLException e) {
            fail("Setup af test-skema fejlede: " + e.getMessage());
        }
    }

    @BeforeEach
    void clearData() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM test.orders");
            stmt.execute("DELETE FROM test.customer");
        } catch (SQLException e) {
            fail("Sletning af testdata fejlede: " + e.getMessage());
        }
    }

    @Test
    void testCreateOrderAndCustomer_success() {
        // Arrange
        String name = "Test Person";
        String email = "test@example.com";
        String address = "Testvej 1";
        int zipCode = 1234;
        String phone = "12345678";
        int width = 500;
        int length = 700;
        String roof = "Plast";
        String customerText = "Ønsker tag med lysplader";

        // Act
        try {
            OrderService.createOrderAndCustomer(name, email, address, zipCode, phone, width, length, roof, customerText, connectionPool);
        } catch (DatabaseException e) {
            fail("Metoden kastede en fejl: " + e.getMessage());
        }

        // Assert
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rsCustomer = stmt.executeQuery("SELECT * FROM test.customer WHERE customer_email = '" + email + "'");
            assertTrue(rsCustomer.next(), "Kunden blev ikke fundet i databasen");
            int customerId = rsCustomer.getInt("customer_id");

            ResultSet rsOrder = stmt.executeQuery("SELECT * FROM test.orders WHERE customer_id = " + customerId);
            assertTrue(rsOrder.next(), "Ordren blev ikke fundet i databasen");

            assertEquals(500, rsOrder.getInt("carport_width"));
            assertEquals(700, rsOrder.getInt("carport_length"));
            assertEquals("Plast", rsOrder.getString("roof"));
            assertEquals("Ønsker tag med lysplader", rsOrder.getString("customer_text"));
            assertEquals(1, rsOrder.getInt("status_id"));

        } catch (SQLException e) {
            fail("Fejl ved verifikation i databasen: " + e.getMessage());
        }
    }

    @Test
    void testCreateOrderAndCustomer_missingField_shouldThrowException() {
        // Arrange
        String name = "Fejl Bruger";
        String email = "fejl@example.com";
        String address = "Testvej 99";
        int zipCode = 9999;
        String phone = "00000000";
        int width = 500;
        int length = 700;
        String roof = null; // Jge sætter roof til at være null
        String customerText = "Der mangler tagtype";

        // Act & Assert
        Exception exception = assertThrows(DatabaseException.class, () -> {
            OrderService.createOrderAndCustomer(name, email, address, zipCode, phone, width, length, roof, customerText, connectionPool);
        });

        String expectedMessage = "ordreoplysninger mangler";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
