package app.service.calculator;

import app.entities.Component;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarportCalculatorService_CalculateCarportMaterialCostTest {

    private static final Logger logger = LoggerFactory.getLogger(CarportCalculatorService_CalculateCarportMaterialCostTest.class);

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void createStructure() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS test.material (" +
                    "material_id SERIAL PRIMARY KEY, name VARCHAR, unit VARCHAR, price DOUBLE PRECISION)");
            stmt.execute("CREATE TABLE IF NOT EXISTS test.material_variant (" +
                    "material_variant_id SERIAL PRIMARY KEY, material_id INTEGER REFERENCES test.material(material_id), length INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS test.orders (" +
                    "order_id SERIAL PRIMARY KEY, carport_width INTEGER, carport_length INTEGER, roof VARCHAR, customer_text VARCHAR, admin_text VARCHAR, status_id INTEGER, sales_price DOUBLE PRECISION, created_at TIMESTAMP)");

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

            stmt.execute("TRUNCATE TABLE test.material_variant RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE test.material RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE test.orders RESTART IDENTITY CASCADE");

            stmt.execute("INSERT INTO test.material (material_id, name, unit, price) VALUES (5, 'Stolpe', 'stk', 25.0), (11, 'Spær/Rem', 'stk', 40.0)");
            stmt.execute("INSERT INTO test.material_variant (material_variant_id, material_id, length) VALUES (1, 5, 300), (2, 11, 600)");
            stmt.execute("INSERT INTO test.orders (order_id, carport_width, carport_length, roof, customer_text, admin_text, status_id, sales_price, created_at) VALUES (1, 300, 750, 'Plast', 'Kommentar', 'Admin', 1, 0.0, current_timestamp)");

            logger.info("Testdata indsat før test");

        } catch (SQLException e) {
            logger.error("Fejl i @BeforeEach ved indsættelse af testdata: {}", e.getMessage(), e);
            fail("Fejl i @BeforeEach: " + e.getMessage());
        }
    }

    @Test
    void testCalculateCarportMaterialCost_shouldReturnPositiveCost() {
        logger.info("Test: calculateCarportMaterialCost starter...");

        // Arrange
        Order order = new Order(1, 300, 750, "Plast", "Kommentar");
        CarportCalculatorService calculator = new CarportCalculatorService(order.getCarportLength(), order.getCarportWidth(), connectionPool);

        List<Component> components = null;
        try {
            components = calculator.calculateCarportBOM(order);
        } catch (DatabaseException e) {
            logger.error("Fejl under BOM-beregning: {}", e.getMessage(), e);
            fail("DatabaseException: " + e.getMessage());
        }

        // Act
        double totalPrice = calculator.calculateCarportMaterialCost(components);

        // Assert
        assertTrue(totalPrice > 0, "Materialepris bør være større end 0");
        logger.info("Materialepris beregnet korrekt: {} kr", totalPrice);
    }
}
