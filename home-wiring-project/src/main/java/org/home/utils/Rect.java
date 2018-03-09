package org.home.utils;

public class Rect {
    private double x1, y1, x2, y2;
    private Point p1, p2, p3, p4;

    public Rect(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public boolean isPointInside(Point p) {
        return p.getX() >= x1 && p.getX() <= x2 && p.getY() >= y1 && p.getY() <= y2;
    }

    /**
     * When point is on the border of Rectangle - it is treated as not inside, so false is returned.
     */
    public boolean isPointInsideExcludeBorders(Point p) {
        return p.getX() > x1 && p.getX() < x2 && p.getY() > y1 && p.getY() < y2;
    }

    public Point getPoint1() {
        if (p1 == null) {
            p1 = new Point(x1, y1);
        }
        return p1;
    }

    public Point getPoint2() {
        if (p2 == null) {
            p2 = new Point(x2, y1);
        }
        return p2;
    }

    public Point getPoint3() {
        if (p3 == null) {
            p3 = new Point(x2, y2);
        }
        return p3;
    }

    public Point getPoint4() {
        if (p4 == null) {
            p4 = new Point(x1, y2);
        }
        return p4;
    }
}
