package app.service.order;

import app.entities.Component;
import app.entities.Customer;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.mapper.order.OrderMapper;
import app.persistence.ConnectionPool;
import app.service.calculator.CarportCalculatorService;
import app.service.email.EmailService;
import app.service.customer.CustomerService;

import java.io.IOException;
import java.util.List;


public class OrderService {

    public static void createOrderAndCustomer(
            String name, String email, String address, Integer zipCode, String phone,
            Integer width, Integer length, String roof, String customerText,
            ConnectionPool connectionPool) throws DatabaseException {

        // Validering
        if (name == null || email == null || address == null || zipCode == null || phone == null ||
                width == null || length == null || roof == null || customerText == null) {
            throw new DatabaseException("En eller flere ordreoplysninger mangler. Prøv igen.");
        }

        try {
            // Opret kunden først
            Customer customer = CustomerService.saveSessionCustomer(name, email, address, zipCode, phone, connectionPool);

            // Opret ordre
            saveSessionOrder(customer.getCustomerId(), width, length, roof, customerText, connectionPool);

        } catch (Exception e) {
            throw new DatabaseException("Fejl under ordreoprettelse: " + e.getMessage(), e);
        }
    }

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

        // Opdaterer ordren
        updateOrderForSeller(orderId, width, length, roof, customerText, adminText, salesPrice, statusId, connectionPool);

        // Henter den opdaterede ordrdr
        Order updatedOrder = getOrderById(orderId, connectionPool);

        // Hent kundens e-mail via CustomerService
        String customerEmail = CustomerService.getCustomerEmailById(updatedOrder.getCustomerId(), connectionPool);

        if (customerEmail != null) {
            // Send email via EmailService
            EmailService.sendOfferEmail(updatedOrder, customerEmail);
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

    public static double calculateMaterialCost(Order currentOrderSalesmanInput, ConnectionPool connectionPool) throws DatabaseException {
        CarportCalculatorService calculator = new CarportCalculatorService(currentOrderSalesmanInput.getCarportLength(), currentOrderSalesmanInput.getCarportWidth(), connectionPool);
        List<Component> orderComponents = calculator.getCarportBOM(currentOrderSalesmanInput);
        return calculator.calculateCarportMaterialCost(orderComponents);
    }
}
