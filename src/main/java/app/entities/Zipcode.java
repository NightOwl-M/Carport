package app.entities;

public class Zipcode {
    private int zipcode;
    private String city;

    public Zipcode(int zipcode, String city) {
        this.zipcode = zipcode;
        this.city = city;
    }

    public int getZipcode() { return zipcode; }
    public String getCity() { return city; }
}
