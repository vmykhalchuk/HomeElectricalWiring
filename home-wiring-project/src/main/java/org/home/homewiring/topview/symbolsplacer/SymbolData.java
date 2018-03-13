package org.home.homewiring.topview.symbolsplacer;

import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.homewiring.utils.Point;
import org.home.homewiring.utils.Rect;

public class SymbolData {

    private TopViewSymbol tvSymbol;
    private double relativeX, relativeY;
    private TopViewRenderingEngine renderingEngine;

    private Rect rect;
    private Double xWidth, yLength; // symbol rectangle dimensions

    private Rect pointRect;
    private Point pointPoint;

    private Double signXWidth, signYLength;
    private Double signCentreXRelative, signCentreYRelative;
    private Point signPoint1;
    private Point signCentrePoint;
    private Rect signRect;

    public SymbolData(TopViewSymbol topViewSymbol, TopViewRenderingEngine renderingEngine) {
        this.tvSymbol = topViewSymbol;
        this.renderingEngine = renderingEngine;
    }

    public SymbolData(Point relativeFrom, TopViewSymbol topViewSymbol, TopViewRenderingEngine renderingEngine) {
        relativeX = relativeFrom != null ? relativeFrom.getX() : 0;
        relativeY = relativeFrom != null ? relativeFrom.getY() : 0;
        this.tvSymbol = topViewSymbol;
        this.renderingEngine = renderingEngine;
    }

    public void setXY(double x, double y) {
        resetSymbolXYRelatedData();
        tvSymbol.setX(x);
        tvSymbol.setY(y);
    }

    private void resetSymbolXYRelatedData() {
        rect = null;

        signPoint1 = null;
        signCentrePoint = null;
        signRect = null;
    }

    public void setLabelAlignment(TopViewSymbol.Label2SymbolAlignment labelAlignment) {
        resetSymbolXYRelatedData();

        xWidth = yLength = null;

        signXWidth = signYLength = null;
        signCentreXRelative = signCentreYRelative = null;

        tvSymbol.setLabelAlignment(labelAlignment);
    }

    public String getLabelText() {
        return tvSymbol.getLabelText();
    }

    public Rect getRect() {
        if (rect == null) {
            rect = new Rect(relativeX + tvSymbol.getX(), relativeY + tvSymbol.getY(),
                    relativeX + tvSymbol.getX() + getXWidth(), relativeY + tvSymbol.getY() + getYLength());
        }
        return rect;
    }

    public Double getXWidth() {
        if (xWidth == null) {
            xWidth = renderingEngine.getSymbolXWidth(tvSymbol);
        }
        return xWidth;
    }

    public Double getYLength() {
        if (yLength == null) {
            yLength = renderingEngine.getSymbolYLength(tvSymbol);
        }
        return yLength;
    }

    public Point getPointPoint() {
        if (pointPoint == null) {
            pointPoint = new Point(relativeX + tvSymbol.getPointX(), relativeY + tvSymbol.getPointY());
        }
        return pointPoint;
    }

    public Rect getPointRect() {
        if (pointRect == null) {
            pointRect = new Rect(relativeX + tvSymbol.getPointX() - 2, relativeY + tvSymbol.getPointY() - 2,
                    relativeX + tvSymbol.getPointX() + 2, relativeY + tvSymbol.getPointY() + 2);
        }
        return pointRect;
    }


    protected Double getSignXWidth() {
        if (signXWidth == null) {
            signXWidth = renderingEngine.getSymbolSignXWidth(tvSymbol.getPointType());
        }
        return signXWidth;
    }

    protected Double getSignYLength() {
        if (signYLength == null) {
            signYLength = renderingEngine.getSymbolSignYLength(tvSymbol.getPointType());
        }
        return signYLength;
    }

    protected Double getSignCentreXRelative() {
        if (signCentreXRelative == null) {
            signCentreXRelative = renderingEngine.getSymbolSignXRelative(tvSymbol) + getSignXWidth() / 2.0;
        }
        return signCentreXRelative;
    }

    protected Double getSignCentreYRelative() {
        if (signCentreYRelative == null) {
            signCentreYRelative = renderingEngine.getSymbolSignYRelative(tvSymbol) + getSignYLength() / 2.0;
        }
        return signCentreYRelative;
    }

    public Point getSignPoint1() {
        if (signPoint1 == null) {
            double symbolSignX = getRect().getX1() + renderingEngine.getSymbolSignXRelative(tvSymbol);
            double symbolSignY = getRect().getY1() + renderingEngine.getSymbolSignYRelative(tvSymbol);
            signPoint1 = new Point(symbolSignX, symbolSignY);
        }
        return signPoint1;
    }

    public Point getSignCentre() {
        if (signCentrePoint == null) {
            double symbolSignXCentre;
            double symbolSignYCentre;
            if (true) {
                symbolSignXCentre = getRect().getX1() + getSignCentreXRelative();
                symbolSignYCentre = getRect().getY1() + getSignCentreYRelative();
            } else {
                symbolSignXCentre = getSignPoint1().getX() + getSignXWidth() / 2.0;
                symbolSignYCentre = getSignPoint1().getY() + getSignYLength() / 2.0;
            }
            signCentrePoint = new Point(symbolSignXCentre, symbolSignYCentre);
        }
        return signCentrePoint;
    }

    public Rect getSignRect() {
        if (signRect == null) {
            signRect = new Rect(getSignPoint1(), getSignXWidth(), getSignYLength());
        }
        return signRect;
    }

}
