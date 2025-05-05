package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.AdminController;
import app.controllers.OrderController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);
    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.jetty.modifyServletContextHandler(handler -> handler.setSessionHandler(SessionConfig.sessionConfig()));
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
        }).start(7070);

        // Tilføj ruter fra controllers
        OrderController.addRoutes(app, connectionPool);
        // Skfit så AdminController også tager connectionPool(); som paremeter men først når vi går i gang med den.
        AdminController.addRoutes(app);
    }
}
