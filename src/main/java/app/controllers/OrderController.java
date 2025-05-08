package app.controllers;

import app.entities.Customer;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.service.order.CarportSvg;
import app.service.customer.CustomerService;
import app.service.order.OrderService;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/", ctx -> ctx.render("form1.html"));
        app.get("/carport", ctx -> ctx.render("form1.html"));
        app.get("/api/carport-svg", ctx -> getCarportSvg(ctx));
        app.post("/carport/info", ctx -> handleCarportInfo(ctx));
        app.get("/carport/info", ctx -> ctx.render("form2.html"));
        app.post("/carport/confirm", ctx -> handleConfirmation(ctx, connectionPool));
        app.get("/carport/confirm", ctx -> showConfirmationPage(ctx));
        app.post("/carport/confirm/save", ctx -> saveOrderToDatabase(ctx, connectionPool));
    }

    public static void getCarportSvg(Context ctx) {
        int width = ctx.queryParamAsClass("width", Integer.class).getOrDefault(300);
        int length = ctx.queryParamAsClass("length", Integer.class).getOrDefault(600);

        CarportSvg carport = new CarportSvg(width, length);
        carport.addBeams();
        carport.addRafters();
        carport.addPost();
        carport.addText();

        ctx.contentType("text/html");
        ctx.result(carport.toString());
    }

    public static void handleCarportInfo(Context ctx) {
        int width = Integer.parseInt(ctx.formParam("width"));
        int length = Integer.parseInt(ctx.formParam("length"));
        String roofType = ctx.formParam("roof");
        String customerText = ctx.formParam("user-text");

        ctx.sessionAttribute("width", width);
        ctx.sessionAttribute("length", length);
        ctx.sessionAttribute("roof", roofType);
        ctx.sessionAttribute("customerText", customerText);

        ctx.render("form2.html");
    }

    public static void handleConfirmation(Context ctx, ConnectionPool connectionPool) {
        try {
            String name = ctx.formParam("name");
            String address = ctx.formParam("address");
            String zipCodeParam = ctx.formParam("zip-code");
            String email = ctx.formParam("email");
            String phone = ctx.formParam("phone");

            if (name == null || address == null || zipCodeParam == null || email == null || phone == null) {
                ctx.sessionAttribute("errorMessage", "Alle felter skal udfyldes.");
                ctx.redirect("/carport/info");
                return;
            }

            int zipCode;
            try {
                zipCode = Integer.parseInt(zipCodeParam);
            } catch (NumberFormatException e) {
                ctx.sessionAttribute("errorMessage", "Postnummer skal være et tal.");
                ctx.redirect("/carport/info");
                return;
            }

            int width = ctx.sessionAttribute("width");
            int length = ctx.sessionAttribute("length");
            String roof = ctx.sessionAttribute("roof");
            String customerText = ctx.sessionAttribute("customerText");

            Customer customer = CustomerService.createCustomer(name, email, address, zipCode, phone, connectionPool);
            Order order = OrderService.createOrder(customer.getCustomerId(), width, length, roof, customerText, connectionPool);

            ctx.sessionAttribute("currentOrder", order);
            ctx.sessionAttribute("currentCustomer", customer);
            ctx.redirect("/carport/confirm");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Fejl under oprettelse af ordre: " + e.getMessage());
            ctx.redirect("/carport/info");
        }
    }

    public static void showConfirmationPage(Context ctx) {
        Order order = ctx.sessionAttribute("currentOrder");
        Customer customer = ctx.sessionAttribute("currentCustomer");

        if (order == null || customer == null) {
            ctx.sessionAttribute("errorMessage", "Ordreoplysninger mangler. Prøv igen.");
            ctx.redirect("/carport/info");
            return;
        }

        CarportSvg carport = new CarportSvg(order.getCarportWidth(), order.getCarportLength());
        carport.addBeams();
        carport.addText();

        ctx.attribute("svg", carport.toString());
        ctx.attribute("currentOrder", order);
        ctx.attribute("currentCustomer", customer);

        ctx.render("form3.html");
    }


    public static void saveOrderToDatabase(Context ctx, ConnectionPool connectionPool) {
        try {
            OrderService.saveSessionOrder(ctx, connectionPool);
            ctx.render("thankyoupage.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
            ctx.redirect("/carport/confirm");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Ukendt fejl: " + e.getMessage());
            ctx.redirect("/carport/confirm");
        }
    }

    public static void getOrderByOrderId(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            Order order = OrderService.getOrderById(orderId, connectionPool);
            if (order != null) {
                ctx.json(order);
            } else {
                ctx.status(404).result("Order not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid order ID");
        } catch (DatabaseException e) {
            ctx.status(500).result("Error fetching order: " + e.getMessage());
        }
    }


}
