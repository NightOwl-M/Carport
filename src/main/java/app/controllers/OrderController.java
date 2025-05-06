package app.controllers;

import app.service.svg.CarportSvg;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class OrderController {

    public static void addRoutes(Javalin app) {

        app.get("/", ctx -> ctx.render("form1.html"));

        // Trin 1 – formular til mål og SVG-visning
        app.get("/carport", ctx -> ctx.render("form1.html"));
        app.get("/api/carport-svg", OrderController::getCarportSvg);

        // Trin 2 – formular til personoplysninger
        app.post("/carport/info", OrderController::handleCarportInfo);
        app.get("/carport/info", ctx -> ctx.render("form2.html"));

        // Trin 3 – bekræftelsesside
        app.post("/carport/confirm", OrderController::handleConfirmation);
        app.get("/carport/confirm", OrderController::showConfirmationPage);
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
}
