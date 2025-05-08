package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.persistence.ConnectionPool;
import app.service.order.OrderService;
import app.service.svg.CarportSvg;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {

        app.get("/", ctx -> ctx.render("form1.html"));
        app.get("/carport", ctx -> ctx.render("form1.html"));
        app.get("/api/carport-svg", OrderController::getCarportSvg);
        app.post("/carport/info", OrderController::handleCarportInfo);
        app.get("/carport/info", ctx -> ctx.render("form2.html"));
        app.post("/carport/confirm", OrderController::handleConfirmation);
        app.get("/carport/confirm", OrderController::showConfirmationPage);

        app.post("/carport/confirm/save", ctx -> saveOrderToDatabase(ctx, connectionPool));
    }


    // 👉 SVG til AJAX
    public static void getCarportSvg(Context ctx) {
        int width = ctx.queryParamAsClass("width", Integer.class).getOrDefault(300);
        int length = ctx.queryParamAsClass("length", Integer.class).getOrDefault(600);

        CarportSvg carport = new CarportSvg(width, length);
        carport.addBeams();
        carport.addText();

        ctx.result(carport.toString());
    }

    // Når customer trykker "Næste" i form1.html
    public static void handleCarportInfo(Context ctx) {
        int width = Integer.parseInt(ctx.formParam("width"));
        int length = Integer.parseInt(ctx.formParam("length"));
        String userText = ctx.formParam("user-text");
        String roofType = ctx.formParam("roof");

        Order currentOrder = new Order(width, length, userText, roofType); //TODO check rækkefølge og eventuelt overload constructor
        ctx.sessionAttribute("currentOrder", currentOrder);

        /*
        ctx.sessionAttribute("width", width);
        ctx.sessionAttribute("length", length);
        ctx.sessionAttribute("userText", userText);
        ctx.sessionAttribute("roofType", roofType);
         */

        ctx.render("form2.html");
    }

    // Når customer trykker "Bekræft" i form2.html
    public static void handleConfirmation(Context ctx) {
        String name = ctx.formParam("name");
        String address = ctx.formParam("address");
        int zipCode = Integer.parseInt(ctx.formParam("zip-code"));
        String email = ctx.formParam("email");
        String phone = ctx.formParam("phone");

        User currentUser = new User (name, address, zipCode, email, phone); //TODO check rækkefølge og eventuelt overload constructor
        ctx.sessionAttribute("currentUser", currentUser);

        /*
        ctx.sessionAttribute("name", name);
        ctx.sessionAttribute("email", email);
        ctx.sessionAttribute("address", address);
         */


        ctx.redirect("/carport/confirm");
    }

    // Viser bekræftelsesside med SVG og alle oplysninger
    public static void showConfirmationPage(Context ctx) {
        User currentUser = ctx.sessionAttribute("currentUser");
        Order currentOrder = ctx.sessionAttribute("currentOrder");

        /*
        int width = ctx.sessionAttribute("width");
        int length = ctx.sessionAttribute("length");
        String name = ctx.sessionAttribute("name");
        String email = ctx.sessionAttribute("email");
        String address = ctx.sessionAttribute("address");
         */

        CarportSvg carport = new CarportSvg(currentOrder.getCarportWidth(), currentOrder.getCarportLength());
        carport.addBeams();
        carport.addText();

        ctx.attribute("svg", carport.toString());

        /*
        ctx.attribute("name", name);
        ctx.attribute("email", email);
        ctx.attribute("address", address);
         */


        // Kalder bekræftelsessiden (form3.html)
        ctx.render("form3.html");
    }


        public static void createOrder(Context ctx, ConnectionPool connectionPool) {
            try {
                int width = Integer.parseInt(ctx.formParam("width"));
                int length = Integer.parseInt(ctx.formParam("length"));
                String roof = ctx.formParam("roof");
                String userText = ctx.formParam("comment");

                User currentUser = ctx.sessionAttribute("currentUser");

                Order newOrder = OrderService.createOrder(
                        currentUser.getUserId(),
                        width,
                        length,
                        roof,
                        userText,
                        connectionPool
                );

                ctx.sessionAttribute("currentOrder", newOrder);
                ctx.redirect("/step2");

            } catch (Exception e) {
                ctx.sessionAttribute("errorMessage", "Fejl ved oprettelse af ordre: " + e.getMessage());
                ctx.redirect("/createorder");
            }
        }

    public static void saveOrderToDatabase(Context ctx, ConnectionPool connectionPool) {
        try {
            // Hent oplysninger fra session
            int width = ctx.sessionAttribute("width");
            int length = ctx.sessionAttribute("length");
            String name = ctx.sessionAttribute("name");
            String email = ctx.sessionAttribute("email");
            String address = ctx.sessionAttribute("address");

            String roof = "standard";
            String userText = "Ingen kommentar";

            User currentUser = ctx.sessionAttribute("currentUser");

            // Gem ordren via service-lag
            Order newOrder = OrderService.createOrder(
                    currentUser.getUserId(),
                    width,
                    length,
                    roof,
                    userText,
                    connectionPool
            );

            // Gem ny ordre i session og redirect
            ctx.sessionAttribute("currentOrder", newOrder);
            ctx.redirect("/thankyou");

        } catch (Exception e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved oprettelse af ordre: " + e.getMessage());
            ctx.redirect("/carport/confirm");
        }
    }
/*
    public static void saveCustomerToDatabase (Context ctx, ConnectionPool connectionPool) {
        try {
            // Hent oplysninger fra session
            int width = ctx.sessionAttribute("width");
            int length = ctx.sessionAttribute("length");
            String name = ctx.sessionAttribute("name");
            String email = ctx.sessionAttribute("email");
            String address = ctx.sessionAttribute("address");

            String roof = "standard";
            String userText = "Ingen kommentar";

            User currentUser = ctx.sessionAttribute("currentUser");

            // Gem ordren via service-lag
            Customer newCustomer = CustomerService.createCustomer(
                    currentUser.getUserId(),
                    width,
                    length,
                    roof,
                    userText,
                    connectionPool
            );

            // Gem ny ordre i session og redirect
            ctx.sessionAttribute("currentOrder", newOrder);
            ctx.redirect("/thankyou");

        } catch (Exception e) {
            ctx.sessionAttribute("errorMessage", "Fejl ved oprettelse af ordre: " + e.getMessage());
            ctx.redirect("/carport/confirm");
        }
    }

 */
}