package org.home.utils;

public class MyMath {

//    public static boolean rectanglesCollide(double r1x1, double r1y1, double r1x2, double r1y2, double r2x1, double r2y1, double r2x2, double r2y2) {
//
//    }

    /**
     * If any of the rectangles collide - true is returned. If rectangle is on the same line as the other rectangle - they are colliding too.
     */
    public static boolean rectanglesCollide(Rect rect1, Rect rect2) {
        // check if any of points of R2 are within R1
        if (rect1.isPointInside(rect2.getPoint1()) || rect1.isPointInside(rect2.getPoint2())) {
            return true;
        }
        if (rect1.isPointInside(rect2.getPoint3()) || rect1.isPointInside(rect2.getPoint4())) {
            return true;
        }

        // check if any of points of R1 are within R2
        if (rect2.isPointInside(rect1.getPoint1()) || rect2.isPointInside(rect1.getPoint2())) {
            return true;
        }
        if (rect2.isPointInside(rect1.getPoint3()) || rect2.isPointInside(rect1.getPoint4())) {
            return true;
        }

        // otherwise
        return false;
    }

    /**
     * If any of the rectangles collide - true is returned.
     * If rectangle is on the same line as the other rectangle (rectangles are adjacent) -
     * then it is not counted as colliding, so false will be returned..
     */
    public static boolean rectanglesCollideExcludeAdjacent(Rect rect1, Rect rect2) {
        // check if any of points of R2 are within R1
        if (rect1.isPointInsideExcludeBorders(rect2.getPoint1()) || rect1.isPointInsideExcludeBorders(rect2.getPoint2())) {
            return true;
        }
        if (rect1.isPointInsideExcludeBorders(rect2.getPoint3()) || rect1.isPointInsideExcludeBorders(rect2.getPoint4())) {
            return true;
        }

        // check if any of points of R1 are within R2
        if (rect2.isPointInsideExcludeBorders(rect1.getPoint1()) || rect2.isPointInsideExcludeBorders(rect1.getPoint2())) {
            return true;
        }
        if (rect2.isPointInsideExcludeBorders(rect1.getPoint3()) || rect2.isPointInsideExcludeBorders(rect1.getPoint4())) {
            return true;
        }

        // otherwise
        return false;
    }

    public static double lineLength(Point p1, Point p2) {
        return lineLength(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public static double lineLength(double x1, double y1, double x2, double y2) {
        double lineLengthOnX = (x2 - x1);
        lineLengthOnX *= lineLengthOnX;
        double lineLengthOnY = (y2 - y1);
        lineLengthOnY *= lineLengthOnY;
        return Math.sqrt(lineLengthOnX + lineLengthOnY);
    }
}
