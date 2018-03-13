package org.home.homewiring.topview.utils;

import org.home.homewiring.utils.Rect;

import java.util.LinkedList;
import java.util.ListIterator;

import static org.home.homewiring.utils.MyMath.valueInRange;

public class CoordinatesNavigator {

    private Rect limitRect;
    private LinkedList<Double> placedSymbolsXCoordinatesSorted = new LinkedList<>();
    private LinkedList<Double> placedSymbolsYCoordinatesSorted = new LinkedList<>();

    public CoordinatesNavigator(Rect limitRect) {
        this.limitRect = limitRect;
    }

    public static void main(String[] args) {
        testNewCoordPlacement();
    }

    public static void testNewCoordPlacement() {
        CoordinatesNavigator gtp = new CoordinatesNavigator(null);
        gtp.registerNewCoordinateInPlacedList(1, gtp.placedSymbolsXCoordinatesSorted);
        gtp.registerNewCoordinateInPlacedList(2, gtp.placedSymbolsXCoordinatesSorted);
        gtp.registerNewCoordinateInPlacedList(3, gtp.placedSymbolsXCoordinatesSorted);
        gtp.registerNewCoordinateInPlacedList(1.5, gtp.placedSymbolsXCoordinatesSorted);
        gtp.registerNewCoordinateInPlacedList(0.5, gtp.placedSymbolsXCoordinatesSorted);
        System.out.println(gtp.placedSymbolsXCoordinatesSorted);
        System.out.println(gtp.findPrevX(1.52, 1.55));
    }

    private void registerNewCoordinateInPlacedList(double coord, LinkedList<Double> coordsList) {
        ListIterator<Double> iterator = coordsList.listIterator();
        ListIterator<Double> iterator2 = coordsList.listIterator();
        while (iterator.hasNext()) {
            double v = iterator.next();
            if (v == coord) {
                return;
            }
            if (v > coord) {
                iterator2.add(coord);
                return;
            }
            iterator2.next();
        }
        iterator.add(coord);
    }

    public void registerNewX(double x) {
        if (limitRect != null && !valueInRange(x, limitRect.getX1(), limitRect.getX2())) {
            return;
        }

        registerNewCoordinateInPlacedList(x, placedSymbolsXCoordinatesSorted);
    }

    public void registerNewY(double y) {
        if (limitRect != null && !valueInRange(y, limitRect.getY1(), limitRect.getY2())) {
            return;
        }

        registerNewCoordinateInPlacedList(y, placedSymbolsYCoordinatesSorted);
    }

    public void registerNewRect(Rect rect) {
        registerNewX(rect.getX1());
        registerNewY(rect.getY1());
        registerNewX(rect.getX2());
        registerNewY(rect.getY2());
    }

    public Double findPrevY(double y) {
        for (int i = placedSymbolsYCoordinatesSorted.size() - 1; i >= 0; i--) {
            Double prevY = placedSymbolsYCoordinatesSorted.get(i);
            if (prevY < y) {
                return prevY;
            }
        }
        return null;
    }

    public Double findNextY(double y) {
        for (int i = 0; i < placedSymbolsYCoordinatesSorted.size(); i++) {
            Double nextY = placedSymbolsYCoordinatesSorted.get(i);
            if (nextY > y) {
                return nextY;
            }
        }
        return null;
    }

    public Double findPrevX(double x) {
        for (int i = placedSymbolsXCoordinatesSorted.size() - 1; i >= 0; i--) {
            Double prevX = placedSymbolsXCoordinatesSorted.get(i);
            if (prevX < x) {
                return prevX;
            }
        }
        return null;
    }

    @Deprecated
    public Double findPrevX(double x, double extraXToTakeIntoAccount) {
        for (int i = placedSymbolsXCoordinatesSorted.size() - 1; i >= 0; i--) {
            Double prevX = placedSymbolsXCoordinatesSorted.get(i);
            Double prevPrevX = i < placedSymbolsXCoordinatesSorted.size() - 1 ? placedSymbolsXCoordinatesSorted.get(i + 1) : null;
            if (extraXToTakeIntoAccount > prevX && extraXToTakeIntoAccount < prevPrevX) {
                if (extraXToTakeIntoAccount < x) {
                    return extraXToTakeIntoAccount;
                }
            }
            if (prevX < x) {
                return prevX;
            }
        }
        return null;
    }

    public Double findNextX(double x) {
        for (int i = 0; i < placedSymbolsXCoordinatesSorted.size(); i++) {
            Double nextX = placedSymbolsXCoordinatesSorted.get(i);
            if (nextX > x) {
                return nextX;
            }
        }
        return null;
    }
}
