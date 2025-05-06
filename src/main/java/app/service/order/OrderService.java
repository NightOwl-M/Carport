
package app.service.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.mapper.order.OrderMapper;
import app.persistence.ConnectionPool;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class OrderService {

    public static Order createOrder(int userId, int width, int length, String roof, String userText, ConnectionPool connectionPool) throws DatabaseException {
        int status = 1; // Forespørgsel
        double price = 0.0; // Sættes senere
        Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());

        return OrderMapper.insertOrder(userId, width, length, roof, userText, status, price, createdAt, connectionPool);
    }
}
