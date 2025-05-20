package app.mapper.component;

import app.entities.Component;
import app.entities.Material;
import app.entities.MaterialVariant;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ComponentMapper_SaveOrderComponentsToDBTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setupTable() {
        try (Connection conn = connectionPool.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS test.component");
            stmt.execute("DROP TABLE IF EXISTS test.material_variant");
            stmt.execute("DROP TABLE IF EXISTS test.material");
            stmt.execute("DROP TABLE IF EXISTS test.orders");

            stmt.execute("CREATE TABLE test.material AS TABLE public.material WITH NO DATA");
            stmt.execute("CREATE TABLE test.material_variant AS TABLE public.material_variant WITH NO DATA");
            stmt.execute("CREATE TABLE test.orders AS TABLE public.orders WITH NO DATA");
            stmt.execute("CREATE TABLE test.component AS TABLE public.component WITH NO DATA");

            stmt.execute("INSERT INTO test.material (material_id, name, unit, price) VALUES (1, 'Træ', 'stk', 100.0)");
            stmt.execute("INSERT INTO test.material_variant (material_variant_id, material_id, length) VALUES (1, 1, 600)");
            stmt.execute("INSERT INTO test.orders (order_id, customer_id, carport_width, carport_length, roof, customer_text, status_id) VALUES (1, 1, 600, 780, 'Plast', 'Kommentar', 1)");

        } catch (SQLException e) {
            fail("Setup af test-tabeller fejlede: " + e.getMessage());
        }
    }

    @BeforeEach
    void clearTable() {
        try (Connection conn = connectionPool.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM test.component");
        } catch (SQLException e) {
            fail("Rydning af test.component fejlede: " + e.getMessage());
        }
    }

    @Test
    void saveOrderComponentsToDB_shouldInsertRows() {
        // Arrange
        Material material = new Material(1, "Træ", "stk", 100.0);
        MaterialVariant variant = new MaterialVariant(1, 600, material);
        Component component = new Component(1, 2, "Til rem", variant);
        List<Component> components = new ArrayList<>();
        components.add(component);

        // Act
        try {
            ComponentMapper.saveOrderComponentsToDB(components, connectionPool);
        } catch (DatabaseException e) {
            fail("Fejl under indsættelse: " + e.getMessage());
        }

        // Assert
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM test.component WHERE order_id = 1")) {

            ResultSet rs = ps.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("material_variant_id"));
            assertEquals(2, rs.getInt("quantity"));
            assertEquals("Til rem", rs.getString("use_description"));

        } catch (SQLException e) {
            fail("Fejl ved validering: " + e.getMessage());
        }
    }
}
