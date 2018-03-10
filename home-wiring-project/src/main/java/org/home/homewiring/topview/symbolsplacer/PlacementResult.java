package org.home.homewiring.topview.symbolsplacer;

public class PlacementResult {
    private Double x, y;
    private Double length;
    public PlacementResult(Double x, Double y, Double length) {
        this.x = x;
        this.y = y;
        this.length = length;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getLength() {
        return length;
    }
}
