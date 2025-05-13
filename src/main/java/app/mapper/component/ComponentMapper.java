package app.mapper.component;

import app.entities.Component;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.*;
import java.util.List;

public class ComponentMapper {
    public static void saveOrderComponentsToDB(List<Component> orderComponents, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO component (order_id, material_variant_id, quantity, use_description) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Component component : orderComponents) {
                System.out.println("Component orderId: " + component.getOrderId());
                ps.setInt(1, component.getOrderId());
                ps.setInt(2, component.getMaterialVariant().getMaterialVariantId());
                ps.setInt(3, component.getQuantity());
                ps.setString(4, component.getUseDescription());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl under indsættelse af components: " + e.getMessage(), e);
        }
    }
}
