package org.home.homewiring.topview;

import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.Snapshot;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.homewiring.util.Pair;
import org.home.homewiring.util.Tripple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.home.homewiring.topview.renderer.svg.SVGRendererHelper.getSymbolSignXRelative;
import static org.home.homewiring.topview.renderer.svg.SVGRendererHelper.getSymbolSignYRelative;

public class RandomTopViewSymbolsPlacer {

    public static double POINT_BORDER_MARGIN = 5; // 5 points from border // FIXME This constant depends on SVG / PNG Rendering engine!
    private static List<Pair<Double, Double>> snapshot = new ArrayList<>();

    private RandomTopViewSymbolsPlacer() {
    }

    private static void populateLabelAlignment(final TopViewSymbol tvSymbol, final TopViewArea tvArea) {
        final Utils.POINT_LOCATION location = Utils.locatePoint(tvSymbol.getPointX(), tvSymbol.getPointY(), tvArea.getxWidth(), tvArea.getyLength());
        switch (location) {
            case LEFT:
            case TOP_LEFT:
            case BOTTOM_LEFT:
                tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.RIGHT);
                break;
            case RIGHT:
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.LEFT);
                break;
            case TOP:
            case MIDDLE:
                tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.BELOW);
                break;
            case BOTTOM:
                tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.ABOVE);
                break;
            default:
                throw new RuntimeException(location.name());
        }
    }

    public static void placeSymbolsProperly(final TopViewModel topViewModel, final TopViewRenderingEngine renderingEngine) {
        for (final TopViewArea tvArea : topViewModel.getAreas()) {
            final List<SymbolWidthLengthTripple> symbolsList = new ArrayList<>();
            for (final TopViewSymbol tvSymbol : tvArea.getSymbols()) {
                // FIXME Detect which tvSymbol is placed manually - so we do not override its configuration!

                populateLabelAlignment(tvSymbol, tvArea);

                double symbolXWidth = renderingEngine.getSymbolXWidth(tvSymbol);
                double symbolYLength = renderingEngine.getSymbolYLength(tvSymbol);
                Utils.POINT_LOCATION location = Utils.locatePoint(tvSymbol.getPointX(), tvSymbol.getPointY(), tvArea.getxWidth(), tvArea.getyLength());

                switch (location) {
                    case LEFT:
                        tvSymbol.setX(tvSymbol.getPointX() + POINT_BORDER_MARGIN);
                        tvSymbol.setY(tvSymbol.getPointY() - symbolYLength / 2);
                        break;
                    case RIGHT:
                    case TOP_RIGHT: // FIXME assure Y is not out of range
                    case BOTTOM_RIGHT: // FIXME assure Y is not out of range
                        tvSymbol.setX(tvSymbol.getPointX() - symbolXWidth - POINT_BORDER_MARGIN);
                        tvSymbol.setY(tvSymbol.getPointY() - symbolYLength / 2);
                        break;
                    case TOP:
                    case TOP_LEFT:
                    case MIDDLE:
                        tvSymbol.setX(Math.max(tvSymbol.getPointX() - symbolXWidth / 2, POINT_BORDER_MARGIN));
                        tvSymbol.setY(tvSymbol.getPointY() + POINT_BORDER_MARGIN);
                        break;
                    case BOTTOM:
                    case BOTTOM_LEFT:
                        tvSymbol.setX(Math.max(tvSymbol.getPointX() - symbolXWidth / 2, POINT_BORDER_MARGIN));
                        tvSymbol.setY(tvSymbol.getPointY() - symbolYLength - POINT_BORDER_MARGIN);
                        break;
                }

                symbolsList.add(new SymbolWidthLengthTripple(tvSymbol, symbolXWidth, symbolYLength));
            }

            // now place symbols randomly
            randomizedPlacing(tvArea, symbolsList, renderingEngine);
        }
    }

    private static void randomizedPlacing(final TopViewArea tvArea, final List<SymbolWidthLengthTripple> symbolsList, final TopViewRenderingEngine renderingEngine) {
        // find colliding symbols (to be placed randomly)
        final List<SymbolWidthLengthTripple> collidingSymbols = symbolsList.stream().filter(a -> symbolCollides(a, symbolsList)).collect(Collectors.toList());

        // try to find placement most optimal
        Snapshot snapshot = new Snapshot(symbolsList.stream().map(a -> a.getU()).collect(Collectors.toList()));
        double best = Math.max(tvArea.getxWidth(), tvArea.getyLength()) * 2;
        snapshot.save();
        for (int i = 0; i < 1000000; i++) {
            placeRandomlyCollidingSymbols(tvArea, symbolsList, collidingSymbols);
            // check if new setup is better then previous best
            double stats = getStatisticMeasurements(collidingSymbols, renderingEngine);
            System.out.println(String.format("Statistic #%s: %s", i, stats));
            if (stats < best) {
                best = stats;
                snapshot.save();
                System.out.println("SAVED!!!");
            }
        }
        snapshot.load();
    }

    private static double getStatisticMeasurements(final List<SymbolWidthLengthTripple> symbolsList, final TopViewRenderingEngine renderingEngine) {
        double accumulatedLength = 0;
        double maxLength = -1;
        double minLength = -1;
        for (SymbolWidthLengthTripple s : symbolsList) {
            double pointX = s.getU().getPointX();
            double pointY = s.getU().getPointY();
            double symbolX = s.getU().getX();
            double symbolY = s.getU().getY();
            double symbolXWidth = s.getXWidth();
            double symbolYLength = s.getYLength();
            double symbolSignXWidth = renderingEngine.getSymbolSignXWidth(s.getU().getPointType());
            double symbolSignYLength = renderingEngine.getSymbolSignYLength(s.getU().getPointType());
            double symbolSignX = symbolX + getSymbolSignXRelative(s.getU(), symbolXWidth, symbolSignXWidth);
            double symbolSignY = symbolY + getSymbolSignYRelative(s.getU(), symbolYLength, symbolSignYLength);
            double symbolSignXCentre = symbolSignX + symbolSignXWidth / 2;
            double symbolSignYCentre = symbolSignY + symbolSignYLength / 2;

            double lineLengthOnX = (symbolSignXCentre - pointX);
            lineLengthOnX *= lineLengthOnX;
            double lineLengthOnY = (symbolSignYCentre - pointY);
            lineLengthOnY *= lineLengthOnY;
            double lineLength = Math.sqrt(lineLengthOnX + lineLengthOnY);

            accumulatedLength += lineLength;

            maxLength = Math.max(maxLength, lineLength);

            minLength = minLength == -1 ? lineLength : Math.min(minLength, lineLength);
        }
        //return accumulatedLength / symbolsList.size();
        return maxLength;
        //return minLength;
        //return accumulatedLength;
    }

    private static void placeRandomlyCollidingSymbols(final TopViewArea tvArea, final List<SymbolWidthLengthTripple> symbolsList, final List<SymbolWidthLengthTripple> collidingSymbolsList) {
        // reset coordinates, so colliding symbols can be placed afterwards
        for (SymbolWidthLengthTripple s : collidingSymbolsList) {
            s.getU().setX(-1000000d);
            s.getU().setY(-1000000d);
        }

        int attemptsCount = 1000 * collidingSymbolsList.size();
        for (SymbolWidthLengthTripple s : collidingSymbolsList) {
            while (attemptsCount >= 0) {
                attemptsCount--;

                double x = POINT_BORDER_MARGIN + nextC(tvArea.getxWidth() - 2 * POINT_BORDER_MARGIN - s.getXWidth());
                double y = POINT_BORDER_MARGIN + nextC(tvArea.getyLength() - 2 * POINT_BORDER_MARGIN - s.getYLength());
                s.getU().setX(x);
                s.getU().setY(y);
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

    private static boolean symbolCollides(final SymbolWidthLengthTripple s1, final List<SymbolWidthLengthTripple> symbolsList) {
        // check if collides with other symbols
        for (SymbolWidthLengthTripple s2 : symbolsList) {
            if (s2 != s1 && twoSymbolsCollide(s1, s2)) {
                return true;
            }
        }
        // check if collides with points (dotswhere every symbol originates at)
        for (SymbolWidthLengthTripple point : symbolsList) {
            if (symbolCollidesWithPoint(s1, point)) {
                return true;
            }
        }
        return false;
    }

    private static boolean twoSymbolsCollide(SymbolWidthLengthTripple s1, SymbolWidthLengthTripple s2) {
        return rectanglesCollide(s1.getX(), s1.getY(), s1.getX2(), s1.getY2(), s2.getX(), s2.getY(), s2.getX2(), s2.getY2());
    }

    private static boolean symbolCollidesWithPoint(SymbolWidthLengthTripple s1, SymbolWidthLengthTripple p2) {
        return rectanglesCollide(s1.getX(), s1.getY(), s1.getX2(), s1.getY2(), p2.getPointX1(), p2.getPointY1(), p2.getPointX2(), p2.getPointY2());
    }

    private static boolean rectanglesCollide(double r1x1, double r1y1, double r1x2, double r1y2, double r2x1, double r2y1, double r2x2, double r2y2) {
        // check if any of points of R2 are within R1
        if (pointInRectangle(r1x1, r1y1, r1x2, r1y2, r2x1, r2y1)) {
            return true;
        }
        if (pointInRectangle(r1x1, r1y1, r1x2, r1y2, r2x2, r2y1)) {
            return true;
        }
        if (pointInRectangle(r1x1, r1y1, r1x2, r1y2, r2x2, r2y2)) {
            return true;
        }
        if (pointInRectangle(r1x1, r1y1, r1x2, r1y2, r2x1, r2y2)) {
            return true;
        }

        // check if any of points of R1 are within R2
        if (pointInRectangle(r2x1, r2y1, r2x2, r2y2, r1x1, r1y1)) {
            return true;
        }
        if (pointInRectangle(r2x1, r2y1, r2x2, r2y2, r1x2, r1y1)) {
            return true;
        }
        if (pointInRectangle(r2x1, r2y1, r2x2, r2y2, r1x2, r1y2)) {
            return true;
        }
        if (pointInRectangle(r2x1, r2y1, r2x2, r2y2, r1x1, r1y2)) {
            return true;
        }

        // otherwise
        return false;
    }

    private static boolean pointInRectangle(double rx1, double ry1, double rx2, double ry2, double px, double py) {
        return px >= rx1 && px <= rx2 && py >= ry1 && py <= ry2;
    }

    public static class SymbolWidthLengthTripple extends Tripple<TopViewSymbol, Double, Double> {
        public SymbolWidthLengthTripple(TopViewSymbol topViewSymbol, Double width, Double length) {
            super(topViewSymbol, width, length);
        }

        public Double getX() {
            return getU().getX();
        }

        public Double getY() {
            return getU().getY();
        }

        public Double getX2() {
            return getU().getX() + getXWidth();
        }

        public Double getY2() {
            return getU().getY() + getYLength();
        }

        public Double getXWidth() {
            return getV();
        }

        public Double getYLength() {
            return getW();
        }

        public Double getPointX1() {
            return getU().getPointX() - 2;
        }

        public Double getPointY1() {
            return getU().getPointY() - 2;
        }

        public Double getPointX2() {
            return getU().getPointX() + 2;
        }

        public Double getPointY2() {
            return getU().getPointY() + 2;
        }
    }

}
