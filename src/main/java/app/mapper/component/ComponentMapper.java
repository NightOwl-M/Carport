package app.mapper.component;
import app.entities.Component;
import app.entities.Material;
import app.entities.MaterialVariant;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComponentMapper {
    public static void saveOrderComponentsToDB(List<Component> orderComponents, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO component (order_id, material_variant_id, quantity, use_description) VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Component component : orderComponents) {
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

    public static List<Component> getAllComponentsByOrderId (int orderId, ConnectionPool connectionPool) throws DatabaseException {
        List<Component> allComponents = new ArrayList<>();

        String sql = "SELECT * FROM component\n" +
                "JOIN material_variant USING (material_variant_id)\n" +
                "JOIN material USING (material_id)\n" +
                "WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                //Material
                int materialId = rs.getInt("material_id");
                String name = rs.getString("name");
                String unit = rs.getString("unit");
                double price = rs.getDouble("price");
                Material material = new Material(materialId, name, unit, price);

                //MaterialVariant
                int materialVariantId = rs.getInt("material_variant_id");
                int length = rs.getInt("length");
                MaterialVariant materialVariant = new MaterialVariant(materialVariantId, length, material);

                //Component
                int component_id = rs.getInt("component_id");
                int quantity = rs.getInt("quantity");
                String use_description = rs.getString("use_description");
                Component component = new Component(component_id, orderId, quantity, use_description, materialVariant);

                allComponents.add(component);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af components med ordreId: " + orderId, e);
        }
        return allComponents;
    }
}
