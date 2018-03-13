package org.home.homewiring.topview.symbolsplacer.utils;

import org.home.utils.Point;
import org.home.utils.Rect;
import org.junit.Test;

import static org.home.utils.MyMathTest.ASSERTION_DELTA;
import static org.junit.Assert.assertEquals;

public class CoordinatesNavigatorTest {

    private Rect limitRect = new Rect(new Point(0, 0), 100, 100);
    private CoordinatesNavigator nav = new CoordinatesNavigator(limitRect);

    @Test
    public void testNewCoordPlacement() {
        nav.registerNewX(1);
        nav.registerNewX(2);
        nav.registerNewX(3);
        nav.registerNewX(1.5);
        nav.registerNewX(0.5);
        assertEquals(1.5, nav.findNextX(1.2), ASSERTION_DELTA);
    }
}
