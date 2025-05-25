package app.entities;

import java.util.Objects;

public class Material {
    private int materialId;
    private String name;
    private String unit;
    private double price;

    public Material(int materialId, String name, String unit, double price) {
        this.materialId = materialId;
        this.name = name;
        this.unit = unit;
        this.price = price;
    }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return materialId == material.materialId && Double.compare(price, material.price) == 0 && Objects.equals(name, material.name) && Objects.equals(unit, material.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId, name, unit, price);
    }
}
