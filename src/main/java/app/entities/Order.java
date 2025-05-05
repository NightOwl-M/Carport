package app.entities;
import java.sql.Timestamp;

public class Order {
    private int orderId;
    private int userId;
    private int carportWidth;
    private int carportLength;
    private String roof;
    private String userText;
    private int status;
    private double salesPrice;
    private Timestamp createdAt;


    public Order(int orderId, int userId, int carportWidth, int carportLength, String roof, String userText, int status, double salesPrice, Timestamp createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.roof = roof;
        this.userText = userText;
        this.status = status;
        this.salesPrice = salesPrice;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCarportWidth() { return carportWidth; }
    public void setCarportWidth(int carportWidth) { this.carportWidth = carportWidth; }

    public int getCarportLength() { return carportLength; }
    public void setCarportLength(int carportLength) { this.carportLength = carportLength; }

    public String getRoof() { return roof; }
    public void setRoof(String roof) { this.roof = roof; }

    public String getUserText() { return userText; }
    public void setUserText(String userText) { this.userText = userText; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public double getSalesPrice() { return salesPrice; }
    public void setSalesPrice(double salesPrice) { this.salesPrice = salesPrice; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
