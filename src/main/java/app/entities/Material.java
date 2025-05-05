package app.entities;

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
}
