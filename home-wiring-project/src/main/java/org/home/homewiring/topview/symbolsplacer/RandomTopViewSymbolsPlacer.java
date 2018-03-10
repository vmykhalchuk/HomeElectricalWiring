package org.home.homewiring.topview.symbolsplacer;

import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.homewiring.topview.symbolsplacer.utils.Snapshot;
import org.home.utils.MyMath;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class RandomTopViewSymbolsPlacer extends TopViewSymbolsPlacer {

    public RandomTopViewSymbolsPlacer(TopViewModel topViewModel, TopViewRenderingEngine renderingEngine) {
        super(topViewModel, renderingEngine);
    }

    public void placeSymbolsProperly() {
        super.placeSymbolsProperly();

        for (final TopViewArea tvArea : topViewModel.getAreas()) {
            final List<SymbolData> symbolsList = new ArrayList<>();
            for (final TopViewSymbol tvSymbol : tvArea.getSymbols()) {
                symbolsList.add(new SymbolData(tvSymbol, renderingEngine));
            }

            // now place symbols randomly
            placeSymbolsProperlyForArea(tvArea, symbolsList);
        }
    }

    protected void placeSymbolsProperlyForArea(TopViewArea tvArea, List<SymbolData> symbolsList) {
        // find colliding symbols (to be placed randomly)
        List<SymbolData> collidingSymbols = symbolsList.stream().filter(a -> symbolCollides(a, symbolsList, false)).collect(Collectors.toList());

        // try to find placement most optimal
        Snapshot snapshot = new Snapshot(symbolsList);
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
            double lineLength = MyMath.lineLength(s.getSignCentre(), s.getPointPoint());

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
            s.setXY(-1000000d, -1000000d);
        }

        int attemptsCount = 1000 * symbolsToPlaceList.size();
        for (SymbolData s : symbolsToPlaceList) {
            while (attemptsCount >= 0) {
                attemptsCount--;

                double x = POINT_BORDER_MARGIN + nextC(tvArea.getxWidth() - 2 * POINT_BORDER_MARGIN - s.getXWidth());
                double y = POINT_BORDER_MARGIN + nextC(tvArea.getyLength() - 2 * POINT_BORDER_MARGIN - s.getYLength());
                s.setXY(x, y);
                // check if no collision, then break
                if (!symbolCollides(s, symbolsList, false)) {
                    break;
                }
            }
        }
    }

    private static double nextC(double max) {
        return Math.random() * max;
    }

    public static boolean symbolCollides(SymbolData s1, List<SymbolData> symbolsList, boolean excludeAdjacent) {
        // check if collides with other symbols
        for (SymbolData s2 : symbolsList) {
            if (s2 != s1 && twoSymbolsCollide(s1, s2, excludeAdjacent)) {
                return true;
            }
        }
        // check if collides with points (dot where every symbol originates at)
        for (SymbolData point : symbolsList) {
            if (symbolCollidesWithPoint(s1, point, excludeAdjacent)) {
                return true;
            }
        }
        return false;
    }

    private static boolean twoSymbolsCollide(SymbolData s1, SymbolData s2, boolean excludeAdjacent) {
        if (excludeAdjacent) {
            return MyMath.rectanglesCollideExcludeAdjacent(s1.getRect(), s2.getRect());
        } else {
            return MyMath.rectanglesCollide(s1.getRect(), s2.getRect());
        }
    }

    private static boolean symbolCollidesWithPoint(SymbolData s1, SymbolData p2, boolean excludeAdjacent) {
        if (excludeAdjacent) {
            return MyMath.rectanglesCollideExcludeAdjacent(s1.getRect(), p2.getPointRect());
        } else {
            return MyMath.rectanglesCollide(s1.getRect(), p2.getPointRect());
        }
    }

}
