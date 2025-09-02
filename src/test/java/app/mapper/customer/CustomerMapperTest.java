package app.mapper.customer;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    static void setupClass() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {

                stmt.execute("DROP TABLE IF EXISTS test.customer");
                stmt.execute("DROP TABLE IF EXISTS test.orders");
                stmt.execute("DROP SEQUENCE IF EXISTS test.customer_customer_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");

                stmt.execute("CREATE TABLE test.customer AS (SELECT * from public.customer) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");

                stmt.execute("CREATE SEQUENCE test.customer_customer_id_seq");
                stmt.execute("ALTER TABLE test.customer ALTER COLUMN customer_id SET DEFAULT nextval('test.customer_customer_id_seq')");
                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    @BeforeEach
    void setUp() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {

                stmt.execute("DELETE FROM test.orders");
                stmt.execute("DELETE FROM test.customer");

                stmt.execute("INSERT INTO test.customer (customer_id, customer_name, customer_email, customer_address, customer_zipcode, customer_phone) " +
                        "VALUES  (1, 'David', 'David.800@email.dk', 'Jollen 53', 3070, '12345678'), " +
                        "(2, 'Jeppe', 'jeppe@email.dk', 'Havnevej 12', 2800, '87654321')," +
                        "(3, 'Dennis', 'dennis@email.dk', 'Skovvej 5', 4000, '11223344')");


                stmt.execute("INSERT INTO test.orders (order_id, customer_id, carport_width, carport_length, roof, customer_text, admin_text, status_id, sales_price, created_at) " +
                        "VALUES (1, 1, 600, 780, 'Plasttrapezplader', 'Skal være god kvalitet', 'Bemærkning fra sælger', 1, 20000, '2025-05-06 10:10:49.630419')," +
                        "(2, 2, 600, 780, 'Plasttapezplader', 'Skal passe til huset', 'Afventer bekræftelse', 2, 15000, '2025-05-07 09:00:00')," +
                        "(3, 3, 600, 780, 'Plasttrapezplader', 'Ønsker det i sort', 'Tak for snakken', 3, 25000, '2025-05-08 12:30:15')");

                stmt.execute("SELECT setval('test.orders_order_id_seq', COALESCE((SELECT MAX(order_id) + 1 FROM test.orders), 1), false)");
                stmt.execute("SELECT setval('test.customer_customer_id_seq', COALESCE((SELECT MAX(customer_id) + 1 FROM test.customer), 1), false)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    @Test
    void getCustomerEmailById(){
        try{
            // Arrange
            int customerId = 1;
            String expectedEmail = "David.800@email.dk";

            // Act
            String actualEmail = CustomerMapper.getCustomerEmailById(customerId, connectionPool);

            // Assert
            assertEquals(expectedEmail, actualEmail);

        }catch(DatabaseException e){
            fail("Database fejl: " + e.getMessage());
        }
    }

}
