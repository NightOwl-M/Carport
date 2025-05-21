package app.mapper.order;


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

class OrderMapperTest {

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
                stmt.execute("DROP TABLE IF EXISTS test.component CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.material_variant CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.material CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.order_status CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.admin CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.zipcode CASCADE");

                stmt.execute("DROP SEQUENCE IF EXISTS test.customer_customer_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.admin_admin_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test.material_material_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test.material_variant_material_variant_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test.component_component_id_seq CASCADE");

                stmt.execute("CREATE TABLE test.customer AS (SELECT * from public.customer) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");
                stmt.execute("CREATE TABLE test.zipcode AS (SELECT * FROM public.zipcode) WITH NO DATA");
                stmt.execute("CREATE TABLE test.admin AS (SELECT * FROM public.admin) WITH NO DATA");
                stmt.execute("CREATE TABLE test.order_status AS (SELECT * FROM public.order_status) WITH NO DATA");
                stmt.execute("CREATE TABLE test.material AS (SELECT * FROM public.material) WITH NO DATA");
                stmt.execute("CREATE TABLE test.material_variant AS (SELECT * FROM public.material_variant) WITH NO DATA");
                stmt.execute("CREATE TABLE test.component AS (SELECT * FROM public.component) WITH NO DATA");

                stmt.execute("CREATE SEQUENCE test.customer_customer_id_seq");
                stmt.execute("ALTER TABLE test.customer ALTER COLUMN customer_id SET DEFAULT nextval('test.customer_customer_id_seq')");

                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");

                stmt.execute("CREATE SEQUENCE IF NOT EXISTS test.admin_admin_id_seq");
                stmt.execute("ALTER TABLE test.admin ALTER COLUMN admin_id SET DEFAULT nextval('test.admin_admin_id_seq')");

                stmt.execute("CREATE SEQUENCE test.material_material_id_seq");
                stmt.execute("ALTER TABLE test.material ALTER COLUMN material_id SET DEFAULT nextval('test.material_material_id_seq')");

                stmt.execute("CREATE SEQUENCE test.material_variant_material_variant_id_seq");
                stmt.execute("ALTER TABLE test.material_variant ALTER COLUMN material_variant_id SET DEFAULT nextval('test.material_variant_material_variant_id_seq')");

                stmt.execute("CREATE SEQUENCE test.component_component_id_seq");
                stmt.execute("ALTER TABLE test.component ALTER COLUMN component_id SET DEFAULT nextval('test.component_component_id_seq')");
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
                stmt.execute("DELETE FROM test.component");
                stmt.execute("DELETE FROM test.material_variant");
                stmt.execute("DELETE FROM test.material");
                stmt.execute("DELETE FROM test.order_status");
                stmt.execute("DELETE FROM test.admin");
                stmt.execute("DELETE FROM test.zipcode");

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
                stmt.execute("SELECT setval('test.admin_admin_id_seq', COALESCE((SELECT MAX(admin_id) + 1 FROM test.admin), 1), false)");
                stmt.execute("SELECT setval('test.material_material_id_seq', COALESCE((SELECT MAX(material_id) + 1 FROM test.material), 1), false)");
                stmt.execute("SELECT setval('test.material_variant_material_variant_id_seq', COALESCE((SELECT MAX(material_variant_id) + 1 FROM test.material_variant), 1), false)");
                stmt.execute("SELECT setval('test.component_component_id_seq', COALESCE((SELECT MAX(component_id) + 1 FROM test.component), 1), false)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database connection failed");
        }
    }

    @Test
    void testConnection() throws SQLException {
        assertNotNull(connectionPool.getConnection()); //Vi tester at når vi kalder vores connect() så returneres der ikke null
    }

    @Test
    void saveSessionOrder() {
    }

    @Test
    void updateOrderForSeller() {
    }

    @Test
    void updateOrderStatus() {
        try {
            //Arrange
            int expectedOrderStatus = 2;

            //Act
            OrderMapper.updateOrderStatus(1,2, connectionPool);
            Order order = OrderMapper.getOrderById(1, connectionPool);
            int actualOrderStatus = order.getStatusId();


            //Assert
            assertEquals(expectedOrderStatus, actualOrderStatus);

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getOrderById() {
        try {
        //Arrange
        Order expectedOrder = new Order (3, 3, 600, 780, "Plasttrapezplader",
                "Ønsker det i sort", "Tak for snakken", 3, 25000, Timestamp.valueOf("2025-05-08 12:30:15"));

        //Act
            Order actualOrder = OrderMapper.getOrderById(3, connectionPool);

        //Assert
        assertEquals(expectedOrder, actualOrder);

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getOrdersByStatus() {
    }

    /* //TODO slet, Jons tests, lader dem stå lidt til inspiration
    @Test
    void getAllOrders()
    {
        try
        {
            List<Order> orders = OrderMapper.getAllOrders(connectionPool);
            assertEquals(3, orders.size());
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void getOrderById()
    {
        try
        {
            User user = new User(1, "jon", "1234", "customer");
            Order expected = new Order(1, 1, 600, 780, 20000, user);
            Order dbOrder = OrderMapper.getOrderById(1, connectionPool);
            assertEquals(expected, dbOrder);
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }
    }

    @Test
    void insertOrder()
    {
        try
        {
            User user = new User(1, "jon", "1234", "customer");
            Order newOrder = new Order(2, 550, 750, 20000, user);
            newOrder = OrderMapper.insertOrder(newOrder, connectionPool);
            Order dbOrder = OrderMapper.getOrderById(newOrder.getOrderId(), connectionPool);
            assertEquals(newOrder, dbOrder);
        }
        catch (DatabaseException e)
        {
            fail("Database fejl: " + e.getMessage());
        }
    }
     */
}