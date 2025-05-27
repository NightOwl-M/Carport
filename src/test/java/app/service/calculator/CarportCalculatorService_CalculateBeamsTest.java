package app.service.calculator;

import app.entities.Component;
import app.entities.Material;
import app.entities.MaterialVariant;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CarportCalculatorService_CalculateBeamsTest {
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

                stmt.execute("INSERT INTO test.material (material_id, name, unit, price)" +
                        "VALUES (5, '97x97 mm. trykimp. Stolpe', 'stk', 75)," +
                        "(11, '45x195 mm. spærtræ ubh.', 'stk', 37)");

                stmt.execute("INSERT INTO test.material_variant (material_variant_id, material_id, length)" +
                        "VALUES (1, 5, 300), (2, 11, 300), (3, 11, 360), (4, 11, 420), (5, 11, 480), (6, 11, 540), (7, 11, 600)");

                stmt.execute("INSERT INTO test.zipcode (zipcode, city)" +
                        "VALUES (2100, 'København Ø'), (5000, 'Odense'), (3000, 'Helsingør')");

                stmt.execute("INSERT INTO test.order_status (status_id, status)" +
                        "VALUES (1, 'unprocessed'), (2, 'pending'), (3, ' processed')");

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
    void calculateBeams() throws DatabaseException {
        //Arrange
        Order order = new Order(3, 3, 600, 780, "Plasttrapezplader", "Ønsker det i sort", "Tak for snakken", 3, 25000, Timestamp.valueOf("2025-05-08 12:30:15"));
        CarportCalculatorService calc = new CarportCalculatorService(780, 600, connectionPool);
        List<Component> expectedOrderComponents = new ArrayList<>();


        /* Beregning af expedtedOrderComponents
        Først findes remmenes samlepunkt (altid stolpe nr. 2)
        780 - 130 = 650 cm (afstand imellem 2 yderste stolper)
        650 / 310 = 2.09 ~ 3 moduler
        650 / 3 = 216.7 ~ 217 cm (1 modul længde)


        1. rem = udhæng + 1 moduls længde
        100 + 217 = 317 ~ næste spær langt nok = 360 cm

        2. rem = total carport længde - 1. rems længde
        780 - 317 = 463 ~ næste spær langt nok = 480 cm
         */

        Material beam = new Material(11, "45x195 mm. spærtræ ubh.", "stk", 37);

        MaterialVariant beamVariant360 = new MaterialVariant(3, 360, beam);
        MaterialVariant beamVariant480 = new MaterialVariant(5, 480, beam);

        Component componentBeamVariant360 = new Component(3, 2, "Remme i sider, sadles ned i stolper", beamVariant360);
        Component componentBeamVariant480 = new Component(3, 2, "Remme i sider, sadles ned i stolper", beamVariant480);

        expectedOrderComponents.add(componentBeamVariant360);
        expectedOrderComponents.add(componentBeamVariant480);


        //Act
        calc.calculateBeams(order);
        List<Component> actualOrderComponents = calc.getOrderComponents();


        //Assert
        assertEquals(expectedOrderComponents.size(), actualOrderComponents.size());
        assertEquals(expectedOrderComponents.get(0), actualOrderComponents.get(0));
        assertEquals(expectedOrderComponents.get(1), actualOrderComponents.get(1));
    }
}
