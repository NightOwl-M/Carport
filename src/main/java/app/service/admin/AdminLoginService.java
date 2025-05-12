package app.service.admin;

import app.exceptions.DatabaseException;
import app.mapper.admin.AdminLoginMapper;
import app.persistence.ConnectionPool;

public class AdminLoginService {

    public static boolean checkAdminLogin(String username, String password, ConnectionPool connectionPool) throws DatabaseException {
        return AdminLoginMapper.checkAdminLoginCredentials(username, password, connectionPool);
    }
}
