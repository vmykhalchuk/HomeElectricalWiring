package org.home.homewiring.utils;

/**
 * <b>Note:</b> Check here to understand layout
 * <a href="{@docRoot}/doc-files/rectangle_explained.png">Symbol layout<img src="{@docRoot}/doc-files/rectangle_explained.png" width="50%" height="50%"/></a>
 */
public class Rect {
    private double x1, y1, x2, y2;
    private Point p1, p2, p3, p4;
    private Point diamondP1, diamondP2, diamondP3, diamondP4;

    public Rect(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Rect(Point p1, double xWidth, double yLength) {
        this.p1 = p1;
        this.x1 = p1.getX();
        this.y1 = p1.getY();
        this.x2 = p1.getX() + xWidth;
        this.y2 = p1.getY() + yLength;
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

    public Point getDiamondP1() {
        if (diamondP1 == null) {
            diamondP1 = new Point(x1 + (x2 - x1) / 2.0, y1);
        }
        return diamondP1;
    }

    public Point getDiamondP3() {
        if (diamondP3 == null) {
            diamondP3 = new Point(x1 + (x2 - x1) / 2.0, y2);
        }
        return diamondP3;
    }

    public Point getDiamondP2() {
        if (diamondP2 == null) {
            diamondP2 = new Point(x2, y1 + (y2 - y1) / 2.0);
        }
        return diamondP2;
    }

    public Point getDiamondP4() {
        if (diamondP4 == null) {
            diamondP4 = new Point(x1, y1 + (y2 - y1) / 2.0);
        }
        return diamondP4;
    }

    public boolean equals(Object rect2) {
        if (this == rect2) {
            return true;
        } else {
            if (rect2 instanceof Rect) {
                Rect r2 = (Rect) rect2;
                return this.x1 == r2.x1 && this.y1 == r2.y1 &&
                        this.x2 == r2.x2 && this.y2 == r2.y2;
            } else {
                return false;
            }
        }
    }
}
