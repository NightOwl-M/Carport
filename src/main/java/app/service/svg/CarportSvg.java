package app.service.svg;

public class CarportSvg {
    private final Svg svgDrawing;
    private final int width;
    private final int height;

    public CarportSvg(int width, int height) {
        this.width = width;
        this.height = height;
        this.svgDrawing = new Svg(0, 0, String.valueOf(width + 100), String.valueOf(height + 100));
    }

    public void addBeams() {
        svgDrawing.addRect(50, 50, width, height, "fill:none;stroke:black;stroke-width:2");
    }

    public void addText() {
        svgDrawing.addText(60, 40, 0, width + " x " + height + " cm");
    }

    public String toString() {
        svgDrawing.close();
        return svgDrawing.toString();
    }
}
