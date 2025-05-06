package app.entities;

public class User {
    private int userId;
    private String name;
    private String address;
    private int zipcode;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;


    public User(int userId, String name, String address, int zipcode, String email, String password, String phoneNumber, String role) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.zipcode = zipcode;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public User(String name, String address, int zipcode, String email, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.zipcode = zipcode;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getZipcode() { return zipcode; }
    public void setZipcode(int zipcode) { this.zipcode = zipcode; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
