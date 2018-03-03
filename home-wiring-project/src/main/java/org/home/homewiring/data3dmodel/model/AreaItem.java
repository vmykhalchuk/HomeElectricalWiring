package org.home.homewiring.data3dmodel.model;

import javax.validation.constraints.Min;

public class AreaItem {
    /**
     * door, window, opening, etc
     */
    private String type;

    private Double x;
    private Double y;
    private Double z;
    @Min(0)
    private Double xWidth;
    @Min(0)
    private Double yLength;
    @Min(0)
    private Double zHeight;

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
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

    public Double getzHeight() {
        return zHeight;
    }

    public void setzHeight(Double zHeight) {
        this.zHeight = zHeight;
    }
}
