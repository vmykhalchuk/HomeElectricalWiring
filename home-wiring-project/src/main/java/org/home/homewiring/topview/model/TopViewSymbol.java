package org.home.homewiring.topview.model;

/**
 * Representation of Symbol on 2d Top View drawing.<br>
 * pointType, pointX and pointY - represent physical location of Point on the drawing.<br>
 * x, y - represent top-left corner of the box in which symbol and label are rendered.<br>
 * <b>Note:</b> all coordinates are relative to area in which symbol is located!
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

    /*public static class Text {
        private String text;
        private Double x;
        private Double y;
        private String color;
        private Double rotation;
        private Allign allign;
        private Double xWidth;
        private Double yLength;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
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

        public Double getRotation() {
            return rotation;
        }

        public void setRotation(Double rotation) {
            this.rotation = rotation;
        }

        public Allign getAllign() {
            return allign;
        }

        public void setAllign(Allign allign) {
            this.allign = allign;
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

        public enum Allign {
            LEFT, RIGHT, CENTER, TOP, BOTTOM
        }
    }*/
}
