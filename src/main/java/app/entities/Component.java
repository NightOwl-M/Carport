package app.entities;

public class Component {
    private int componentId;
    private int orderId;
    private int materialVariantId;
    private int quantity;
    private String useDescription;
    private MaterialVariant materialVariant;
    

    public Component(int componentId, int orderId, int materialVariantId, int quantity, String useDescription) {
        this.componentId = componentId;
        this.orderId = orderId;
        this.materialVariantId = materialVariantId;
        this.quantity = quantity;
        this.useDescription = useDescription;
    }


    public Component(int componentId, int orderId, int quantity, String useDescription, MaterialVariant materialVariant) {
        this.componentId = componentId;
        this.orderId = orderId;
        this.quantity = quantity;
        this.useDescription = useDescription;
        this.materialVariant = materialVariant;
    }

    public int getComponentId() { return componentId; }
    public void setComponentId(int componentId) { this.componentId = componentId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getMaterialVariantId() { return materialVariantId; }
    public void setMaterialVariantId(int materialVariantId) { this.materialVariantId = materialVariantId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUseDescription() { return useDescription; }
    public void setUseDescription(String useDescription) { this.useDescription = useDescription; }
}
