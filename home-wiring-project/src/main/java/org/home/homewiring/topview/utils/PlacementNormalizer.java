package org.home.homewiring.topview.utils;

import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.symbolsplacer.SymbolData;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlacementNormalizer {

    private double topViewAreaBorderMarging;

    public PlacementNormalizer(double topViewAreaBorderMarging) {
        this.topViewAreaBorderMarging = topViewAreaBorderMarging;
    }

    private interface ComputeDoubleFunction extends Function<SymbolData, Double> {
    }

    private Map<TopViewArea, ComputeDoubleFunction> computeMaxXFunctions = new HashMap<>();
    private Map<SymbolData, Double> computedMaxX = new HashMap<>();

    public double normalizeX(double x, TopViewArea tvArea, SymbolData symbol) {
        if (x < topViewAreaBorderMarging) {
            return topViewAreaBorderMarging;
        }
        if (!computeMaxXFunctions.containsKey(tvArea)) {
            computeMaxXFunctions.put(tvArea, s -> tvArea.getxWidth() - s.getXWidth() - topViewAreaBorderMarging);
        }
        double maxX = computedMaxX.computeIfAbsent(symbol, computeMaxXFunctions.get(tvArea));
        if (x > maxX) {
            return maxX;
        }
        return x;
    }

    private Map<TopViewArea, ComputeDoubleFunction> computeMaxYFunctions = new HashMap<>();
    private Map<SymbolData, Double> computedMaxY = new HashMap<>();

    public double normalizeY(double y, TopViewArea tvArea, SymbolData symbol) {
        if (y < topViewAreaBorderMarging) {
            return topViewAreaBorderMarging;
        }
        if (!computeMaxYFunctions.containsKey(tvArea)) {
            computeMaxYFunctions.put(tvArea, s -> tvArea.getyLength() - s.getYLength() - topViewAreaBorderMarging);
        }
        double maxY = computedMaxY.computeIfAbsent(symbol, computeMaxYFunctions.get(tvArea));
        if (y > maxY) {
            return maxY;
        }
        return y;
    }
}
