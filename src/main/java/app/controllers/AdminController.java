package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.service.order.OrderService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/admin/order/update", ctx -> updateOrder(ctx, connectionPool));
        app.post("/admin/order/status", ctx -> updateOrderStatus(ctx, connectionPool));
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
            int statusId = Integer.parseInt(ctx.formParam("statusId"));

            // Kald til Service-laget
            OrderService.updateOrderForSeller(orderId, width, length, roof, customerText, adminText, salesPrice, statusId, connectionPool);
            ctx.redirect("/admin/orders");

        } catch (NumberFormatException e) {
            ctx.sessionAttribute("errorMessage", "Fejl i input. Tjek venligst dine talværdier.");
            ctx.redirect("/admin/order/update");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
            ctx.redirect("/admin/order/update");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Ukendt fejl: " + e.getMessage());
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
}
