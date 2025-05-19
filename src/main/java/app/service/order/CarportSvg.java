package app.service.order;


public class CarportSvg {
    private Svg svgDrawing;
    private int width;
    private int length;
    private final int distanceToRafterEnd = 10; //Remmene går ikke helt ud til spærenes ender
    private final double rafterWidth = 4.5;

    public CarportSvg(int width, int length) {
        this.width = Math.max(width, 240);
        this.length = Math.max(length, 240);

        String viewBox = "0 0 " + this.length + " " + this.width;
        svgDrawing = new Svg(0, 0, viewBox, "90%", "90%");
    }

    public void addBeams() {
        svgDrawing.addRectangle(0, 0 + distanceToRafterEnd, length, rafterWidth, "stroke:black; fill:none; stroke-width:2;");
        svgDrawing.addRectangle(0, width - rafterWidth - distanceToRafterEnd, length, rafterWidth, "stroke:black; fill:none; stroke-width:2;");
    }

    public void addRafters() {
        double emptySpaceBetweenRaftes = 55;
        double moduleLength = emptySpaceBetweenRaftes + rafterWidth; //Forkant af spær til forkant af næste spær

        double lengthMinusEndRafter = length - rafterWidth; //Vi gør plads til sidste spær, fx 780 - 4.5 = 775.5 cm
        int modules = (int) Math.ceil((lengthMinusEndRafter) / moduleLength); //Runder op til nærmeste heltal, fx 13.03 ~ 14
        double distanceBetweenRafters = (lengthMinusEndRafter) / modules; //fx 775.5 / 14 = 55.4 cm

        for (double x = 0; x <= lengthMinusEndRafter; x += distanceBetweenRafters) {
            svgDrawing.addRectangle(x, 0.0, rafterWidth, width, "stroke:gray; stroke-width:1;");
        }
    }

    public void addPost() {
        //svgDrawing.addRectangle(100, rafterWidth / 2 + postWidth / 2, postWidth, postWidth, "stroke:gray; stroke-width:1;");
        //svgDrawing.addRectangle(410, rafterWidth / 2 + postWidth / 2, postWidth, postWidth, "stroke:gray; stroke-width:1;");


        double postWidth = 10;
        int overHangFront = 100;
        int overHangRear = 30;
        int remainingLength = length - overHangFront - overHangRear;
        int maxDistanceBetweenPoles = 310;

        int modules = (int) Math.ceil((double) remainingLength / maxDistanceBetweenPoles); //fx. 650 / 310 = 2.1 ~ 3
        int moduleLength = (int)  Math.ceil((double) remainingLength / modules); //fx. 650 / 3 = 217

        for (double x = 100; x <= length; x += moduleLength) {
            svgDrawing.addRectangle(x - postWidth / 2, rafterWidth / 2 + postWidth / 2, postWidth, postWidth, "stroke:gray; stroke-width:1;"); //Øverste stolper
            svgDrawing.addRectangle(x - postWidth / 2, width - rafterWidth / 2 - postWidth / 2 - distanceToRafterEnd, postWidth, postWidth, "stroke:gray; stroke-width:1;"); //Nederste stolper
        }
    }

    public void addText() {
        // int fontSize = Math.max(12, width / 20);
        //  svgDrawing.addText(width / 2 - 30, height / 2, 0, "Carport", fontSize);
    }

    @Override
    public String toString() {
        return svgDrawing.toString();
    }
}
