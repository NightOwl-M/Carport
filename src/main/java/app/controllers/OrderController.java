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

        // Ruter til betaling
        app.get("/pay/{orderId}", ctx -> showPaymentPage(ctx, connectionPool));
        app.post("/pay/confirm", ctx -> confirmPayment(ctx, connectionPool));

    }

    // Generer SVG basseret på på width og length.
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

    // Indhenter input fra form1 og gemmer dem som session attributes.
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

    // Håndterer og gemmer personoplysninger som session attributes.
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

            ctx.sessionAttribute("name", name);
            ctx.sessionAttribute("address", address);
            ctx.sessionAttribute("zipCode", zipCode);
            ctx.sessionAttribute("email", email);
            ctx.sessionAttribute("phone", phone);

            ctx.redirect("/carport/confirm");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Fejl under bekræftelse: " + e.getMessage());
            ctx.redirect("/carport/info");
        }
    }

    // Henter alle tidligere session attributer som var blevet gemt.
    // Opretter midlertidlige instanser af Order og Customer.
    // Disse instanser er oprettet udelukkende til visningsformål - derfor sættes de med 0.
    // derefter sættes de som attributer, så de kan tilgås af Thymeleaf.
    public static void showConfirmationPage(Context ctx) {
        Integer width = ctx.sessionAttribute("width");
        Integer length = ctx.sessionAttribute("length");
        String roof = ctx.sessionAttribute("roof");
        String customerText = ctx.sessionAttribute("customerText");

        String name = ctx.sessionAttribute("name");
        String address = ctx.sessionAttribute("address");
        Integer zipCode = ctx.sessionAttribute("zipCode");
        String email = ctx.sessionAttribute("email");
        String phone = ctx.sessionAttribute("phone");

        if (width == null || length == null || roof == null || customerText == null ||
                name == null || address == null || zipCode == null || email == null || phone == null) {
            ctx.sessionAttribute("errorMessage", "Ordreoplysninger mangler. Prøv igen.");
            ctx.redirect("/carport/info");
            return;
        }

        Order tempOrder = new Order(0, width, length, roof, customerText);
        Customer tempCustomer = new Customer(0, name, email, address, zipCode, phone);

        // Sæt dem som attributter
        ctx.attribute("currentOrder", tempOrder);
        ctx.attribute("currentCustomer", tempCustomer);

        // Generer SVG
        CarportSvg carport = new CarportSvg(width, length);
        carport.addBeams();
        carport.addText();
        ctx.attribute("svg", carport.toString());

        ctx.render("form3.html");
    }

    // Henter alle sessionAttributerne.
    // Derefter opretter vi customer først hvorefter vi så kan bruge customerId i orderen.
    public static void saveOrderToDatabase(Context ctx, ConnectionPool connectionPool) {
        try {
            Integer width = ctx.sessionAttribute("width");
            Integer length = ctx.sessionAttribute("length");
            String roof = ctx.sessionAttribute("roof");
            String customerText = ctx.sessionAttribute("customerText");

            String name = ctx.sessionAttribute("name");
            String address = ctx.sessionAttribute("address");
            Integer zipCode = ctx.sessionAttribute("zipCode");
            String email = ctx.sessionAttribute("email");
            String phone = ctx.sessionAttribute("phone");

            if (width == null || length == null || roof == null || customerText == null ||
                    name == null || address == null || zipCode == null || email == null || phone == null) {
                ctx.sessionAttribute("errorMessage", "Ordreoplysninger mangler. Prøv igen.");
                ctx.redirect("/carport/confirm");
                return;
            }

            Customer customer = CustomerService.saveSessionCustomer(name, email, address, zipCode, phone, connectionPool);
            Order order = OrderService.saveSessionOrder(customer.getCustomerId(), width, length, roof, customerText, connectionPool);

            // Ryd sessionen efter gemning
            ctx.req().getSession().invalidate();

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

    private static void showPaymentPage(Context ctx, ConnectionPool connectionPool) {
        String orderIdParam = ctx.queryParam("orderId");

        if (orderIdParam == null) {
            ctx.sessionAttribute("errorMessage", "Ordre-ID mangler.");
            ctx.redirect("/");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdParam);
            Order order = OrderService.validateAndFetchOrder(orderIdParam, connectionPool);

            if (order == null) {
                ctx.sessionAttribute("errorMessage", "Ordre ikke fundet.");
                ctx.redirect("/");
                return;
            }

            ctx.attribute("currentOrder", order);
            ctx.render("pay.html");

        } catch (NumberFormatException e) {
            ctx.sessionAttribute("errorMessage", "Ugyldigt ordre-ID.");
            ctx.redirect("/");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
            ctx.redirect("/");
        }
    }

    private static void confirmPayment(Context ctx, ConnectionPool connectionPool) {
        String orderIdParam = ctx.pathParam("orderId");

        try {
            int orderId = Integer.parseInt(orderIdParam);

            // Opdaterer ordrestatus til "Processed" (statusId = 3)
            OrderService.updateOrderStatus(orderId, 3, connectionPool);

            ctx.redirect("/payed.html");

        } catch (NumberFormatException e) {
            ctx.sessionAttribute("errorMessage", "Ugyldigt ordre-ID.");
            ctx.redirect("/pay/" + orderIdParam);

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved opdatering af ordrestatus.");
            ctx.redirect("/pay/" + orderIdParam);
        }
    }
}
