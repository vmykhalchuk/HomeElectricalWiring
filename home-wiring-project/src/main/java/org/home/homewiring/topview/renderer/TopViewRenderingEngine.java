package org.home.homewiring.topview.renderer;

import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;

import java.io.File;
import java.io.FileNotFoundException;

public interface TopViewRenderingEngine {

    default double getSymbolXWidth(TopViewSymbol symbol) {
        return getSymbolXWidth(symbol.getPointType(), symbol.getLabelAlignment(), symbol.getLabelText());
    }

    double getSymbolXWidth(String pointType, TopViewSymbol.Label2SymbolAlignment labelAlignment, String labelText);

    default double getSymbolYLength(TopViewSymbol symbol) {
        return getSymbolYLength(symbol.getPointType(), symbol.getLabelAlignment(), symbol.getLabelText());
    }

    double getSymbolYLength(String pointType, TopViewSymbol.Label2SymbolAlignment labelAlignment, String labelText);

    double getSymbolSignXWidth(String pointType);
    double getSymbolSignYLength(String pointType);

    double getSymbolSignXRelative(TopViewSymbol tvSymbol);
    double getSymbolSignYRelative(TopViewSymbol tvSymbol);

    double getSymbolLableXRelative(TopViewSymbol tvSymbol);
    double getSymbolLableYRelative(TopViewSymbol tvSymbol);

    void generateFile(File file, TopViewModel topViewModel) throws FileNotFoundException;
}
