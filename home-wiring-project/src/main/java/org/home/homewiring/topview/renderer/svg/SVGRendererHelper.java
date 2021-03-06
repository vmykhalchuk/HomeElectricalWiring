package org.home.homewiring.topview.renderer.svg;

import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewAreaItem;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.homewiring.topview.symbolsplacer.SymbolData;
import org.home.homewiring.utils.Point;

import java.io.PrintWriter;

public class SVGRendererHelper {

    private PrintWriter writer;
    private TopViewModel topViewModel;
    private TopViewRenderingEngine tvRenderingEngine;
    private boolean debugEnabled;
    private int indent = 0;
    private int identifier = 1000;

    public SVGRendererHelper(PrintWriter writer, TopViewModel topViewModel, TopViewRenderingEngine tvRenderingEngine, boolean debugEnabled) {
        this.writer = writer;
        this.topViewModel = topViewModel;
        this.tvRenderingEngine = tvRenderingEngine;
        this.debugEnabled = debugEnabled;
    }

    public void writeToStream() {
        double maxX = topViewModel.getAreas().stream().mapToDouble((a) -> a.getX() + a.getxWidth()).max().orElse(0);
        double maxY = topViewModel.getAreas().stream().mapToDouble((a) -> a.getY() + a.getyLength()).max().orElse(0);

        printlnF("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        printlnF("<svg");
        printlnF("        xmlns=\"http://www.w3.org/2000/svg\"");
        printlnF("        width=\"%s\"", maxX + 10);
        printlnF("        height=\"%s\"", maxY + 10);
        printlnF("        id=\"svg2\"");
        printlnF("        version=\"1.1\">");

        indent++;

        // print Areas
        for (final TopViewArea tvArea : topViewModel.getAreas()) {
            gWrap(() -> {
                printlnF("<!-- Area: %s, data -->", tvArea.getCode());
                gWrap(() -> {
                    printlnF("<rect style=\"fill:none;stroke:#000000;stroke-width:2\" id=\"%s\"", nextSvgId());
                    printlnF("      y=\"%s\" x=\"%s\" height=\"%s\" width=\"%s\"/>", tvArea.getY(), tvArea.getX(), tvArea.getyLength(), tvArea.getxWidth());
                    // FIXME Now we have to print AreaItems
                    printAreaItems(tvArea);
                });
                printlnF("<!-- Area: %s, Symbols data -->", tvArea.getCode());
                gWrap(new AreaSymbolsWorker(tvArea));
            });
        }

        writer.print("</svg>\n");
    }

    private void printAreaItems(TopViewArea area) {
        for (final TopViewAreaItem item : area.getItems()) {
            String fill = "none";
            String stroke = "#000000";
            String strokeWidth = "2";
            switch (item.getType()) {
                case door:
                    fill = "#ffffff";
                    break;
                case opening:
                    fill = "#ffffff";
                    stroke = "#ffffff";
                    strokeWidth = "0";
                    break;
            }

            printlnF("<rect style=\"fill:%s;stroke:%s;stroke-width:%s\"", fill, stroke, strokeWidth);
            double x = item.getX(), y = item.getY();
            double width = item.getxWidth(), length = item.getyLength();
            if (width <= 0.001) {
                width = 10;
                x -= 5;
            } else {
                length = 10;
                y -= 5;
            }
            printlnF("      x=\"%s\" y=\"%s\"", area.getX() + x, area.getY() + y);
            printlnF("      width=\"%s\" height=\"%s\"/>", width, length);
        }
    }

    private String nextSvgId() {
        return "svg_" + ++identifier;
    }

    private void printIndent() {
        for (int i = 0; i < indent * 4; i++) {
            writer.print(' ');
        }
    }

    private void printlnF(String format, Object... args) {
        println(String.format(format, args));
    }

    private void println(String s) {
        printIndent();
        writer.print(s);
        writer.print('\n');
    }

    private void gWrap(Worker... worker) {
        for (Worker w : worker) {
            println("<g>");
            indent++;
            w.doWork();
            indent--;
            println("</g>");
        }
    }

    private void printSvgLine(Point p1, Point p2) {
        printSvgLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    private void printSvgLine(double x1, double y1, double x2, double y2) {
        printlnF("<path style=\"fill:#000000;stroke:#000000;stroke-width:1\"");
        printlnF("      d=\"m %s,%s %s,%s z\"/>", x1, y1, x2 - x1, y2 - y1);
    }

    private interface Worker {
        void doWork();
    }

    private class AreaSymbolsWorker implements Worker {

        private TopViewArea tvArea;

        AreaSymbolsWorker(TopViewArea tvArea) {
            this.tvArea = tvArea;
        }

        @Override
        public void doWork() {
            for (final TopViewSymbol tvSymbol : tvArea.getSymbols()) {
                final String pointType = tvSymbol.getPointType();
                gWrap(() -> {
                    printlnF("<!-- tvSymbol(type:%s) here-->", pointType);

                    String symbolInnerText = tvSymbol.getInnerText() == null ? "" : tvSymbol.getInnerText();
                    SymbolData symbolData = new SymbolData(new Point(tvArea.getX(), tvArea.getY()), tvSymbol, tvRenderingEngine);

                    // print line from Symbol Sign to Point location
                    printSvgLine(symbolData.getPointPoint(), symbolData.getSignCentre());

                    final double symbolX = symbolData.getRect().getX1();
                    final double symbolY = symbolData.getRect().getY1();
                    final double symbolSignX = symbolData.getSignPoint1().getX();
                    final double symbolSignY = symbolData.getSignPoint1().getY();
                    final double symbolSignXCentre = symbolData.getSignCentre().getX();
                    final double symbolSignYCentre = symbolData.getSignCentre().getY();
                    final double symbolLabelX = symbolX + tvRenderingEngine.getSymbolLabelXRelative(tvSymbol);
                    final double symbolLabelY = symbolY + tvRenderingEngine.getSymbolLabelYRelative(tvSymbol);

                    // print symbol sign
                    switch (pointType) {
                        case "W":
                            printlnF("<path style=\"fill:%s;stroke:%s;stroke-width:1\" id=\"%s\"", tvSymbol.getColor(), tvSymbol.getColor(), nextSvgId());
                            printlnF("      d=\"m %s,%s 7,-9 6.99997,9 -6.99997,8.99994 -7,-8.99994 z\"/>", symbolSignX, symbolSignY + 10d);
                            printlnF("<text style=\"font-weight:bold;font-size:11px;font-family:Helvetica, Arial, sans-serif;fill:#ffffff;stroke:#000000;stroke-width:0\"");
                            printlnF("      x=\"%s\" y=\"%s\">%s</text>", symbolSignX + 4.2, symbolSignY + 13.5, symbolInnerText);
                            break;
                        case "S":
                            printlnF("<rect style=\"fill:%s;stroke:%s;stroke-width:0\" id=\"%s\" height=\"10\" width=\"18\"", tvSymbol.getColor(), tvSymbol.getColor(), nextSvgId());
                            printlnF("      x=\"%s\" y=\"%s\"/>", symbolSignX, symbolSignY);
                            printlnF("<text style=\"font-weight:bold;font-size:11px;font-family:Helvetica, Arial, sans-serif;fill:#ffffff;stroke:#000000;stroke-width:0\"");
                            printlnF("      id=\"%s\"", nextSvgId());
                            printlnF("      x=\"%s\" y=\"%s\">%s</text>", symbolSignX + 5.0d, symbolSignY + 9.1d, symbolInnerText);
                            break;
                        case "M":
                            printlnF("<circle style=\"fill:%s\" r=\"10\" cx=\"%s\" cy=\"%s\"/>", tvSymbol.getColor(), symbolSignXCentre, symbolSignYCentre);
                            printlnF("<circle style=\"fill:%s\" r=\"10\" cx=\"%s\" cy=\"%s\"/>", tvSymbol.getColor(), symbolSignX + 10, symbolSignY + 10);
                            printlnF("<text style=\"font-weight:bold;font-size:11px;font-family:Helvetica, Arial, sans-serif;fill:#ffffff;stroke:#000000;stroke-width:0\"");
                            printlnF("      x=\"%s\" y=\"%s\">%s</text>", symbolSignX + 7.0d, symbolSignY + 14.1d, symbolInnerText);
                            break;
                        default:
                            throw new RuntimeException("No SVG Renderer for Point type: " + pointType);
                    }

                    // print symbol label
                    printlnF("<text style=\"font-size:8px;font-family:Helvetica, Arial, sans-serif;fill:#000000;stroke:#000000;stroke-width:0\"");
                    printlnF("      x=\"%s\" y=\"%s\">%s</text>", symbolLabelX, symbolLabelY + 6.5, tvSymbol.getLabelText());

                    if (debugEnabled) {
                        // print debug rectangle
                        printlnF("<rect style=\"fill:none;stroke:#000000;stroke-width:1\" width=\"%s\" height=\"%s\"", symbolData.getXWidth(), symbolData.getYLength());
                        printlnF("      x=\"%s\" y=\"%s\"/>", symbolX, symbolY);

                        // print symbol label rectangle - for debug purposes
                        printlnF("<rect style=\"fill:none;stroke:#555555;stroke-width:1\" width=\"%s\" height=\"%s\"", 5, 5);
                        printlnF("      x=\"%s\" y=\"%s\"/>", symbolLabelX, symbolLabelY);

                        // print line from Symbol Sign to Point location (debug purposes - so line appears above symbol sign)
                        printSvgLine(symbolData.getPointPoint(), symbolData.getSignCentre());
                    }
                });
            }
        }
    }

}
