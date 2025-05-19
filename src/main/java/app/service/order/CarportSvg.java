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


        svgDrawing.addRectangle(0,0 + distanceToRafterEnd, length, rafterWidth, "stroke:black; fill:none; stroke-width:2;");
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
       /*
        double postWidth = 10;

        svgDrawing.addRectangle(100, rafterWidth/2 + postWidth/2, postWidth, postWidth,"stroke:gray; stroke-width:1;" );
        svgDrawing.addRectangle(410, rafterWidth/2 + postWidth/2, postWidth, postWidth,"stroke:gray; stroke-width:1;" );


  double postWidth = 10;
    double overhangStart = 100;
    double overhangEnd = 30;
    double usableLength = length - overhangStart - overhangEnd;
    int maxDistanceBetweenPoles = 310;

    // Antal sektioner/stolpe-positioner langs længden (én i hver ende + midtersektioner)
    int numberOfSections = (int) Math.ceil(usableLength / maxDistanceBetweenPoles);
    double sectionSpacing = usableLength / numberOfSections;

    // Y-positioner for stolper (under hver rem)
    double leftY = rafterWidth / 2 - postWidth / 2 + distanceToRafterEnd;
    double rightY = width - rafterWidth / 2 - postWidth / 2 - distanceToRafterEnd;

    // Tegn stolper ved hver sektion på begge sider
    for (int i = 0; i <= numberOfSections; i++) {
        double x = overhangStart + i * sectionSpacing;

        svgDrawing.addRectangle(x - postWidth / 2, leftY, postWidth, postWidth, "stroke:gray; stroke-width:1;");
        svgDrawing.addRectangle(x - postWidth / 2, rightY, postWidth, postWidth, "stroke:gray; stroke-width:1;");
    }
}
        */


        double postWidth = 10;
        int totalOverHang = 100 + 30;
        int maxDistanceBetweenPoles = 310;

        int numberOfPolesEachSide = 2 + (length - totalOverHang) / maxDistanceBetweenPoles;
        int totalPoles = numberOfPolesEachSide * 2;

        double spacing = (length - totalOverHang) / (numberOfPolesEachSide - 1);
        double startX = 100;

        double leftY = rafterWidth / 2.0 - postWidth / 2.0; // Centrer under øverste rem
        double rightY = width - rafterWidth / 2.0 - postWidth / 2.0; // Centrer under nederste rem

        for (int i = 0; i < numberOfPolesEachSide; i++) {
            double x = startX + i * spacing;

            // Venstre side
            svgDrawing.addRectangle(x, leftY, postWidth, postWidth, "stroke:black; fill:gray;");

            // Højre side
            svgDrawing.addRectangle(x, rightY, postWidth, postWidth, "stroke:black; fill:gray;");
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
