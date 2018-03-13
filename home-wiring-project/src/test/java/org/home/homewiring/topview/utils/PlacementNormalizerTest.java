package org.home.homewiring.topview.utils;

import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.homewiring.topview.renderer.svg.SVGRenderingEngine;
import org.home.homewiring.topview.symbolsplacer.SymbolData;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.home.homewiring.utils.MyMathTest.ASSERTION_DELTA;
import static org.junit.Assert.assertEquals;

public class PlacementNormalizerTest {

    private static final double BORDER_MARGING = 10.0;

    private static PlacementNormalizer placementNormalizer;
    private static TopViewArea tvArea;
    private static TopViewSymbol tvSymbol1;
    private static TopViewRenderingEngine tvRenderingEngine;

    private static SymbolData symbolData1;

    @BeforeClass
    public static void setupClass() {
        placementNormalizer = new PlacementNormalizer(BORDER_MARGING);

        tvRenderingEngine= new SVGRenderingEngine();

        tvArea = new TopViewArea();
        tvArea.setxWidth(100.0);
        tvArea.setyLength(150.0);

        tvSymbol1 = new TopViewSymbol();
        tvSymbol1.setPointType("M");
        tvSymbol1.setLabelText("MK1");
        tvSymbol1.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.BELOW);

        symbolData1 = new SymbolData(tvSymbol1, tvRenderingEngine);
    }

    @Test
    public void testNormalizeXLowValues() {
        double x = placementNormalizer.normalizeX(5, tvArea, symbolData1);
        assertEquals(BORDER_MARGING, x, ASSERTION_DELTA);

        x = placementNormalizer.normalizeX(-20, tvArea, symbolData1);
        assertEquals(BORDER_MARGING, x, ASSERTION_DELTA);

        x = placementNormalizer.normalizeX(-1000020, tvArea, symbolData1);
        assertEquals(BORDER_MARGING, x, ASSERTION_DELTA);
    }

    @Test
    public void testNormalizeXNormalValues() {
        double x = placementNormalizer.normalizeX(10, tvArea, symbolData1);
        assertEquals(10, x, ASSERTION_DELTA);

        x = placementNormalizer.normalizeX(11, tvArea, symbolData1);
        assertEquals(11, x, ASSERTION_DELTA);

        x = placementNormalizer.normalizeX(69, tvArea, symbolData1);
        assertEquals(69, x, ASSERTION_DELTA);
    }

    @Test
    public void testNormalizeXHighValues() {
        double expectedX = tvArea.getxWidth() - symbolData1.getXWidth() - BORDER_MARGING;
        double x = placementNormalizer.normalizeX(10000, tvArea, symbolData1);
        assertEquals(expectedX, x, ASSERTION_DELTA);

        x = placementNormalizer.normalizeX(90, tvArea, symbolData1);
        assertEquals(expectedX, x, ASSERTION_DELTA);

        x = placementNormalizer.normalizeX(70, tvArea, symbolData1);
        assertEquals(70, x, ASSERTION_DELTA);
    }

}
