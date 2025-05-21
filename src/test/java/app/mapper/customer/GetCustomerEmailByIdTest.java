package app.mapper.customer;

import app.persistence.ConnectionPool;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapper_GetCustomerEmailByIdTest {

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
    void insertCustomer() {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM test.customer");
            stmt.execute("INSERT INTO test.customer (customer_id, customer_name, customer_email, customer_address, customer_zipcode, customer_phone) " +
                    "VALUES (1, 'Test Bruger', 'test@eksempel.dk', 'Testvej 1', 4000, '12345678')");

        } catch (SQLException e) {
            fail("Fejl under indsættelse af testdata: " + e.getMessage());
        }
    }

    @Test
    void testGetCustomerEmailById_shouldReturnCorrectEmail() {
        try {
            String email = CustomerMapper.getCustomerEmailById(1, connectionPool);
            assertEquals("test@eksempel.dk", email);
        } catch (DatabaseException e) {
            fail("DatabaseException blev kastet: " + e.getMessage());
        }
    }

    @Test
    void testGetCustomerEmailById_shouldReturnNullIfNotFound() {
        try {
            String email = CustomerMapper.getCustomerEmailById(999, connectionPool);
            assertNull(email);
        } catch (DatabaseException e) {
            fail("DatabaseException blev kastet: " + e.getMessage());
        }
    }
}
