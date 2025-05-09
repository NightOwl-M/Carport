package app.service.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.mapper.order.OrderMapper;
import app.persistence.ConnectionPool;


public class OrderService {

    public static Order saveSessionOrder(int customerId, int width, int length, String roof, String userText, ConnectionPool connectionPool) throws DatabaseException {
        Order order = new Order(customerId, width, length, roof, userText);
        return OrderMapper.saveSessionOrder(order, connectionPool);
    }


    public static void updateOrderForSeller(int orderId, int width, int length, String roof, String customerText, String adminText, double salesPrice, int statusId, ConnectionPool connectionPool) throws DatabaseException {
        Order order = new Order(orderId, width, length, roof, customerText, adminText, salesPrice, statusId);
        OrderMapper.updateOrderForSeller(order, connectionPool);
    }

    public static void updateOrderStatus(int orderId, int statusId, ConnectionPool connectionPool) throws DatabaseException {
        OrderMapper.updateOrderStatus(orderId, statusId, connectionPool);
    }
}
