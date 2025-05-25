package app.entities;

import java.util.Objects;

public class MaterialVariant {
    private int materialVariantId;
    private int materialId;
    private int length;
    private Material material;


    public MaterialVariant(int materialVariantId, int materialId, int length) {
        this.materialVariantId = materialVariantId;
        this.materialId = materialId;
        this.length = length;
    }

    public MaterialVariant(int materialVariantId, int length, Material material) {
        this.materialVariantId = materialVariantId;
        this.material = material;
        this.length = length;
    }

    public int getMaterialVariantId() { return materialVariantId; }
    public void setMaterialVariantId(int materialVariantId) { this.materialVariantId = materialVariantId; }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }

    public Material getMaterial() {
        return material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialVariant that = (MaterialVariant) o;
        return materialVariantId == that.materialVariantId && materialId == that.materialId && length == that.length && Objects.equals(material, that.material);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialVariantId, materialId, length, material);
    }
}
