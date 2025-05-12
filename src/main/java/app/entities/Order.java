package app.entities;

import java.sql.Timestamp;

public class Order {
    private int orderId;
    private int customerId;
    private int carportWidth;
    private int carportLength;
    private String roof;
    private String customerText;
    private String adminText;
    private int statusId;
    private double salesPrice;
    private Timestamp createdAt;
    private String customerEmail;

    /**
     * Constructor til midlertidig lagring i sessionen (uden customerId).
     */
    public Order(int carportWidth, int carportLength, String roof, String customerText) {
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.roof = roof;
        this.customerText = customerText;
        this.adminText = null;
        this.statusId = 1;
        this.salesPrice = 0.0;
    }

    /**
     * Constructor til oprettelse af en ny ordre (før insert).
     * Bruges når customer opretter en ny ordre, og orderId endnu ikke er blevet genereret af databasen.
     */
    public Order(int customerId, int carportWidth, int carportLength, String roof, String customerText) {
        this.customerId = customerId;
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.roof = roof;
        this.customerText = customerText;
        this.adminText = null;
        this.statusId = 1;
        this.salesPrice = 0.0;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Constructor til opdatering af en eksisterende ordre.
     */
    public Order(int orderId, int carportWidth, int carportLength, String roof, String customerText, String adminText, double salesPrice, int statusId) {
        this.orderId = orderId;
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.roof = roof;
        this.customerText = customerText;
        this.adminText = adminText;
        this.salesPrice = salesPrice;
        this.statusId = statusId;
    }

    /**
     * Constructor med alle felter.
     */
    public Order(int orderId, int customerId, int carportWidth, int carportLength, String roof,
                 String customerText, String adminText, int statusId, double salesPrice, Timestamp createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.carportWidth = carportWidth;
        this.carportLength = carportLength;
        this.roof = roof;
        this.customerText = customerText;
        this.adminText = adminText;
        this.statusId = statusId;
        this.salesPrice = salesPrice;
        this.createdAt = createdAt;
    }

    // --- Getters ---
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public int getCarportWidth() { return carportWidth; }
    public int getCarportLength() { return carportLength; }
    public String getRoof() { return roof; }
    public String getCustomerText() { return customerText; }
    public String getAdminText() { return adminText; }
    public int getStatusId() { return statusId; }
    public double getSalesPrice() { return salesPrice; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getCustomerEmail() { return customerEmail; }

    // --- Setters ---
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerId = customerId;
    }
}
