package app.service.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.mapper.order.OrderMapper;
import app.persistence.ConnectionPool;
import io.javalin.http.Context;

public class OrderService {

    public static Order createOrder(int customerId, int width, int length, String roof, String userText, ConnectionPool connectionPool) throws DatabaseException {
        Order order = new Order(customerId, width, length, roof, userText);
        return OrderMapper.insertOrder(order, connectionPool);
    }

    public static void updateOrderForSeller(int orderId, int width, int length, String roof, String customerText, String adminText, double salesPrice, int statusId, ConnectionPool connectionPool) throws DatabaseException {
        Order order = new Order(orderId, width, length, roof, customerText, adminText, salesPrice, statusId);
        OrderMapper.updateOrder(order, connectionPool);
    }

    public static void updateOrderStatus(int orderId, int statusId, ConnectionPool connectionPool) throws DatabaseException {
        OrderMapper.updateOrderStatus(orderId, statusId, connectionPool);
    }

    public static void saveSessionOrder(Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        Order order = ctx.sessionAttribute("currentOrder");

        if (order == null) {
            throw new DatabaseException("Ingen ordre fundet i sessionen.");
        }

        OrderMapper.saveSessionOrder(order, connectionPool);
    }

    public static Order getOrderAndCustomerInfoByOrderId(int orderId, Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        Order order = OrderMapper.getOrderAndCustomerInfoByOrderId(orderId, connectionPool);
        return order;
    }
}
