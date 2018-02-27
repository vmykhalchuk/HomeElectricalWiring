package org.home.homewiring.topview.model;

import java.util.List;

public class TopViewArea {
    private String code;
    private Double x;
    private Double y;
    private Double xWidth;
    private Double yLength;

    private List<TopViewSymbol> symbols;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public List<TopViewSymbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<TopViewSymbol> symbols) {
        this.symbols = symbols;
    }
}
