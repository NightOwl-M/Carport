package app.controllers;

import app.persistence.ConnectionPool;
import app.service.order.OrderService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.post("/admin/order/update", ctx -> updateOrderForSeller(ctx, connectionPool));
        app.post("/admin/order/status", ctx -> updateOrderStatus(ctx, connectionPool));
    }


    private static void updateOrderForSeller(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            int width = Integer.parseInt(ctx.formParam("width"));
            int length = Integer.parseInt(ctx.formParam("length"));
            String roof = ctx.formParam("roof");
            String customerText = ctx.formParam("customerText");
            String adminText = ctx.formParam("adminText");
            double salesPrice = Double.parseDouble(ctx.formParam("salesPrice"));
            int statusId = Integer.parseInt(ctx.formParam("statusId"));

            OrderService.updateOrderForSeller(orderId, width, length, roof, customerText, adminText, salesPrice, statusId, connectionPool);
            ctx.redirect("/admin/orders");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Fejl ved opdatering af ordre: " + e.getMessage());
            ctx.redirect("/admin/order/update");
        }
    }

    private static void updateOrderStatus(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            int statusId = Integer.parseInt(ctx.formParam("statusId"));

            OrderService.updateOrderStatus(orderId, statusId, connectionPool);
            ctx.redirect("/admin/orders");

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Fejl ved opdatering af status: " + e.getMessage());
            ctx.redirect("/admin/order/status");
        }
    }
}
