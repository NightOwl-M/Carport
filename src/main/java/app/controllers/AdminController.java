package app.controllers;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.service.order.OrderService;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.util.List;

public class AdminController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/admin/order/update", ctx -> updateOrder(ctx, connectionPool));
        app.post("/admin/order/status", ctx -> updateOrderStatus(ctx, connectionPool));

        app.get("/admin/orders/unprocessed", ctx -> showUnprocessedOrders(ctx, connectionPool));
        app.get("/admin/orders/pending", ctx -> showPendingOrders(ctx, connectionPool));
        app.get("/admin/orders/processed", ctx -> showProcessedOrders(ctx, connectionPool));

    }

     // Henter data fra formular.
     private static void updateOrder(Context ctx, ConnectionPool connectionPool) {
         try {
             int orderId = Integer.parseInt(ctx.formParam("orderId"));
             int width = Integer.parseInt(ctx.formParam("width"));
             int length = Integer.parseInt(ctx.formParam("length"));
             String roof = ctx.formParam("roof");
             String customerText = ctx.formParam("customerText");
             String adminText = ctx.formParam("adminText");
             double salesPrice = Double.parseDouble(ctx.formParam("salesPrice"));

             // Status 2 = Pending (efter tilbud er sendt)
             int statusId = 2;

             // **Opdater ordre og send tilbud via email**
             OrderService.updateOrderAndSendOffer(orderId, width, length, roof, customerText, adminText, salesPrice, statusId, connectionPool);

             ctx.redirect("/admindashboard.html");

         } catch (NumberFormatException e) {
             ctx.sessionAttribute("errorMessage", "Fejl i input. Tjek venligst dine talværdier.");
             ctx.redirect("/admin/order/update");

         } catch (DatabaseException | IOException e) {
             ctx.sessionAttribute("errorMessage", "Fejl ved opdatering/afsendelse af e-mail: " + e.getMessage());
             ctx.redirect("/admin/order/update");
         }
     }


    //Opdaterer status for en ordre.
    private static void updateOrderStatus(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            int statusId = Integer.parseInt(ctx.formParam("statusId"));

            // Kald til Service-laget
            OrderService.updateOrderStatus(orderId, statusId, connectionPool);
            ctx.redirect("/admin/orders");

        } catch (NumberFormatException e) {
            ctx.sessionAttribute("errorMessage", "Fejl i input. Tjek venligst dine talværdier.");
            ctx.redirect("/admin/order/status");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
            ctx.redirect("/admin/order/status");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Ukendt fejl: " + e.getMessage());
            ctx.redirect("/admin/order/status");
        }
    }

    // Viser alle unprocessed ordrer
    private static void showUnprocessedOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> orders = OrderService.getAllUnprocessedOrders(connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("unprocessed.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved hentning af unprocessed ordrer.");
            ctx.redirect("/admindashboard");
        }
    }

    // Viser alle pending ordrer
    private static void showPendingOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> orders = OrderService.getAllPendingOrders(connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("pending.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved hentning af pending ordrer.");
            ctx.redirect("/admindashboard");
        }
    }

    // Viser alle processed ordrer
    private static void showProcessedOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> orders = OrderService.getAllProcessedOrders(connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("processed.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved hentning af processed ordrer.");
            ctx.redirect("/admindashboard");
        }
    }

}
