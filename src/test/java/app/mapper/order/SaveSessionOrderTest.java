package app.mapper.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.mapper.order.OrderMapper;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapper_SaveSessionOrderTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void createStructure() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            // Opret tabeller hvis de ikke findes
            stmt.execute("CREATE TABLE IF NOT EXISTS test.zipcode (zipcode INTEGER PRIMARY KEY, city VARCHAR)");
            stmt.execute("CREATE TABLE IF NOT EXISTS test.admin (admin_id SERIAL PRIMARY KEY, username VARCHAR NOT NULL, password VARCHAR NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS test.customer (" +
                    "customer_id SERIAL PRIMARY KEY," +
                    "customer_name VARCHAR NOT NULL," +
                    "customer_email TEXT NOT NULL," +
                    "customer_address VARCHAR NOT NULL," +
                    "customer_zipcode INTEGER REFERENCES test.zipcode(zipcode)," +
                    "customer_phone VARCHAR NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS test.order_status (status_id INTEGER PRIMARY KEY, status VARCHAR NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS test.orders (" +
                    "order_id SERIAL PRIMARY KEY," +
                    "customer_id INTEGER REFERENCES test.customer(customer_id)," +
                    "carport_width INTEGER NOT NULL," +
                    "carport_length INTEGER NOT NULL," +
                    "roof VARCHAR NOT NULL," +
                    "customer_text VARCHAR," +
                    "admin_text VARCHAR," +
                    "status_id INTEGER REFERENCES test.order_status(status_id)," +
                    "sales_price DOUBLE PRECISION DEFAULT 0.0," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        } catch (SQLException e) {
            fail("Fejl i @BeforeAll ved oprettelse af tabeller: " + e.getMessage());
        }
    }

    @BeforeEach
    void truncateAndInsertTestData() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            // Truncate relevante tabeller
            stmt.execute("TRUNCATE TABLE test.orders RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE test.customer RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE test.order_status RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE test.zipcode RESTART IDENTITY CASCADE");

            // Insert testdata
            stmt.execute("INSERT INTO test.zipcode (zipcode, city) VALUES (8000, 'Aarhus')");
            stmt.execute("INSERT INTO test.customer (customer_name, customer_email, customer_address, customer_zipcode, customer_phone) " +
                    "VALUES ('Testperson', 'test@example.com', 'Testvej 1', 8000, '12345678')");
            stmt.execute("INSERT INTO test.order_status (status_id, status) VALUES (1, 'unprocessed')");

        } catch (SQLException e) {
            fail("Fejl i @BeforeEach ved indsættelse af testdata: " + e.getMessage());
        }
    }

    @Test
    void testSaveSessionOrder_shouldInsertAndReturnOrderWithId() {
        //Arrange
        Order order = new Order(1, 300, 600, "Plast", "Testkommentar");

        //Act
        Order savedOrder = null;
        try {
            savedOrder = OrderMapper.saveSessionOrder(order, connectionPool);
        } catch (DatabaseException e) {
            fail("DatabaseException blev kastet: " + e.getMessage());
        }

        //Assert
        assertNotNull(savedOrder);
        assertTrue(savedOrder.getOrderId() > 0, "OrderId skal være sat og større end 0");
    }
}
