package org.home.homewiring.topview.symbolsplacer;

import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;

import java.io.File;
import java.io.FileNotFoundException;

public class TVRenderingEngineWrapperWithPaddingAccounted implements TopViewRenderingEngine {

    private TopViewRenderingEngine engine;

    public TVRenderingEngineWrapperWithPaddingAccounted(TopViewRenderingEngine engine) {
        this.engine = engine;
    }

    @Override
    public double getSymbolXWidth(String pointType, TopViewSymbol.Label2SymbolAlignment labelAlignment, String labelText) {
        return engine.getSymbolXWidth(pointType, labelAlignment, labelText) + 1.0;
    }

    @Override
    public double getSymbolYLength(String pointType, TopViewSymbol.Label2SymbolAlignment labelAlignment, String labelText) {
        return engine.getSymbolYLength(pointType, labelAlignment, labelText) + 1.0;
    }

    @Override
    public double getSymbolSignXWidth(String pointType) {
        return engine.getSymbolSignXWidth(pointType);
    }

    @Override
    public double getSymbolSignYLength(String pointType) {
        return engine.getSymbolSignYLength(pointType);
    }

    @Override
    public double getSymbolSignXRelative(TopViewSymbol tvSymbol) {
        double x = engine.getSymbolSignXRelative(tvSymbol);
        switch (tvSymbol.getLabelAlignment()) {
            case LEFT:
                return x - 0.5;
            case RIGHT:
                return x + 0.5;
            case ABOVE:
            case BELOW:
                return x;
            default:
                throw new RuntimeException("" + tvSymbol.getLabelAlignment());
        }
    }

    @Override
    public double getSymbolSignYRelative(TopViewSymbol tvSymbol) {
        double y = engine.getSymbolSignYRelative(tvSymbol);
        switch (tvSymbol.getLabelAlignment()) {
            case LEFT:
            case RIGHT:
            case BELOW:
                return y + 0.5;
            case ABOVE:
                return y - 0.5;
            default:
                throw new RuntimeException("" + tvSymbol.getLabelAlignment());
        }
    }

    @Override
    public double getSymbolLabelXRelative(TopViewSymbol tvSymbol) {
        throw new RuntimeException();
    }

    @Override
    public double getSymbolLabelYRelative(TopViewSymbol tvSymbol) {
        throw new RuntimeException();
    }

    @Override
    public void generateFile(File file, TopViewModel topViewModel, boolean debugEnabled) throws FileNotFoundException {
        throw new RuntimeException();
    }
}
