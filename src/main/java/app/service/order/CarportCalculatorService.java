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
    private static final int POST = 5; //Id i materiale-tabel i DB
    private static final int BEAMS_AND_RAFTERS = 11; //Id i materiale-tabel i DB


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
        calculatePosts();
        calculateBeams();
        calculateRafters();

        return orderComponents;
    }

    private void calculatePosts() throws DatabaseException { //TODO håndter exception her eller i controller
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(POST, length, connectionPool);

    }

    private void calculateBeams() {

    }

    private void calculateRafters() {

    }



    public List<Component> getOrderComponents() {
        return orderComponents;
    }
}
