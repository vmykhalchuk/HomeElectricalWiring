package org.home.homewiring.topview.renderer.svg.symbolslibrary;

import org.home.homewiring.data3dmodel.model.AbstractPoint;

public abstract class SymbolRenderer {

    public abstract double getxWidth(AbstractPoint point);

    public abstract double getyLength(AbstractPoint point);

    public abstract double getTextXWidth(AbstractPoint point);

    public abstract double getTextYLength(AbstractPoint point);
}
