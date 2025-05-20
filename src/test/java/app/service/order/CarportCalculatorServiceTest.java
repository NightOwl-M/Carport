package app.service.order;

import app.entities.Component;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import jdk.dynalink.linker.LinkerServices;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarportCalculatorServiceTest {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=test";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @Test
    void calculateCarportBOM() {
    }


    @Test
    void calculateCarportMaterialCost() throws DatabaseException {
        /*
        //Arrange
        CarportCalculatorService carportCalculatorService = new CarportCalculatorService(780, 600, connectionPool);
        Order order = new Order(780, 600, "intet tag", " ", " ");
        List<Component> orderComponents;

        orderComponents = carportCalculatorService.calculateCarportBOM(order);

        //Act

        Stolper = 8 stk * 300 cm * 75 kr/m = 1800 kr
        Remme = 2 stk * 360 cm * 37/m + 2 stk * 480 * 37/m = 621.6 kr
        Lægter = 15 stk * 600 cm * 37 kr/m = 3330 kr

        double expectedPrice = 5751.6;
        double actualPrice = carportCalculatorService.calculateCarportMaterialCost(orderComponents);

        //Assert
        assertEquals(expectedPrice, actualPrice);
        */
    }

    @Test
    void calculatePosts() {
    }

    @Test
    void calculatePostQuantity() {
        //Arrange
        CarportCalculatorService carportCalculatorService = new CarportCalculatorService(780, 600, connectionPool);
        CarportCalculatorService carportCalculatorService2 = new CarportCalculatorService(750, 600, connectionPool);

        /* Beregning af expectedQuantity
         780 - 100 - 30 = 650 cm
         650 / 310 = 2.09 ~ 3 (moduler med 1 stolpe i)
         3 moduler + 1 = 4 stolper
         4 * 2 = 8 stolper i alt
         */
        int expectedQuantity = 8;

        /* Beregning af expectedQuantity2
         750 - 100 - 30 = 620 cm
         620 / 310 = 2.0 ~ 2 (moduler med 1 stolpe i)
         2 moduler + 1 stolpe = 3 stolper
         3 * 2 = 6 stolper i alt
         */
        int expectedQuantity2 = 6;

        //Act
        int actualQuantity = carportCalculatorService.calculatePostQuantity();
        int actualQuantity2 = carportCalculatorService2.calculatePostQuantity();

        //Assert
        assertEquals(expectedQuantity, actualQuantity);
        assertEquals(expectedQuantity2, actualQuantity2);
    }

    @Test
    void calculateBeams() {
    }

    @Test
    void calculateBeamsJoiningPoint() {
        //Arrange
        CarportCalculatorService carportCalculatorService = new CarportCalculatorService(780, 600, connectionPool);
        CarportCalculatorService carportCalculatorService2 = new CarportCalculatorService(630, 600, connectionPool);

        /* Beregning af expectedQuantity
         780 - 100 - 30 = 650 cm
         650 / 310 = 2.09 ~ 3 (moduler med 1 stolpe i)
         650 / 3 = 216.6 ~ 217 cm (modulAfstand)
         100 + 217 = 317 cm (2. stolpes x-koordinat og remmenes samlepunkt)
         */
        int expectedQuantity = 317;

        /* Beregning af expectedQuantity2
         630 - 100 - 30 = 500 cm
         500 / 310 = 1.6 ~ 2
         500 / 2 = 250 cm (modulAfstand)
         100 + 250 = 350 cm (2. stolpes x-koordinat og remmenes samlepunkt)
         */
        int expectedQuantity2 = 350;

        //Act
        int actualQuantity = carportCalculatorService.calculateBeamsJoiningPoint();
        int actualQuantity2 = carportCalculatorService2.calculateBeamsJoiningPoint();

        //Assert
        assertEquals(expectedQuantity, actualQuantity);
        assertEquals(expectedQuantity2, actualQuantity2);
    }

    @Test
    void calculateRafters() {
    }

    @Test
    void calculateRaftersQuantity() {
        //Arrange
        CarportCalculatorService carportCalculatorService = new CarportCalculatorService(780, 600, connectionPool);
        CarportCalculatorService carportCalculatorService2 = new CarportCalculatorService(750, 600, connectionPool);

        /* Beregning af expectedQuantity
         780 - 4.5 = 775.5 cm
         775.5 / (4.5 + 55) = 13.03 ~ 14 (moduler med 1 spær i)
         14 moduler + 1 ende-spær = 15 spær i alt
         */
        int expectedQuantity = 15;

        /* Beregning af expectedQuantity2
         750 - 4.5 = 745.5 cm
         745.5 / (4.5 + 55) = 12.53 ~ 13 (moduler med 1 spær i)
         13 moduler + 1 endespær = 14 spær i alt
         */
        int expectedQuantity2 = 14;

        //Act
        int actualQuantity = carportCalculatorService.calculateRaftersQuantity(780);
        int actualQuantity2 = carportCalculatorService2.calculateRaftersQuantity(750);

        //Assert
        assertEquals(expectedQuantity, actualQuantity);
        assertEquals(expectedQuantity2, actualQuantity2);
    }
}