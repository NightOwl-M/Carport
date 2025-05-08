package app.service.order;

public class CarportSvg {
    private Svg svgDrawing;
    private int width;
    private int height;

    public CarportSvg(int width, int height) {
        this.width = width;
        this.height = height;
        svgDrawing = new Svg(0, 0, "0 0 " + width + " " + height, width + "px", height + "px");
    }

    public void addBeams() {
        svgDrawing.addRectangle(20, 10, 30, height - 20, "stroke:black; fill:none; stroke-width:2;");
        svgDrawing.addRectangle(width - 50, 10, 30, height - 20, "stroke:black; fill:none; stroke-width:2;");
    }

    public void addRafters() {
        for (int i = 50; i < width - 50; i += 100) {
            svgDrawing.addLine(i, 10, i, height - 10, "stroke:gray; stroke-width:1;");
        }
    }


    public void addPost() {
        // En centralt placeret stolpe
        svgDrawing.addRectangle(width / 2 - 15, height / 2 - 15, 30, 30, "stroke:black; fill:gray;");
    }

    public void addText() {
        svgDrawing.addText(width / 2 - 30, height / 2, 0, "Carport");
    }

    @Override
    public String toString() {
        return svgDrawing.toString();
    }
}
