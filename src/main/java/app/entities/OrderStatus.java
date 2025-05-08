package app.entities;

public class OrderStatus {
    private int statusId;
    private String status;

    public OrderStatus(int statusId, String status) {
        this.statusId = statusId;
        this.status = status;
    }

    public int getStatusId() { return statusId; }
    public String getStatus() { return status; }
}
