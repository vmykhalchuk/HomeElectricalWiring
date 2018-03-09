package org.home.utils;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MyMathTest {

    public static double ASSERTION_DELTA = 0.0000000000001;

    @Test
    public void testRectanglesCollidePositive1() {
        Rect rect1 = new Rect(10, 10, 100, 100);
        Rect rect2 = new Rect(50, 50, 200, 200);

        assertEquals(true, MyMath.rectanglesCollide(rect1, rect2));
    }

    @Test
    public void testRectanglesCollideNegative1() {
        Rect rect1 = new Rect(10, 10, 100, 100);
        Rect rect2 = new Rect(50, 100.01, 200, 200);

        assertEquals(false, MyMath.rectanglesCollide(rect1, rect2));
    }

    @Test
    public void testLineLength1() {
        Point p1 = new Point(10, 10);
        Point p2 = new Point(10, 100);

        assertEquals(90, MyMath.lineLength(p1, p2), ASSERTION_DELTA);
    }
}
