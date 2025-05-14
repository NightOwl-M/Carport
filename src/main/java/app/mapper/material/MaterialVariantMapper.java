package app.mapper.material;

import app.entities.Component;
import app.entities.Material;
import app.entities.MaterialVariant;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialVariantMapper {

    public static List<MaterialVariant> getMaterialVariantsByIdAndMinLength(int materialId, int minLength, ConnectionPool connectionPool) throws DatabaseException {
        List<MaterialVariant> materialVariants = new ArrayList<>();

        String sql = "SELECT * FROM material_variant\n" +
                "JOIN material USING (material_id)\n" +
                "WHERE material_id = ? AND length >= ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.setInt(2, minLength);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                //Material
                String name = rs.getString("name");
                String unit = rs.getString("unit");
                double price = rs.getDouble("price");
                Material material = new Material(materialId, name, unit, price);

                //MaterialVariant
                int materialVariantId = rs.getInt("material_variant_id");
                int length = rs.getInt("length");
                MaterialVariant materialVariant = new MaterialVariant(materialVariantId, length, material);

                materialVariants.add(materialVariant);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af MaterialVariants: " + e);
        }
        return materialVariants;
    }
}
