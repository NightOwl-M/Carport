package app.entities;

public class Customer {
    private int customerId;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private int customerZipcode;
    private String customerPhone;

    /**
     * Constructor uden customerId (før insert)
     */
    public Customer(String customerName, String customerEmail, String customerAddress, int customerZipcode, String customerPhone) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerZipcode = customerZipcode;
        this.customerPhone = customerPhone;
    }

    /**
     * Constructor med customerId (efter insert)
     */
    public Customer(int customerId, String customerName, String customerEmail, String customerAddress, int customerZipcode, String customerPhone) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerZipcode = customerZipcode;
        this.customerPhone = customerPhone;
    }

    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerAddress() { return customerAddress; }
    public int getCustomerZipcode() { return customerZipcode; }
    public String getCustomerPhone() { return customerPhone; }
}
