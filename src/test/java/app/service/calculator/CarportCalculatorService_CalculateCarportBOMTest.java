package app.service.calculator;

import app.entities.Component;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarportCalculatorService_CalculateCarportBOMTest {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @Test
    void calculateCarportBOM_shouldReturnNonEmptyList() {
        try {
            Order dummyOrder = new Order(1, 600, 780, "Plast", "Test tekst");
            CarportCalculatorService calculator = new CarportCalculatorService(780, 600, connectionPool);

            List<Component> components = calculator.calculateCarportBOM(dummyOrder);

            assertNotNull(components, "Component-listen må ikke være null");
            assertFalse(components.isEmpty(), "Component-listen må ikke være tom");

        } catch (DatabaseException e) {
            fail("Fejl i beregning af BOM: " + e.getMessage());
        } catch (Exception e) {
            fail("Uventet fejl: " + e.getMessage());
        }
    }
}
