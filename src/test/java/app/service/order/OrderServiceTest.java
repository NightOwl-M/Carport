package app.service.order;

import app.entities.Order;
import app.exceptions.DatabaseException;
import app.mapper.order.OrderMapper;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Test
    void createOrder_returnsExpectedOrder() throws DatabaseException {
        // Arrange
        int userId = 1;
        int width = 300;
        int length = 600;
        String roof = "Plastmo";
        String userText = "Testkommentar";
        int status = 1;
        double price = 0.0;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        ConnectionPool mockPool = mock(ConnectionPool.class);
        Order expectedOrder = new Order(1, userId, width, length, roof, userText, status, price, timestamp);

        try (MockedStatic<OrderMapper> mockedMapper = mockStatic(OrderMapper.class)) {
            mockedMapper.when(() ->
                    OrderMapper.insertOrder(
                            eq(userId), eq(width), eq(length), eq(roof), eq(userText),
                            eq(status), eq(price), any(), eq(mockPool))
            ).thenReturn(expectedOrder);

            // Act
            Order result = OrderService.createOrder(userId, width, length, roof, userText, mockPool);

            // Assert
            assertNotNull(result);
            assertEquals(expectedOrder, result);
        }
    }
}
