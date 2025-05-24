package app.service.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderService_UpdateOrderAndSendOfferTest {

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
            stmt.execute("DROP TABLE IF EXISTS test.customer");
            stmt.execute("CREATE TABLE test.customer AS TABLE public.customer WITH NO DATA");
            stmt.execute("CREATE TABLE test.orders AS TABLE public.orders WITH NO DATA");
            stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.customer_customer_id_seq");
            stmt.execute("ALTER TABLE test.customer ALTER COLUMN customer_id SET DEFAULT nextval('test.customer_customer_id_seq')");
            stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.orders_order_id_seq");
            stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");

            stmt.execute("INSERT INTO test.customer (customer_id, customer_name, customer_email, customer_address, customer_zipcode, customer_phone) " +
                    "VALUES (1, 'Test Bruger', 'test@test.dk', 'Testvej 123', 4000, '12345678')");

            stmt.execute("INSERT INTO test.orders (order_id, customer_id, carport_width, carport_length, roof, customer_text, status_id) " +
                    "VALUES (1, 1, 600, 780, 'Plast', 'Oprindelig tekst', 1)");

        } catch (SQLException e) {
            fail("Setup fejlede: " + e.getMessage());
        }
    }

    @Test
    void updateOrderAndSendOffer_shouldUpdateAndSend() {
        try {
            OrderService.updateOrderAndSendOffer(
                    1,
                    650,
                    790,
                    "Trapez",
                    "Ny tekst",
                    "Tilbud klar",
                    36000.0,
                    2,
                    connectionPool
            );

            Order updatedOrder = OrderService.getOrderById(1, connectionPool);
            assertNotNull(updatedOrder, "Ordren burde være fundet");
            assertEquals("Tilbud klar", updatedOrder.getAdminText());
            assertEquals(2, updatedOrder.getStatusId());

        } catch (DatabaseException e) {
            fail("Databasefejl: " + e.getMessage());
        } catch (IOException e) {
            fail("Fejl under e-mail afsendelse: " + e.getMessage());
        } catch (Exception e) {
            fail("Uventet fejl: " + e.getMessage());
        }
    }
}
