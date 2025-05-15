package app.service.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.mapper.order.OrderMapper;
import app.persistence.ConnectionPool;
import app.service.email.EmailService;
import app.service.customer.CustomerService;

import java.io.IOException;
import java.util.List;


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

    public static Order getOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        return OrderMapper.getOrderById(orderId, connectionPool);
    }

    public static List<Order> getOrderSummariesByStatus(int statusId, ConnectionPool connectionPool) throws DatabaseException {
        return OrderMapper.getOrderSummariesByStatus(statusId, connectionPool);
    }

    public static Order getUnprocessedOrderById(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        return OrderMapper.getUnprocessedOrderById(orderId, connectionPool);
    }

    public static void updateOrderAndSendOffer(int orderId, int width, int length, String roof, String customerText, String adminText, double salesPrice, int statusId, ConnectionPool connectionPool) throws DatabaseException, IOException {

        System.out.println("OrderService - Status ID før opdatering: " + statusId);
        // Først tjekker vi, om ordren eksisterer
        Order existingOrder = getOrderById(orderId, connectionPool);

        if (existingOrder == null) {
            throw new DatabaseException("Ordre med ID " + orderId + " findes ikke og kan derfor ikke opdateres.");
        }

        // Opdaterer ordren, da vi nu er sikre på, at den eksisterer
        updateOrderForSeller(orderId, width, length, roof, customerText, adminText, salesPrice, statusId, connectionPool);

        // Hent kundens e-mail via CustomerService
        String customerEmail = CustomerService.getCustomerEmailById(existingOrder.getCustomerId(), connectionPool);

        if (customerEmail != null) {
            // Send email via EmailService
            EmailService.sendOfferEmail(existingOrder, customerEmail);
        }
    }

    public static Order getOrderAndCustomerInfoByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        return OrderMapper.getOrderAndCustomerInfoByOrderId(orderId, connectionPool);
    }

    //Beregner forslået salgspris ud fra dækningsgrad og samlede materialepris for en carport
    public static double calculateEstimatedSalesPrice(double coverageRate, double carportTotalPrice) {
        return carportTotalPrice * (1 + (coverageRate/100));
    }

    public static Order validateAndFetchOrder(String orderIdParam, ConnectionPool connectionPool) throws DatabaseException {
        try {
            int orderId = Integer.parseInt(orderIdParam);
            return getOrderById(orderId, connectionPool);
        } catch (NumberFormatException e) {
            throw new DatabaseException("Ugyldigt ordre-ID.", e);
        }
    }

}
