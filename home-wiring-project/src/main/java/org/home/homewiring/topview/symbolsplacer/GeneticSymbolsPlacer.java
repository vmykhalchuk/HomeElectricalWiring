package org.home.homewiring.topview.symbolsplacer;

import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.homewiring.topview.symbolsplacer.utils.CoordinatesNavigator;
import org.home.homewiring.topview.symbolsplacer.utils.PlacementNormalizer;
import org.home.utils.MyMath;
import org.home.utils.Point;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneticSymbolsPlacer extends RandomTopViewSymbolsPlacer {

    private PlacementNormalizer normalizer = new PlacementNormalizer(POINT_BORDER_MARGIN);

    public GeneticSymbolsPlacer(TopViewModel topViewModel, TopViewRenderingEngine renderingEngine) {
        super(topViewModel, renderingEngine);
    }

    private static Double negateSafely(Double d1, double d2) {
        return d1 == null ? null : d1 - d2;
    }

    public void placeSymbolsProperly() {
        super.placeSymbolsProperly();
    }

    protected void placeSymbolsProperlyForArea(TopViewArea tvArea, List<SymbolData> symbolsList) {
        // find colliding symbols (to be placed randomly)
        List<SymbolData> collidingSymbols = symbolsList.stream().filter(a -> symbolCollides(a, symbolsList, false)).collect(Collectors.toList());

        List<Integer> prioritiesList = new ArrayList<>(collidingSymbols.size());
        for (int i = 0; i < collidingSymbols.size(); i++) {
            prioritiesList.add(i);
        }
        placeSymbolsOptimally(tvArea, symbolsList, collidingSymbols, prioritiesList);
    }

    private void placeSymbolsOptimally(TopViewArea tvArea, List<SymbolData> symbolsList, List<SymbolData> symbolsToPlace, List<Integer> symbolsToPlacePriorities) {
        // reset coordinates, so colliding symbols can be placed afterwards
        for (SymbolData s : symbolsToPlace) {
            s.setXY(-1000000d, -1000000d);
        }

        List<SymbolData> placedSymbolsList = symbolsList.stream().filter(s -> !symbolsToPlace.contains(s)).collect(Collectors.toList());
        CoordinatesNavigator nav = createCoordinatesNavigator(symbolsList, placedSymbolsList);

        for (Integer prio : symbolsToPlacePriorities) {
            SymbolData symbolToPlace = symbolsToPlace.get(prio);

            PlacementResult placementResult = placeSymbolOptimallyRightFromPoint(tvArea, placedSymbolsList, symbolToPlace, nav);
            if (placementResult == null) {
                throw new RuntimeException("Unable to find proper location for symbol: " + symbolToPlace.getLabelText());
            }
            symbolToPlace.setXY(placementResult.getX(), placementResult.getY());

            // Register symbol
            placedSymbolsList.add(symbolToPlace);
            nav.registerNewRect(symbolToPlace.getRect());
        }
    }

    private CoordinatesNavigator createCoordinatesNavigator(List<SymbolData> symbolsList, List<SymbolData> placedSymbolsList) {
        CoordinatesNavigator nav = new CoordinatesNavigator();
        for (SymbolData placedSymbol : placedSymbolsList) {
            nav.registerNewRect(placedSymbol.getRect());
        }
        for (SymbolData symbol : symbolsList) {
            nav.registerNewRect(symbol.getPointRect());
        }
        return nav;
    }

    private PlacementResult placeSymbolOptimallyRightFromPoint(TopViewArea tvArea, List<SymbolData> placedSymbolsList, SymbolData symbolToPlace, CoordinatesNavigator coordinatesNavigator) {
        final double symbolXWidth = symbolToPlace.getXWidth();
        final double symbolYLength = symbolToPlace.getYLength();
        final double minY = POINT_BORDER_MARGIN;
        final double maxY = tvArea.getyLength() - symbolYLength - POINT_BORDER_MARGIN;
        final double minX = POINT_BORDER_MARGIN;
        final double maxX = tvArea.getxWidth() - symbolXWidth - POINT_BORDER_MARGIN;

        State state = new State(tvArea, placedSymbolsList, symbolToPlace);

        Function moveUpAndDownVertically = o -> {
            // move up (drag top of rectangle up)
            moveSymbolTillBetterFound(state, null, null,
                    _state -> coordinatesNavigator.findPrevY(state.y), minY);

            // now move down (drag bottom of rectangle down)
            moveSymbolTillBetterFound(state, null, null,
                    _state -> negateSafely(coordinatesNavigator.findNextY(state.y + symbolYLength), symbolYLength), maxY);

            // move up again (drag top of rectangle up)
            moveSymbolTillBetterFound(state, null, null,
                    _state -> coordinatesNavigator.findPrevY(state.y), minY);
            return null;
        };


        state.x = symbolToPlace.getPointRect().getX2();

        // Move from left to right by dragging right border of the symbol
        state.y = symbolToPlace.getPointPoint().getY() - symbolToPlace.getSignCentreYRelative();
        boolean lastOne = false;
        do {
            // decide if we are far enough to stop cycle
            if (!isCurrentXYBetterThenBest(state)) {
                break;
            }

            moveUpAndDownVertically.apply(null);

            if (lastOne) {
                break;
            }

            // move x to the right (drag right border of symbol)
            Double nextX = negateSafely(coordinatesNavigator.findNextX(state.x + symbolXWidth), symbolXWidth);
            if (nextX == null) {
                nextX = maxX;
                lastOne = true;
            }
            state.x = nextX;
        } while (true);


        // Move from right to left by dragging left border of the symbol
        state.y = symbolToPlace.getPointPoint().getY() - symbolToPlace.getSignCentreYRelative();
        lastOne = false;
        do {
            // decide if we are far enough to stop cycle
            if (!isCurrentXYBetterThenBest(state)) {
                break;
            }

            moveUpAndDownVertically.apply(null);

            if (lastOne) {
                break;
            }

            // move x to the left (drag left border of symbol)
            Double prevX = coordinatesNavigator.findPrevX(state.x);
            if (prevX == null) {
                prevX = minX;
                lastOne = true;
            }
            state.x = prevX;
        } while (true);

        // Move from left to right by dragging right border of the symbol
        state.y = symbolToPlace.getPointPoint().getY() - symbolToPlace.getSignCentreYRelative();
        lastOne = false;
        do {
            // decide if we are far enough to stop cycle
            if (!isCurrentXYBetterThenBest(state)) {
                break;
            }

            moveUpAndDownVertically.apply(null);

            if (lastOne) {
                break;
            }

            // move x to the right (drag right border of symbol)
            Double nextX = negateSafely(coordinatesNavigator.findNextX(state.x + symbolXWidth), symbolXWidth);
            if (nextX == null) {
                nextX = maxX;
                lastOne = true;
            }
            state.x = nextX;
        } while (true);

        return state.best;
    }

    private boolean isCurrentXYBetterThenBest(State state) {
        if (state.best == null) {
            return true;
        } else {
            return MyMath.lineLength(state.symbolToPlace.getSignCentre(), new Point(state.x, state.y)) < state.best.getLength();
        }
    }

    private void moveSymbolTillBetterFound(State state, Function<State, Double> computeNextX, Double lastX, Function<State, Double> computeNextY, Double lastY) {
        boolean lastOne = false;
        while (true) {
            double normalizedX = normalizer.normalizeX(state.x, state.tvArea, state.symbolToPlace);
            double normalizedY = normalizer.normalizeY(state.y, state.tvArea, state.symbolToPlace);

            assert normalizedX >= POINT_BORDER_MARGIN && normalizedX <= state.tvArea.getxWidth() - POINT_BORDER_MARGIN - state.symbolToPlace.getXWidth();
            assert normalizedY >= POINT_BORDER_MARGIN && normalizedY <= state.tvArea.getyLength() - POINT_BORDER_MARGIN - state.symbolToPlace.getYLength();
            state.symbolToPlace.setXY(normalizedX, normalizedY);

            double newLength = MyMath.lineLength(state.symbolToPlace.getPointPoint(), state.symbolToPlace.getSignCentre());
            boolean betterLength = state.best == null ? true : newLength > state.best.getLength();
            if (!betterLength) {
                break; // already given length was best - we couldn't find any better
            }
            if (betterLength && !symbolCollides(state.symbolToPlace, state.placedSymbolsList, true) /*FIXME try to avoid this adjacent!*/) {
                state.best = new PlacementResult(newLength, normalizedX, normalizedY);
                break;
            } else {
                if (lastOne) {
                    break;
                }

                // compute next X | Y
                if (computeNextX != null) {
                    Double nextX = computeNextX.apply(state);
                    if (nextX == null) {
                        nextX = lastX;
                        lastOne = true;
                    }
                    state.x = nextX;
                } else if (computeNextY != null) {
                    Double nextY = computeNextY.apply(state);
                    if (nextY == null) {
                        state.y = lastY;
                        lastOne = true;
                    } else {
                        state.y = nextY;
                    }
                }
            }
        }
    }

    private void placeSymbolOptimallyBelowFromPoint(TopViewArea tvArea, List<SymbolData> symbolsList, SymbolData symbolToPlace) {
        double startPointX = symbolToPlace.getPointPoint().getX();
        double startPointY = symbolToPlace.getPointRect().getY2();
        double symbolSignCentreXRelative = symbolToPlace.getSignCentre().getX() - symbolToPlace.getRect().getX1();

        double startingX = startPointX - symbolSignCentreXRelative;
        double startingY = startPointY;

    }

    private static class State {
        protected TopViewArea tvArea;
        protected List<SymbolData> placedSymbolsList;
        protected SymbolData symbolToPlace;
        protected double x;
        protected double y;
        protected PlacementResult best;

        public State(TopViewArea tvArea, List<SymbolData> placedSymbolsList, SymbolData symbolToPlace) {
            this.tvArea = tvArea;
            this.placedSymbolsList = placedSymbolsList;
            this.symbolToPlace = symbolToPlace;
        }
    }
}
