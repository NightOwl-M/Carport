package app.entities;

import java.sql.Timestamp;

public class Svg {
    private int svgId;
    private int orderId;
    private String svgData;
    private Timestamp createdAt;

    public Svg(int svgId, int orderId, String svgData, Timestamp createdAt) {
        this.svgId = svgId;
        this.orderId = orderId;
        this.svgData = svgData;
        this.createdAt = createdAt;
    }

    public int getSvgId() { return svgId; }
    public int getOrderId() { return orderId; }
    public String getSvgData() { return svgData; }
    public Timestamp getCreatedAt() { return createdAt; }
}
