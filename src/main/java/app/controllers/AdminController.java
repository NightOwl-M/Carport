package app.controllers;

import app.entities.Component;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.service.admin.AdminLoginService;
import app.service.component.ComponentService;
import app.service.order.CarportCalculatorService;
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
        app.get("/admin/orders/unprocessed/{orderId}", ctx -> showUnprocessedOrder(ctx, connectionPool));

        app.get("/admin/orders/pending", ctx -> showPendingOrders(ctx, connectionPool));
        app.get("/admin/orders/pending/{orderId}", ctx -> showPendingOrder(ctx, connectionPool));

        app.get("/admin/orders/processed", ctx -> showProcessedOrders(ctx, connectionPool));
        app.get("/admin/orders/processed/{orderId}", ctx -> showProcessedOrder(ctx, connectionPool));


        app.post("/admin/login", ctx -> adminLogin(ctx, connectionPool));
        app.get("/adminlogin", ctx -> ctx.render("adminlogin.html"));
        app.get("/admindashboard", AdminController::checkAdminLogin);


        //Viser offerpage med ordredata
        app.get("/offerpage", ctx -> {
            String orderIdParam = ctx.queryParam("orderId");

            if (orderIdParam == null) {
                ctx.sessionAttribute("errorMessage", "Ordre ID mangler.");
                ctx.redirect("/admin/orders/unprocessed");
                return;
            }

            try {
                int orderId = Integer.parseInt(orderIdParam);
                Order order = OrderService.getOrderAndCustomerInfoByOrderId(orderId, connectionPool);

                if (order == null) {
                    ctx.sessionAttribute("errorMessage", "Ordren eksisterer ikke.");
                    ctx.redirect("/admin/orders/unprocessed");
                    return;
                }

                // Gem ordren i sessionen
                ctx.sessionAttribute("currentOrder", order);
                ctx.render("offerPage.html");

            } catch (NumberFormatException e) {
                ctx.sessionAttribute("errorMessage", "Ugyldigt ordre ID.");
                ctx.redirect("/admin/orders/unprocessed");
            } catch (DatabaseException e) {
                ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
                ctx.redirect("/admin/orders/unprocessed");
            }
        });

        //Når man trykker på "videre"
        app.post("/offerpage/generate-offer", ctx -> showOfferPageConfirmation(ctx, connectionPool));

        app.get("/offerpage", ctx -> showOfferPage(ctx, connectionPool));
        //Når man trykker på "beregn pris"
        app.post("/offerpage/show-prices", ctx -> showPrices(ctx, connectionPool));

        //Når admin trykker på "se stykliste" //TODO bruges hvis vi vil have at styklisten indlæses på en ny html-side og ikke på offerpage.html
        app.get("/offerpage/show-bom", ctx -> showBomPage(ctx, connectionPool));
        //Når admin trykker på "send tilbud
        app.post("/offerpage/send-offer", ctx -> sendOffer(ctx, connectionPool));

    }

    private static void adminLogin(Context ctx, ConnectionPool connectionPool) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            boolean isAdmin = AdminLoginService.checkAdminLogin(username, password, connectionPool);

            if (isAdmin) {
                ctx.sessionAttribute("isAdmin", true);
                ctx.redirect("/admindashboard");
            } else {
                ctx.sessionAttribute("errorMessage", "Forkert admin-brugernavn eller password.");
                ctx.redirect("/adminlogin.html");
            }

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
            ctx.redirect("/adminlogin.html");
        }
    }

    private static void checkAdminLogin(Context ctx) {
        Boolean isAdmin = ctx.sessionAttribute("isAdmin");

        if (isAdmin == null || !isAdmin) {
            ctx.redirect("/adminlogin.html");
        } else {
            ctx.render("admindashboard.html");
        }
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

    private static void updateOrderStatus(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = Integer.parseInt(ctx.formParam("orderId"));
            int statusId = Integer.parseInt(ctx.formParam("statusId"));

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

    private static void showUnprocessedOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> orders = OrderService.getOrderSummariesByStatus(1, connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("unprocessed.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved hentning af unprocessed ordrer.");
            ctx.redirect("/admindashboard");
        }
    }

    private static void showUnprocessedOrder(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(ctx.pathParam("orderId"));

        try {
            Order order = OrderService.getUnprocessedOrderById(orderId, connectionPool);
            ctx.attribute("order", order);
            ctx.render("unprocessedorder.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved hentning af unprocessed ordre.");
            ctx.redirect("/admin/orders/unprocessed");
        }
    }

    private static void showPendingOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> orders = OrderService.getOrderSummariesByStatus(2, connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("pending.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved hentning af pending ordrer.");
            ctx.redirect("/admindashboard");
        }
    }

    private static void showPendingOrder(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(ctx.pathParam("orderId"));

        try {
            Order order = OrderService.getOrderAndCustomerInfoByOrderId(orderId, connectionPool);
            ctx.attribute("order", order);
            ctx.render("pendingorder.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved hentning af ordre.");
            ctx.redirect("/admin/orders/pending");
        }
    }

    private static void showProcessedOrders(Context ctx, ConnectionPool connectionPool) {
        try {
            List<Order> orders = OrderService.getOrderSummariesByStatus(3, connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("processed.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved hentning af processed ordrer.");
            ctx.redirect("/admindashboard");
        }
    }

    private static void showProcessedOrder(Context ctx, ConnectionPool connectionPool) {
        int orderId = Integer.parseInt(ctx.pathParam("orderId"));

        try {
            Order order = OrderService.getOrderAndCustomerInfoByOrderId(orderId, connectionPool);
            ctx.attribute("order", order);
            ctx.render("processedorder.html");

        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved hentning af ordre.");
            ctx.redirect("/admin/orders/processed");
        }
    }

    //Kaldes når sælger trykker på "vælg" på en unprocessed order
    private static void showOfferPage(Context ctx, ConnectionPool connectionPool) {
        try {
            int orderId = 2; //TODO orderId hardcoded, skal hentes fra sessionen, når admin vælger en ordre at skulle bearbejde
            Order currentOrder = OrderService.getOrderAndCustomerInfoByOrderId(orderId, connectionPool);

            ctx.sessionAttribute("currentOrder", currentOrder);
            ctx.render("offerpage.html");
        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
            ctx.redirect(""); //TODO

        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Ukendt fejl: " + e.getMessage());
            ctx.redirect(""); //TODO
        }
    }

    //Kaldes når der trykkes på "videre"
    private static void showOfferPageConfirmation(Context ctx, ConnectionPool connectionPool) {
        try {
            Order currentOrder = ctx.sessionAttribute("currentOrder");

            //Nye mål på carport sat af sælger hentes og laves til ny order-objekt
            int carportLength = Integer.parseInt(ctx.formParam("length"));
            int carportWidth = Integer.parseInt(ctx.formParam("width"));
            String roof = ctx.formParam("roof");
            String adminText = ctx.formParam("admin-text");

            Order currentOrderSalesmanInput = new Order(carportWidth, carportLength, roof, adminText); //TODO Der findes ikke en konstruktør med adminText til sidst
            currentOrderSalesmanInput.setOrderId(currentOrder.getOrderId());

            //Beregning af pris
            double coverageRate = Double.parseDouble(ctx.formParam("coverage-rate"));
            double materialCostPrice = 20000; //TODO hardcoded indtil videre //TODO Mangler metode der beregner carportens samlede materialepris
            double estimatedSalesPrice = OrderService.calculateEstimatedSalesPrice(coverageRate, materialCostPrice);

            ctx.sessionAttribute("coverageRate", coverageRate);
            ctx.sessionAttribute("materialCostPrice", materialCostPrice);
            ctx.sessionAttribute("estimatedSalesPrice", estimatedSalesPrice);
            ctx.sessionAttribute("currentOrderSalesmanInput", currentOrderSalesmanInput);

            ctx.render("offerpageconfirmation.html");
            /*
        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
            ctx.redirect(""); //TODO
             */
        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Ukendt fejl: " + e.getMessage());
            ctx.redirect(""); //TODO
        }
    }

    private static void showBomPage(Context ctx, ConnectionPool connectionPool) {
        try {
            Order currentOrderSalesmanInput = ctx.sessionAttribute("currentOrderSalesmanInput");
            List<Component> orderComponents = ComponentService.calculateBom(currentOrderSalesmanInput, connectionPool);

            ctx.sessionAttribute("orderComponents", orderComponents);
            ctx.render("bompage.html");
        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
            ctx.redirect(""); //TODO
        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Ukendt fejl: " + e.getMessage());
            ctx.redirect(""); //TODO
        }
    }

    private static void sendOffer(Context ctx, ConnectionPool connectionPool) {
        try {
            Order currentOrder = ctx.sessionAttribute("currentOrder");
            List<Component> orderComponents = ctx.sessionAttribute("orderComponents");

            //TODO kald metode der ændrer på ordrestatus og ændre orderStatus i DB

            //TODO kald på metode der indsætter components i DB
           //Orderservice.insertOrderComponentsIntoDB(orderComponents, currentOrder.getOrderId(), connectionPool);

            //TODO nulstil sessionAttributes

            ctx.render("admindashboard.html"); //TODO hvor skal sælger hen efter sendt tilbud?
            /*
        } catch (DatabaseException e) {
            ctx.sessionAttribute("errorMessage", "Databasefejl: " + e.getMessage());
            ctx.redirect(""); //TODO
             */
        } catch (Exception e) {
            e.printStackTrace();
            ctx.sessionAttribute("errorMessage", "Ukendt fejl: " + e.getMessage());
            ctx.redirect(""); //TODO
        }
    }
}
