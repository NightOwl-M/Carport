package app.service.order;


public class CarportSvg {
    private Svg svgDrawing;
    private int width;
    private int length;

    public CarportSvg(int width, int length) {
        this.width = Math.max(width, 240);
        this.length = Math.max(length, 240);

        String viewBox = "0 0 " + this.length + " " + this.width;
        svgDrawing = new Svg(0, 0, viewBox, "90%", "90%");
    }

    public void addBeams() {
        svgDrawing.addRectangle(0,0, length, 4.5, "stroke:black; fill:none; stroke-width:2;");
        svgDrawing.addRectangle(0, width - 4.5, length, 4.5, "stroke:black; fill:none; stroke-width:2;");
    }

    public void addRafters() {
        double rafterWidth = 4.5;
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
