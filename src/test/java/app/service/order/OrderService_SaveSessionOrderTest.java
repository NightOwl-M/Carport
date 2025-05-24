package app.service.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderService_SaveSessionOrderTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setup() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS test.orders");
            stmt.execute("CREATE TABLE test.orders AS TABLE public.orders WITH NO DATA");
            stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.orders_order_id_seq");
            stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");

        } catch (SQLException e) {
            fail("Setup fejlede: " + e.getMessage());
        }
    }

    @Test
    void saveSessionOrder_shouldInsertOrder() {
        try {
            Order savedOrder = OrderService.saveSessionOrder(1, 600, 780, "Plast", "Test tekst", connectionPool);

            assertNotNull(savedOrder, "Ordren må ikke være null");
            assertTrue(savedOrder.getOrderId() > 0, "OrderId skal være genereret");
            assertEquals(600, savedOrder.getCarportWidth());
            assertEquals(780, savedOrder.getCarportLength());

        } catch (DatabaseException e) {
            fail("Fejl ved ordreoprettelse: " + e.getMessage());
        } catch (Exception e) {
            fail("Uventet fejl: " + e.getMessage());
        }
    }
}
