package app.service.svg;

public class Svg {
    private final StringBuilder svg = new StringBuilder();
    private final int x;
    private final int y;
    private final String width;
    private final String height;

    public Svg(int x, int y, String width, String height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        svg.append(String.format(
                "<svg viewBox='%d %d %s %s' xmlns='http://www.w3.org/2000/svg'>\n",
                x, y, width, height
        ));
    }

    public void addRect(int x, int y, int w, int h, String style) {
        svg.append(String.format(
                "<rect x='%d' y='%d' width='%d' height='%d' style='%s' />\n",
                x, y, w, h, style
        ));
    }

    public void addLine(int x1, int y1, int x2, int y2, String style) {
        svg.append(String.format(
                "<line x='%d' y='%d' x2='%d' y2='%d' style='%s' />\n",
                x1, y1, x2, y2, style
        ));
    }

    public void addText(int x, int y, int rotation, String text) {
        svg.append(String.format(
                "<text transform='rotate(%d, %d,%d)' x='%d' y='%d'>%s</text>\n",
                rotation, x, y, x, y, text
        ));
    }

    public void close() {
        svg.append("</svg>");
    }

    @Override
    public String toString() {
        return svg.toString();
    }
}

