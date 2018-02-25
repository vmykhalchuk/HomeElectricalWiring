package org.home.homewiring.topview.renderer.svg.symbolslibrary;

import org.home.homewiring.data3dmodel.model.AbstractPoint;

public class SwitchSymbolRenderer extends SymbolRenderer {

    @Override
    public double getxWidth(AbstractPoint point) {
        return 20;
    }

    @Override
    public double getyLength(AbstractPoint point) {
        return 20;
    }

    @Override
    public double getTextXWidth(AbstractPoint point) {
        return point.getCode().length() * 10; // take into account rotation of text!
    }

    @Override
    public double getTextYLength(AbstractPoint point) {
        return 20;
    }
}
