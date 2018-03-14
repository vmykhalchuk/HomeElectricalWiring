package org.home.homewiring.topview.symbolsplacer;

import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.homewiring.topview.utils.CoordinatesNavigator;
import org.home.homewiring.topview.utils.PlacementNormalizer;
import org.home.homewiring.utils.MyMath;
import org.home.homewiring.utils.Point;
import org.home.homewiring.utils.Rect;

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
        List<SymbolData> symbolsListWIthPadding = symbolsList.stream().map(s -> new SymbolData(s, new TVRenderingEngineWrapperWithPaddingAccounted(renderingEngine))).collect(Collectors.toList());

        // find colliding symbols (to be placed randomly)
        List<SymbolData> collidingSymbols = symbolsListWIthPadding.stream().filter(a -> symbolCollides(a, symbolsListWIthPadding, false)).collect(Collectors.toList());
        for (SymbolData s : collidingSymbols) {
            s.setXY(-1000000d, -1000000d);
        }

        List<Integer> prioritiesList = new ArrayList<>(collidingSymbols.size());
        for (int i = 0; i < collidingSymbols.size(); i++) {
            prioritiesList.add(i);
        }
        placeSymbolsOptimally(tvArea, symbolsListWIthPadding, collidingSymbols, prioritiesList);
    }

    private void placeSymbolsOptimally(TopViewArea tvArea, List<SymbolData> symbolsList, List<SymbolData> symbolsToPlace, List<Integer> symbolsToPlacePriorities) {
        // reset coordinates, so colliding symbols can be placed afterwards
        for (SymbolData s : symbolsToPlace) {
            s.setXY(-1000000d, -1000000d);
        }

        List<SymbolData> placedSymbolsList = symbolsList.stream().filter(s -> !symbolsToPlace.contains(s)).collect(Collectors.toList());
        CoordinatesNavigator nav = createCoordinatesNavigator(tvArea, symbolsList, placedSymbolsList);

        for (Integer prio : symbolsToPlacePriorities) {
            SymbolData symbolToPlace = symbolsToPlace.get(prio);

            PlacementResult placementResult = placeSymbolOptimallyRightFromPoint(tvArea, symbolsList, symbolToPlace, nav);
            if (placementResult == null) {
                throw new RuntimeException("Unable to find proper location for symbol: " + symbolToPlace.getLabelText());
            }
            symbolToPlace.setXY(placementResult.getX(), placementResult.getY());

            // Register symbol
            placedSymbolsList.add(symbolToPlace);
            nav.registerNewRect(symbolToPlace.getRect());
        }

        // register artificial symbols representing Area border margins, so symbols are not colliding with border margin
        placedSymbolsList.add(new AreaBorderSymbolData(new Rect(0, 0,
                tvArea.getxWidth(), POINT_BORDER_MARGIN))); // top margin
        placedSymbolsList.add(new AreaBorderSymbolData(new Rect(0, tvArea.getyLength() - POINT_BORDER_MARGIN,
                tvArea.getxWidth(), tvArea.getyLength()))); // bottom margin
        placedSymbolsList.add(new AreaBorderSymbolData(new Rect(0, 0,
                POINT_BORDER_MARGIN, tvArea.getyLength()))); // left margin
        placedSymbolsList.add(new AreaBorderSymbolData(new Rect(tvArea.getxWidth() - POINT_BORDER_MARGIN, 0,
                tvArea.getxWidth(), tvArea.getyLength()))); // right margin
    }

    public static class AreaBorderSymbolData extends SymbolData {

        private Rect rect;

        public AreaBorderSymbolData(Rect rect) {
            super(new Point(0, 0), null, null);
            this.rect = rect;
        }

        @Override
        public Rect getRect() {
            return rect;
        }
    }

    private CoordinatesNavigator createCoordinatesNavigator(TopViewArea tvArea, List<SymbolData> symbolsList, List<SymbolData> placedSymbolsList) {
        Rect tvAreaRect = new Rect(new Point(0, 0), tvArea.getxWidth(), tvArea.getyLength());
        CoordinatesNavigator nav = new CoordinatesNavigator(tvAreaRect);
        for (SymbolData placedSymbol : placedSymbolsList) {
            nav.registerNewRect(placedSymbol.getRect());
        }
        for (SymbolData symbol : symbolsList) {
            nav.registerNewRect(symbol.getPointRect());
        }
        // corner cases
        nav.registerNewRect(tvAreaRect);
        nav.registerNewRect(new Rect(POINT_BORDER_MARGIN, POINT_BORDER_MARGIN,
                tvArea.getxWidth() - POINT_BORDER_MARGIN, tvArea.getyLength() - POINT_BORDER_MARGIN));
        return nav;
    }

    private PlacementResult placeSymbolOptimallyRightFromPoint(TopViewArea tvArea, List<SymbolData> allSymbolsList, SymbolData symbolToPlace, CoordinatesNavigator coordinatesNavigator) {
        final double symbolXWidth = symbolToPlace.getXWidth();
        final double symbolYLength = symbolToPlace.getYLength();
        final double minY = POINT_BORDER_MARGIN;
        final double maxY = tvArea.getyLength() - symbolYLength - POINT_BORDER_MARGIN;
        final double minX = POINT_BORDER_MARGIN;
        final double maxX = tvArea.getxWidth() - symbolXWidth - POINT_BORDER_MARGIN;

        State state = new State(tvArea, allSymbolsList, symbolToPlace);

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

        final double initialX = symbolToPlace.getPointPoint().getX() - symbolToPlace.getSignCentreXRelative();
        final double initialY = symbolToPlace.getPointPoint().getY() - symbolToPlace.getSignCentreYRelative();

        state.x = initialX;

        // Move from left to right by dragging right border of the symbol
        state.y = initialY;
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
        state.y = initialY;
        lastOne = false;
        do {
            // decide if we are far enough to stop cycle
            if (state.x < initialX && !isCurrentXYBetterThenBest(state)) {
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
        state.y = initialY;
        lastOne = false;
        do {
            // decide if we are far enough to stop cycle
            if (state.x > initialX && !isCurrentXYBetterThenBest(state)) {
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
            boolean betterLength = state.best == null ? true : newLength < state.best.getLength();
            //if (!betterLength) {
            //    break; // already given length was best - we couldn't find any better
            //}
            if (betterLength && !symbolCollides(state.symbolToPlace, state.allSymbolsList, true) /*FIXME try to avoid this adjacent!*/) {
                state.best = new PlacementResult(normalizedX, normalizedY, newLength);
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

    private static class State {
        protected TopViewArea tvArea;
        protected List<SymbolData> allSymbolsList;
        protected SymbolData symbolToPlace;
        protected double x;
        protected double y;
        protected PlacementResult best;

        public State(TopViewArea tvArea, List<SymbolData> allSymbolsList, SymbolData symbolToPlace) {
            this.tvArea = tvArea;
            this.allSymbolsList = allSymbolsList;
            this.symbolToPlace = symbolToPlace;
        }
    }
}
