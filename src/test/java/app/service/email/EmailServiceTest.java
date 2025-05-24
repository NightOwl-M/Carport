package app.service.email;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.service.order.OrderService;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class EmailService_UpdateOrderAndSendOfferTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setupClass() {
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS test.orders");
            stmt.execute("DROP TABLE IF EXISTS test.customer");
            stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE");
            stmt.execute("DROP SEQUENCE IF EXISTS test.customer_customer_id_seq CASCADE");

            stmt.execute("CREATE TABLE test.customer AS (SELECT * FROM public.customer) WITH NO DATA");
            stmt.execute("CREATE TABLE test.orders AS (SELECT * FROM public.orders) WITH NO DATA");

            stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
            stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");

            stmt.execute("CREATE SEQUENCE test.customer_customer_id_seq");
            stmt.execute("ALTER TABLE test.customer ALTER COLUMN customer_id SET DEFAULT nextval('test.customer_customer_id_seq')");

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database setup fejlede: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute("DELETE FROM test.orders");
            stmt.execute("DELETE FROM test.customer");

            stmt.execute("INSERT INTO test.customer (customer_id, customer_name, customer_email, customer_address, customer_zipcode, customer_phone) " +
                    "VALUES (1, 'Test Customer', 'Carportcarportnoreply@gmail.com', 'Testvej 1', 1234, '12345678')");

            stmt.execute("INSERT INTO test.orders (order_id, customer_id, carport_width, carport_length, roof, customer_text, admin_text, status_id, sales_price, created_at) " +
                    "VALUES (1, 1, 600, 780, 'Plasttrapezplader', 'Ønsker stærkt tag', 'Opdateret kommentar', 1, 15000, '2025-05-13 10:10:49')");

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Setup fejlede: " + e.getMessage());
        }
    }

    @Test
    void testUpdateOrderAndSendOffer_shouldUpdateOrderAndSendEmail() {
        try {
            // Arrange
            int orderId = 1;
            int width = 800;
            int length = 1000;
            String roof = "Metal";
            String customerText = "Skal kunne holde til sne";
            String adminText = "Tilføjet snebeskyttelse";
            double salesPrice = 20000;
            int statusId = 2;

            // Act
            OrderService.updateOrderAndSendOffer(orderId, width, length, roof, customerText, adminText, salesPrice, statusId, connectionPool);

            // Assert – valider at dataen er opdateret korrekt i databasen
            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT * FROM test.orders WHERE order_id = ?")) {

                ps.setInt(1, orderId);
                ResultSet rs = ps.executeQuery();

                assertTrue(rs.next());
                assertEquals(width, rs.getInt("carport_width"));
                assertEquals(length, rs.getInt("carport_length"));
                assertEquals(roof, rs.getString("roof"));
                assertEquals(customerText, rs.getString("customer_text"));
                assertEquals(adminText, rs.getString("admin_text"));
                assertEquals(salesPrice, rs.getDouble("sales_price"));
                assertEquals(statusId, rs.getInt("status_id"));

            } catch (SQLException e) {
                fail("Validering i databasen fejlede: " + e.getMessage());
            }

            System.out.println("Order blev opdateret og email sendt.");

        } catch (DatabaseException | IOException e) {
            e.printStackTrace();
            fail("Fejl under updateOrderAndSendOffer: " + e.getMessage());
        }
    }

}
