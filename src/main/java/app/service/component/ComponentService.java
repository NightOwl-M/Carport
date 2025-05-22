package app.service.component;

import app.entities.Component;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.mapper.component.ComponentMapper;
import app.persistence.ConnectionPool;
import app.service.calculator.CarportCalculatorService;

import java.util.List;

public class ComponentService {
    public static void saveOrderComponentsToDB (List<Component> orderComponents, ConnectionPool connectionPool) throws DatabaseException {
        ComponentMapper.saveOrderComponentsToDB(orderComponents, connectionPool);
    }

    public static List<Component> calculateBom(Order order, ConnectionPool connectionPool) throws DatabaseException {
        List<Component> orderComponents;

        CarportCalculatorService carportCalculatorService = new CarportCalculatorService
                (order.getCarportLength(),
                order.getCarportWidth(),
                connectionPool); //TODO mangler tag

        orderComponents = carportCalculatorService.calculateCarportBOM(order);

        return orderComponents;
    }
}
