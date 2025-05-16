package app.service.order;

public class CarportSvg {
    private Svg svgDrawing;
    private int width;
    private int height;

    public CarportSvg(int width, int height) {
        this.width = Math.max(width, 300);
        this.height = Math.max(height, 300);

        String viewBox = "0 0 " + this.width + " " + this.height;
        svgDrawing = new Svg(0, 0, viewBox, "100%", "100%");
    }

    public void addBeams() {
        int beamWidth = Math.max(30, width / 10);
        svgDrawing.addRectangle(20, 10, beamWidth, height - 20, "stroke:black; fill:none; stroke-width:2;");
        svgDrawing.addRectangle(width - beamWidth - 20, 10, beamWidth, height - 20, "stroke:black; fill:none; stroke-width:2;");
    }

    public void addRafters() {
        int rafterSpacing = Math.max(50, width / 10);
        for (int i = rafterSpacing; i < width - rafterSpacing; i += rafterSpacing) {
            svgDrawing.addLine(i, 10, i, height - 10, "stroke:gray; stroke-width:1;");
        }
    }

    public void addPost() {
        int postSize = Math.max(30, Math.min(width, height) / 8);
        svgDrawing.addRectangle(width / 2 - postSize / 2, height / 2 - postSize / 2, postSize, postSize, "stroke:black; fill:gray;");
    }

    public void addText() {
        int fontSize = Math.max(12, width / 20);
        svgDrawing.addText(width / 2 - 30, height / 2, 0, "Carport", fontSize);
    }

    @Override
    public String toString() {
        return svgDrawing.toString();
    }
}
