package org.home.homewiring.topview.model;

/**
 * Representation of Symbol on 2d Top View drawing.<br>
 * pointType, pointX and pointY - represent physical location of Point on the drawing.<br>
 * x, y - represent top-left corner of the box in which symbol and label are rendered.<br>
 * <b>Note:</b> all coordinates are relative to area in which symbol is located!
 * <br>
 * <br>
 * <b>Note:</b> Check here to understand
 * <a href="{@docRoot}/doc-files/symbol_explained.png">Symbol layout<img src="{@docRoot}/doc-files/symbol_explained.png" width="20%" height="20%"/></a>
 */
public class TopViewSymbol {

    /**
     * W - Switch, S - Socket, M - Misc, etc
     */
    private String pointType; // pointType to load topView symbol styling from
    private Double pointX;
    private Double pointY;

    private Double x;
    private Double y;
    private String color;
    private String innerText;
    private Label2SymbolAlignment labelAlignment;
    private String labelText;

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public Double getPointX() {
        return pointX;
    }

    public void setPointX(Double pointX) {
        this.pointX = pointX;
    }

    public Double getPointY() {
        return pointY;
    }

    public void setPointY(Double pointY) {
        this.pointY = pointY;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getInnerText() {
        return innerText;
    }

    public void setInnerText(String innerText) {
        this.innerText = innerText;
    }


    public Label2SymbolAlignment getLabelAlignment() {
        return labelAlignment;
    }

    public void setLabelAlignment(Label2SymbolAlignment labelAlignment) {
        this.labelAlignment = labelAlignment;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public enum Label2SymbolAlignment {
        LEFT, RIGHT, ABOVE, BELOW
    }

}
