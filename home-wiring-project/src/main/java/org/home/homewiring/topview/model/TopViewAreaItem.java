package org.home.homewiring.topview.model;

public class TopViewAreaItem {

    private Type type;
    private Double x;
    private Double y;
    private Double xWidth;
    private Double yLength;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getxWidth() {
        return xWidth;
    }

    public void setxWidth(Double xWidth) {
        this.xWidth = xWidth;
    }

    public Double getyLength() {
        return yLength;
    }

    public void setyLength(Double yLength) {
        this.yLength = yLength;
    }

    public enum Type {
        door, window, opening
    }
}
