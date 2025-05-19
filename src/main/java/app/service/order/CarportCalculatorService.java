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


    protected void calculatePosts(Order order) throws DatabaseException {
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(POST_ID, 300, connectionPool); //Stolper findes kun i 1 længde, derfor 300
        MaterialVariant materialVariant = materialVariants.get(0);

        int quantity = calculatePostQuantity();
        Component orderComponent = new Component(order.getOrderId(), quantity, "Stolper nedgraves 90 cm i jord", materialVariant);
        orderComponents.add(orderComponent);
    }

    protected int calculatePostQuantity() {
        //310 er fra den antagelse, at det er det længste stykke der må være mellem 2 stolper, jvf tegningen vi har fået
        //130 er afstanden fra carportens start til den første stolpe: 100 cm + afstanden fra sidste stolpe og til carportens slutning: 30 cm
        //Er den 440 eller over så skal der bruges 6 stolper
        //Ganges med 2 fordi der skal bruges samme antal på hver side, plusses med 2, da der altid til være 2 stolper på hver side
        int totalOverHang = 100 + 30;
        int maxDistanceBetweenPoles = 310;

        return 2 * (2 + (length - totalOverHang) / maxDistanceBetweenPoles);
    }

    protected void calculateBeams(Order order) throws DatabaseException {
        int maxBeamLength = 600;
        int overHangFront = 100; //TODO, intelliJ forslår metode for sig selv
        int overHangRear = 30;
        int maxDistanceBetweenPoles = 310;

        int remainingLength = length - overHangFront + overHangRear; //fx. 780 - 100 - 30 = 650
        int modules = (int) Math.ceil((double) remainingLength / maxDistanceBetweenPoles); //fx. 650 / 310 = 2.1 ~ 3
        int modulesLength = (int)  Math.ceil((double) remainingLength / modules); //fx. 650 / 3 = 217
        int BeamJoiningPoint = overHangFront + modulesLength; //fx. 100 + 217 = 317 (placering af pæl nr. 2 og evt. samlested for remme


        //Vi antager at det længste spær Fog har, er på 600 cm
        if (length <= maxBeamLength) {
            List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(BEAMS_AND_RAFTERS_ID, length, connectionPool);
            MaterialVariant materialVariant = materialVariants.get(0);
            Component orderComponent = new Component(order.getOrderId(), 2, "Remme i sider, sadles ned i stolper", materialVariant);
            orderComponents.add(orderComponent);
        } else {
            int firstBeamLength = BeamJoiningPoint;
            int restLength = length - firstBeamLength; //fx. 780 - 317 = 463

            //Første rem findes
            List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(BEAMS_AND_RAFTERS_ID, firstBeamLength, connectionPool);
            MaterialVariant materialVariant = materialVariants.get(0);
            Component orderComponent = new Component(order.getOrderId(), 2, "Remme i sider, sadles ned i stolper", materialVariant);
            orderComponents.add(orderComponent);

            //Vi ser om vi kan bruge ét spær, som kan deles i 2, til den anden del af remmen
            if (restLength * 2 <= maxBeamLength) {  //463 * 2 = 926 = false
                for (MaterialVariant mv : materialVariants) {
                    if (mv.getLength() >= restLength * 2) {
                        Component orderComponent2 = new Component(order.getOrderId(), 1, "Remme i sider, sadles ned i stolper", mv);
                        orderComponents.add(orderComponent2);
                        break;
                    }
                }
            } else {
                //Vi finder det korteste spær vi kan bruge som anden rem og tilføjer 2 af dem
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

    protected void calculateRafters(Order order) throws DatabaseException {
        //Antallet af spær beregnes
        int quantity = calculateRaftersQuantity(length);

        //Materiale-varianten med
        List<MaterialVariant> materialVariants = MaterialVariantMapper.getMaterialVariantsByIdAndMinLength(BEAMS_AND_RAFTERS_ID, width, connectionPool);
        MaterialVariant materialVariant = materialVariants.get(0);
        Component orderComponent = new Component(order.getOrderId(), quantity, "Spær, monteres på rem", materialVariant);
        orderComponents.add(orderComponent);
    }

    protected int calculateRaftersQuantity(int length) {
        //Beregning af spær
        int quantity;
        int modules; //1 spær og 1 tomrum
        double rafterWidth = 4.5;
        int emptySpaceBetweenRafters = 55;
        double moduleLength = emptySpaceBetweenRafters + rafterWidth; //Forkant af spær til forkant af næste spær
        double lengthMinusEndRafter = length - rafterWidth;

        //Rundes op til nærmeste heltal, for at undgå at have et spær for lidt, samt slutspærret ligges til
        modules = (int) Math.ceil(lengthMinusEndRafter / moduleLength);
        quantity = modules + 1;
        return quantity;
    }


    //Getters and Setters
    public List<Component> getOrderComponents() {
        return orderComponents;
    }
}
