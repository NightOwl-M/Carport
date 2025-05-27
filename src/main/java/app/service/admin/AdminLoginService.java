package app.service.admin;

import app.exceptions.DatabaseException;
import app.mapper.admin.AdminLoginMapper;
import app.persistence.ConnectionPool;
import io.javalin.http.Context;

public class AdminLoginService {

    public static String handleAdminLogin(String username, String password, Context ctx, ConnectionPool connectionPool) throws DatabaseException {
        boolean isAdmin = AdminLoginMapper.checkAdminLoginCredentials(username, password, connectionPool);

        if (isAdmin) {
            ctx.sessionAttribute("isAdmin", true);
            return "/admindashboard";
        } else {
            ctx.sessionAttribute("errorMessage", "Forkert admin-brugernavn eller password.");
            return "/adminlogin.html";
        }
    }
}
