package app.service.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderService_UpdateOrderAndSendOfferTest {

    private static final Logger logger = LoggerFactory.getLogger(OrderService_UpdateOrderAndSendOfferTest.class);

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void createStructure() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS test.zipcode (zipcode INTEGER PRIMARY KEY, city VARCHAR)");
            stmt.execute("CREATE TABLE IF NOT EXISTS test.customer (" +
                    "customer_id SERIAL PRIMARY KEY, customer_name VARCHAR, customer_email TEXT, customer_address VARCHAR, customer_zipcode INTEGER REFERENCES test.zipcode(zipcode), customer_phone VARCHAR)");
            stmt.execute("CREATE TABLE IF NOT EXISTS test.order_status (status_id INTEGER PRIMARY KEY, status VARCHAR)");
            stmt.execute("CREATE TABLE IF NOT EXISTS test.orders (" +
                    "order_id SERIAL PRIMARY KEY, customer_id INTEGER REFERENCES test.customer(customer_id), carport_width INTEGER, carport_length INTEGER, roof VARCHAR, customer_text VARCHAR, admin_text VARCHAR, status_id INTEGER REFERENCES test.order_status(status_id), sales_price DOUBLE PRECISION, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            logger.info("Testtabeller oprettet");

        } catch (SQLException e) {
            logger.error("Fejl i @BeforeAll ved oprettelse af tabeller: {}", e.getMessage(), e);
            fail("Fejl i @BeforeAll: " + e.getMessage());
        }
    }

    @BeforeEach
    void truncateAndInsertTestData() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("TRUNCATE TABLE test.orders RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE test.customer RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE test.order_status RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE test.zipcode RESTART IDENTITY CASCADE");

            stmt.execute("INSERT INTO test.zipcode (zipcode, city) VALUES (8000, 'Aarhus')");
            stmt.execute("INSERT INTO test.customer (customer_name, customer_email, customer_address, customer_zipcode, customer_phone) " +
                    "VALUES ('Testperson', 'test@example.com', 'Testvej 1', 8000, '12345678')");
            stmt.execute("INSERT INTO test.order_status (status_id, status) VALUES (1, 'unprocessed'), (2, 'pending')");
            stmt.execute("INSERT INTO test.orders (order_id, customer_id, carport_width, carport_length, roof, customer_text, admin_text, status_id, sales_price) " +
                    "VALUES (1, 1, 300, 600, 'Plast', 'Test', 'Adminnote', 1, 0.0)");

            logger.info("Testdata indsat før test");

        } catch (SQLException e) {
            logger.error("Fejl i @BeforeEach ved indsættelse af testdata: {}", e.getMessage(), e);
            fail("Fejl i @BeforeEach: " + e.getMessage());
        }
    }

    @Test
    void testUpdateOrderAndSendOffer_shouldUpdateAndTriggerEmail() {
        logger.info("Test: updateOrderAndSendOffer starter...");

        try {
            // Act
            OrderService.updateOrderAndSendOffer(
                    1,
                    360,
                    620,
                    "Stål",
                    "Opdateret kommentar fra kunde",
                    "Tilføjet note fra sælger",
                    32999.95,
                    2,
                    connectionPool
            );

            // Assert
            Order updatedOrder = OrderService.getOrderById(1, connectionPool);
            assertNotNull(updatedOrder);
            assertEquals(360, updatedOrder.getCarportWidth());
            assertEquals(620, updatedOrder.getCarportLength());
            assertEquals("Stål", updatedOrder.getRoof());
            assertEquals("Tilføjet note fra sælger", updatedOrder.getAdminText());
            assertEquals(32999.95, updatedOrder.getSalesPrice());
            assertEquals(2, updatedOrder.getStatusId());

            logger.info("Ordre opdateret og emailforsøg gennemført");

        } catch (DatabaseException | IOException e) {
            logger.error("Fejl i updateOrderAndSendOffer: {}", e.getMessage(), e);
            fail("Exception kastet under test: " + e.getMessage());
        }
    }
}
