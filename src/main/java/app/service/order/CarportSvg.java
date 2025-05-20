package app.service.order;


public class CarportSvg {
    private Svg svgDrawing;
    private int width;
    private int length;
    private static final int DISTANCE_TO_RAFTER_END = 35; //Remmene går ikke helt ud til spærenes ender
    private static final double RAFTER_WIDTH = 4.5;
    private static final double POST_WIDTH = 10;
    private static final int OVERHANG_FRONT = 100;
    private static final int OVERHANG_REAR = 30;
    private static final int MAX_DISTANCE_BETWEEN_POLES = 310;
    private static final int EMPTY_SPACE_BETWEEN_RAFTERS = 55;

    public CarportSvg(int width, int length) {
        this.width = Math.max(width, 240);
        this.length = Math.max(length, 240);

        String viewBox = "0 0 " + this.length + " " + this.width;
        svgDrawing = new Svg(0, 0, viewBox, "100%", "100%");
    }

    public void addBeams() {
        svgDrawing.addRectangle(0, 0 + DISTANCE_TO_RAFTER_END, length, RAFTER_WIDTH, "stroke:black; fill:none; stroke-width:2;");
        svgDrawing.addRectangle(0, width - RAFTER_WIDTH - DISTANCE_TO_RAFTER_END, length, RAFTER_WIDTH, "stroke:black; fill:none; stroke-width:2;");
    }

    public void addRafters() {
        double moduleLength = EMPTY_SPACE_BETWEEN_RAFTERS + RAFTER_WIDTH; //Forkant af spær til forkant af næste spær

        double lengthMinusEndRafter = length - RAFTER_WIDTH; //Vi gør plads til sidste spær, fx 780 - 4.5 = 775.5 cm
        int modules = (int) Math.ceil((lengthMinusEndRafter) / moduleLength); //Runder op til nærmeste heltal, fx 13.03 ~ 14
        double distanceBetweenRafters = (lengthMinusEndRafter) / modules; //fx 775.5 / 14 = 55.4 cm

        for (double x = 0; x <= lengthMinusEndRafter; x += distanceBetweenRafters) {
            svgDrawing.addRectangle(x, 0.0, RAFTER_WIDTH, width, "stroke:gray; stroke-width:1;");
        }
    }

    public void addPost() {
        //Vi finder længde mellem de 2 yderste stolper
        int remainingLength = length - OVERHANG_FRONT - OVERHANG_REAR; //fx. 780 - 130 = 650

        //Vi beregner antallet af moduler og derefter kan vi finde længden af hvert modul og derved stolpernes placering
        int modules = (int) Math.ceil((double) remainingLength / MAX_DISTANCE_BETWEEN_POLES); //fx. 650 / 310 = 2.1 ~ 3
        int moduleLength = (int)  Math.ceil((double) remainingLength / modules); //fx. 650 / 3 = 217

        //Loopet placerer første stolpe ved x = 100 og derefter med 1 moduleLength i mellemrum
        for (double x = OVERHANG_FRONT; x <= length; x += moduleLength) {
            svgDrawing.addRectangle(x - POST_WIDTH / 2,  DISTANCE_TO_RAFTER_END - POST_WIDTH / 2 + RAFTER_WIDTH / 2, POST_WIDTH, POST_WIDTH, "stroke:gray; stroke-width:1;"); //Øverste stolper
            svgDrawing.addRectangle(x - POST_WIDTH / 2, width - RAFTER_WIDTH / 2 - POST_WIDTH / 2 - DISTANCE_TO_RAFTER_END, POST_WIDTH, POST_WIDTH, "stroke:gray; stroke-width:1;"); //Nederste stolper
        }
    }

    public void addText() {
    }

    @Override
    public String toString() {
        return svgDrawing.toString();
    }
}
