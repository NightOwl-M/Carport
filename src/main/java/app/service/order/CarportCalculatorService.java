package app.service.order;

import app.entities.Component;
import app.entities.MaterialVariant;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.mapper.material.MaterialVariantMapper;
import app.persistence.ConnectionPool;

import java.util.ArrayList;
import java.util.List;


public class CarportCalculatorService {
    //Konstanter med værdi tilsvarende materialets id i DB
    private static final int POST_ID = 5;
    private static final int BEAMS_AND_RAFTERS_ID = 11;

    //Konstanter i cm, vi antager at nedenstående mål/antagelser gør sig gældende for alle Fog's skræddersyet carporte med fladt tag
    private static final int OVERHANG_FRONT = 100;
    private static final int OVERHANG_REAR = 30;
    private static final int MAX_DISTANCE_BETWEEN_POSTS = 310;
    private static final double RAFTER_WIDTH = 4.5;
    private static final int MAX_BEAM_LENGTH = 600;
    private static final int EMPTY_SPACE_BETWEEN_RAFTERS = 55;
    private static final int POST_LENGTH = 300;

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

    public double calculateCarportMaterialCost(List<Component> orderComponents) {
        double carportMaterialCost = 0;
        for (Component orderComponent : orderComponents) {
            double pricePrMeter = orderComponent.getMaterialVariant().getMaterial().getPrice();
            double totalMaterialLength = (double) orderComponent.getMaterialVariant().getLength() / 100 * orderComponent.getQuantity();
            carportMaterialCost += totalMaterialLength * pricePrMeter;
        }
        return carportMaterialCost;
    }


    public void calculatePosts(Order order) throws DatabaseException {
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(POST_ID, POST_LENGTH, connectionPool);
        MaterialVariant materialVariant = materialVariants.get(0);

        int quantity = calculatePostQuantity();
        Component orderComponent = new Component(order.getOrderId(), quantity, "Stolper nedgraves 90 cm i jord", materialVariant);
        orderComponents.add(orderComponent);
    }

    public int calculatePostQuantity() {
        //Længden imellem de yderste stolper findes
        int remainingLength = length - OVERHANG_FRONT - OVERHANG_REAR; //fx. 750 - 130 = 620

        //Antallet af moduler imellem de yderste stoler findes (modul = 1 stolpe + tomrum til næste stolpe)
        int modules = (int) Math.ceil((double) remainingLength / MAX_DISTANCE_BETWEEN_POSTS); //fx. 620 / 310 = 2

        //Antallet af stolper vil altid være 1 mere end der er moduler, da sidste stolpe ikke skal have et efterfølgende modul
        //Vi ganger derefter med 2 da der er 2 sider i en carport med stolper
        int quantity = (modules + 1) * 2;
        return quantity;
    }

    public void calculateBeams(Order order) throws DatabaseException {
        if (length <= MAX_BEAM_LENGTH) {
            List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(BEAMS_AND_RAFTERS_ID, length, connectionPool);
            MaterialVariant materialVariant = materialVariants.get(0);
            Component orderComponent = new Component(order.getOrderId(), 2, "Remme i sider, sadles ned i stolper", materialVariant);
            orderComponents.add(orderComponent);
        } else {
            //Første rems længde findes ved beregne remmenes samlepunkt, vi antager at det altid sker ved stolpe nr. 2
            int firstBeamLength = calculateBeamsJoiningPoint();
            int restLength = length - firstBeamLength; //fx. 780 - 317 = 463

            //Første rem findes
            List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(BEAMS_AND_RAFTERS_ID, firstBeamLength, connectionPool);
            MaterialVariant materialVariant = materialVariants.get(0);
            Component orderComponent = new Component(order.getOrderId(), 2, "Remme i sider, sadles ned i stolper", materialVariant);
            orderComponents.add(orderComponent);

            //Vi ser om vi kan bruge ét spær, som kan deles i 2, til den anden del af remmen
            if (restLength * 2 <= MAX_BEAM_LENGTH) {  //463 * 2 = 926 = false
                for (MaterialVariant mv : materialVariants) {
                    if (mv.getLength() >= restLength * 2) {
                        Component orderComponent2 = new Component(order.getOrderId(), 1, "Remme i sider, sadles ned i stolper", mv);
                        orderComponents.add(orderComponent2);
                        break;
                    }
                }
            } else {
                //Vi finder det korteste spær vi kan bruge som det andet remstykke og tilføjer 2 af dem
                for (MaterialVariant mv : materialVariants) {
                    if (mv.getLength() >= restLength) { //fx. 480 >= 463 = true
                        Component orderComponent2 = new Component(order.getOrderId(), 2, "Remme i sider, sadles ned i stolper", mv);
                        orderComponents.add(orderComponent2);
                        break;
                    }
                }
            }
        }
    }

    public int calculateBeamsJoiningPoint() {
        //Længden mellem de yderste stolper findes
        int remainingLength = length - OVERHANG_FRONT - OVERHANG_REAR; //fx. 780 - 100 - 30 = 650

        //Antallet af moduler findes
        int modules = (int) Math.ceil((double) remainingLength / MAX_DISTANCE_BETWEEN_POSTS); //fx. 650 / 310 = 2.1 ~ 3

        //Længden af hvert modul beregnes
        int modulesLength = (int) Math.ceil((double) remainingLength / modules); //fx. 650 / 3 = 217

        //Første stolpe placers altid efter 100 cm og næste stolpe placeres derfor 1 moduls længde efter:
        int BeamJoiningPoint = OVERHANG_FRONT + modulesLength; //fx. 100 + 217 = 317 (placering af pæl nr. 2 og samlested for remme)
        return BeamJoiningPoint;
    }

    public void calculateRafters(Order order) throws DatabaseException {
        //Antallet af spær beregnes
        int quantity = calculateRaftersQuantity(length);

        //Materiale-varianten med
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(BEAMS_AND_RAFTERS_ID, width, connectionPool);
        MaterialVariant materialVariant = materialVariants.get(0);
        Component orderComponent = new Component(order.getOrderId(), quantity, "Spær, monteres på rem", materialVariant);
        orderComponents.add(orderComponent);
    }

    public int calculateRaftersQuantity(int length) {
        int modules; //Udgør 1 spær og 1 tomrum
        double moduleLength = EMPTY_SPACE_BETWEEN_RAFTERS + RAFTER_WIDTH; //Forkant af spær til forkant af næste spær
        double carportLengthMinusEndRafter = length - RAFTER_WIDTH;

        //Rundes op til nærmeste heltal, for at undgå at have et spær for lidt, samt slutspærret ligges til
        modules = (int) Math.ceil(carportLengthMinusEndRafter / moduleLength);
        int quantity = modules + 1;
        return quantity;
    }
}


