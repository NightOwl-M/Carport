package app.service.order;

import java.util.Locale;

public class Svg {
    private StringBuilder svg;

    public Svg(int x, int y, String viewBox, String width, String height) {
        svg = new StringBuilder();
        svg.append(String.format("<svg x='%d' y='%d' viewBox='%s' width='%s' height='%s' xmlns='http://www.w3.org/2000/svg' preserveAspectRatio='xMinYMin meet'>",
                x, y, viewBox, width, height));
        addMarker();
    }

    public void addRectangle(double x, double y, double width, double height, String style) {
        svg.append(String.format(Locale.US, "<rect x='%f' y='%f' width='%f' height='%f' style='%s' />", x, y, width, height, style));
    }



    public void addLine(int x1, int y1, int x2, int y2, String style) {
        svg.append(String.format("<line x1='%d' y1='%d' x2='%d' y2='%d' style='%s' />", x1, y1, x2, y2, style));
    }

    public void addText(int x, int y, int rotation, String text, int fontSize) {
        svg.append(String.format("<text x='%d' y='%d' style='font-size:%dpx; fill:black;'>%s</text>", x, y, fontSize, text));
    }

    public void addMarker() {
        svg.append("<defs>");
        svg.append("<marker id='arrow' markerWidth='10' markerHeight='10' refX='5' refY='5' orient='auto'>");
        svg.append("<path d='M0,0 L10,5 L0,10 Z' fill='black' />");
        svg.append("</marker>");
        svg.append("</defs>");
    }

    @Override
    public String toString() {
        return svg.toString() + "</svg>";
    }
}