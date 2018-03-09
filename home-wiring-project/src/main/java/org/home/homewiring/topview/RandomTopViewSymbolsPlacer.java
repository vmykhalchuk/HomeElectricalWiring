package org.home.homewiring.topview;

import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.Snapshot;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.utils.MyMath;
import org.home.utils.Point;
import org.home.utils.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class RandomTopViewSymbolsPlacer {

    public static double POINT_BORDER_MARGIN = 5; // 5 points from border // FIXME This constant depends on SVG / PNG Rendering engine!

    protected TopViewModel topViewModel;
    protected TopViewRenderingEngine renderingEngine;

    public RandomTopViewSymbolsPlacer(TopViewModel topViewModel, TopViewRenderingEngine renderingEngine) {
        this.topViewModel = topViewModel;
        this.renderingEngine = renderingEngine;
    }

    public void placeSymbolsProperly() {
        TopViewSymbolsPlacer.placeSymbolsProperly(topViewModel, renderingEngine);

        for (final TopViewArea tvArea : topViewModel.getAreas()) {
            final List<SymbolData> symbolsList = new ArrayList<>();
            for (final TopViewSymbol tvSymbol : tvArea.getSymbols()) {
                symbolsList.add(new SymbolData(tvSymbol, renderingEngine));
            }

            // now place symbols randomly
            randomizedPlacing(tvArea, symbolsList);
        }
    }

    private void randomizedPlacing(TopViewArea tvArea, List<SymbolData> symbolsList) {
        // find colliding symbols (to be placed randomly)
        List<SymbolData> collidingSymbols = symbolsList.stream().filter(a -> symbolCollides(a, symbolsList)).collect(Collectors.toList());

        // try to find placement most optimal
        Snapshot snapshot = new Snapshot(symbolsList.stream().map(a -> a.getTopViewSymbol()).collect(Collectors.toList()));
        double best = Math.max(tvArea.getxWidth(), tvArea.getyLength()) * 2;
        snapshot.save();
        for (int i = 0; i < 1000000; i++) {
            randomlyPlaceSymbols(tvArea, symbolsList, collidingSymbols);
            // check if new setup is better then previous best
            double stats = getStatisticMeasurements(collidingSymbols);
            System.out.println(String.format("Statistic #%s: %s", i, stats));
            if (stats < best) {
                best = stats;
                snapshot.save();
                System.out.println("SAVED!!!");
            }
        }
        snapshot.load();
    }

    private double getStatisticMeasurements(List<SymbolData> symbolsList) {
        double accumulatedLength = 0;
        double maxLength = -1;
        double minLength = -1;
        for (SymbolData s : symbolsList) {
            double lineLength = MyMath.lineLength(s.getSymbolSignCentre(), s.getPointPoint());

            accumulatedLength += lineLength;
            maxLength = Math.max(maxLength, lineLength);
            minLength = minLength == -1 ? lineLength : Math.min(minLength, lineLength);
        }

        switch ("max") {
            case "av":
                return accumulatedLength / symbolsList.size();
            case "max":
                return maxLength;
            case "min":
                return minLength;
            case "accum":
                return accumulatedLength;
            default:
                throw new RuntimeException();
        }
    }

    private void randomlyPlaceSymbols(TopViewArea tvArea, List<SymbolData> symbolsList, List<SymbolData> symbolsToPlaceList) {
        // reset coordinates, so colliding symbols can be placed afterwards
        for (SymbolData s : symbolsToPlaceList) {
            s.setX(-1000000d);
            s.setY(-1000000d);
        }

        int attemptsCount = 1000 * symbolsToPlaceList.size();
        for (SymbolData s : symbolsToPlaceList) {
            while (attemptsCount >= 0) {
                attemptsCount--;

                double x = POINT_BORDER_MARGIN + nextC(tvArea.getxWidth() - 2 * POINT_BORDER_MARGIN - s.getXWidth());
                double y = POINT_BORDER_MARGIN + nextC(tvArea.getyLength() - 2 * POINT_BORDER_MARGIN - s.getYLength());
                s.setX(x);
                s.setY(y);
                // check if no collision, then break
                if (!symbolCollides(s, symbolsList)) {
                    break;
                }
            }
        }
    }

    private static double nextC(double max) {
        return Math.random() * max;
    }

    public static boolean symbolCollides(SymbolData s1, List<SymbolData> symbolsList) {
        // check if collides with other symbols
        for (SymbolData s2 : symbolsList) {
            if (s2 != s1 && twoSymbolsCollide(s1, s2)) {
                return true;
            }
        }
        // check if collides with points (dot where every symbol originates at)
        for (SymbolData point : symbolsList) {
            if (symbolCollidesWithPoint(s1, point)) {
                return true;
            }
        }
        return false;
    }

    private static boolean twoSymbolsCollide(SymbolData s1, SymbolData s2) {
        return MyMath.rectanglesCollide(s1.getSymbolRect(), s2.getSymbolRect());
    }

    private static boolean symbolCollidesWithPoint(SymbolData s1, SymbolData p2) {
        return MyMath.rectanglesCollide(s1.getSymbolRect(), p2.getPointRect());
    }

    public static class SymbolData {
        private TopViewSymbol topViewSymbol;
        private TopViewRenderingEngine renderingEngine;

        private Rect symbolRect;
        private Double xWidth, yLength; // symbol rectangle dimensions

        private Rect pointRect;
        private Point pointPoint;

        private Double symbolSignXWidth, symbolSignYLength;
        private Double symbolSignCentreXRelative, symbolSignCentreYRelative;
        private Point symbolSignPoint1;
        private Point symbolSignCentrePoint;

        public SymbolData(TopViewSymbol topViewSymbol, TopViewRenderingEngine renderingEngine) {
            this.topViewSymbol = topViewSymbol;
            this.renderingEngine = renderingEngine;
        }

        public TopViewSymbol getTopViewSymbol() {
            return topViewSymbol;
        }

        public void setX(double x) {
            resetSymbolXYRelatedData();
            topViewSymbol.setX(x);
        }

        public void setY(double y) {
            resetSymbolXYRelatedData();
            topViewSymbol.setY(y);
        }

        private void resetSymbolXYRelatedData() {
            symbolRect = null;
            symbolSignPoint1 = null;
            symbolSignCentrePoint = null;
        }

        public void setLabelAlignment(TopViewSymbol.Label2SymbolAlignment labelAlignment) {
            resetSymbolXYRelatedData();
            xWidth = yLength = null;
            symbolSignXWidth = symbolSignYLength = null;
            symbolSignCentreXRelative = symbolSignCentreYRelative = null;

            topViewSymbol.setLabelAlignment(labelAlignment);
        }

        public Rect getSymbolRect() {
            if (symbolRect == null) {
                symbolRect = new Rect(topViewSymbol.getX(), topViewSymbol.getY(), topViewSymbol.getX() + getXWidth(),
                        topViewSymbol.getY() + getYLength());
            }
            return symbolRect;
        }

        public Double getXWidth() {
            if (xWidth == null) {
                xWidth = renderingEngine.getSymbolXWidth(topViewSymbol);
            }
            return xWidth;
        }

        public Double getYLength() {
            if (yLength == null) {
                yLength = renderingEngine.getSymbolYLength(topViewSymbol);
            }
            return yLength;
        }

        public Point getPointPoint() {
            if (pointPoint == null) {
                pointPoint = new Point(topViewSymbol.getPointX(), topViewSymbol.getPointY());
            }
            return pointPoint;
        }

        public Rect getPointRect() {
            if (pointRect == null) {
                pointRect = new Rect(topViewSymbol.getPointX() - 2, topViewSymbol.getPointY() - 2,
                        topViewSymbol.getPointX() + 2, topViewSymbol.getPointY() + 2);
            }
            return pointRect;
        }


        public Double getSymbolSignXWidth() {
            if (symbolSignXWidth == null) {
                symbolSignXWidth = renderingEngine.getSymbolSignXWidth(topViewSymbol.getPointType());
            }
            return symbolSignXWidth;
        }

        public Double getSymbolSignYLength() {
            if (symbolSignYLength == null) {
                symbolSignYLength = renderingEngine.getSymbolSignYLength(topViewSymbol.getPointType());
            }
            return symbolSignYLength;
        }

        public Double getSymbolSignCentreXRelative() {
            if (symbolSignCentreXRelative == null) {
                symbolSignCentreXRelative = renderingEngine.getSymbolSignXRelative(topViewSymbol) + getSymbolSignXWidth() / 2.0;
            }
            return symbolSignCentreXRelative;
        }

        public Double getSymbolSignCentreYRelative() {
            if (symbolSignCentreYRelative == null) {
                symbolSignCentreYRelative = renderingEngine.getSymbolSignXRelative(topViewSymbol) + getSymbolSignYLength() / 2.0;
            }
            return symbolSignCentreYRelative;
        }

        public Point getSymbolSignPoint1() {
            if (symbolSignPoint1 == null) {
                double symbolSignX = getSymbolRect().getX1() + renderingEngine.getSymbolSignXRelative(topViewSymbol);
                double symbolSignY = getSymbolRect().getY1() + renderingEngine.getSymbolSignYRelative(topViewSymbol);
                symbolSignPoint1 = new Point(symbolSignX, symbolSignY);
            }
            return symbolSignPoint1;
        }

        public Point getSymbolSignCentre() {
            if (symbolSignCentrePoint == null) {
                double symbolSignXCentre;
                double symbolSignYCentre;
                if (true) {
                    symbolSignXCentre = getSymbolRect().getX1() + getSymbolSignCentreXRelative();
                    symbolSignYCentre = getSymbolRect().getY1() + getSymbolSignCentreYRelative();
                } else {
                    symbolSignXCentre = getSymbolSignPoint1().getX() + getSymbolSignXWidth() / 2.0;
                    symbolSignYCentre = getSymbolSignPoint1().getY() + getSymbolSignYLength() / 2.0;
                }
                symbolSignCentrePoint = new Point(symbolSignXCentre, symbolSignYCentre);
            }
            return symbolSignCentrePoint;
        }

    }

}
