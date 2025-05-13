package app.service.order;

import app.entities.Component;
import app.entities.MaterialVariant;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.mapper.material.MaterialVariantMapper;
import app.persistence.ConnectionPool;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.List;

public class CarportCalculatorService {
    //Konstanter med værdi tilsvarende materialets id i DB
    private static final int POST = 5;
    private static final int BEAMS_AND_RAFTERS = 11;

    private List<Component> orderComponents = new ArrayList<>();
    private int width;
    private int length;
    private ConnectionPool connectionPool;


    public CarportCalculatorService(int length, int width, ConnectionPool connectionPool) {
        this.length = length;
        this.width = width;
        this.connectionPool = connectionPool;
    }

    public List<Component> calculateCarportBOM(Order order) throws DatabaseException {
        calculatePosts(order);
        calculateBeams(order);
        calculateRafters(order);

        return orderComponents;
    }

    private void calculatePosts(Order order) throws DatabaseException { //TODO håndter exception her eller i controller
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(POST, 0, connectionPool); //Stolper findes kun i 1 længde, derfor 0
        MaterialVariant materialVariant = materialVariants.get(0);

        int quantity = calculatePostQuantity();
        Component orderComponent = new Component(order.getOrderId(), quantity, "Stolper nedgraves 90 cm i jord", materialVariant);
        orderComponents.add(orderComponent);
    }

    private void calculateBeams(Order order) {

    }

    private void calculateRafters(Order order) {

    }

    private int calculatePostQuantity () {
        //340 er fra den antagelse, at det er det længste stykke der må være mellem 2 stolper
        //130 er afstanden fra carportens start til den første stolpe 100 + afstanden fra sidste stolpe og til carportens slutning

        //TODO Jons regnestykke, tjek selv
        return 2 * (2 + (length - 130) / 340);
    }



    //Getters and Setters
    public List<Component> getOrderComponents() {
        return orderComponents;
    }
}
