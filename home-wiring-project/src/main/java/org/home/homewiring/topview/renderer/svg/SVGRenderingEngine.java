package org.home.homewiring.topview.renderer.svg;

import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class SVGRenderingEngine implements TopViewRenderingEngine {

    @Override
    public double getSymbolXWidth(String pointType, TopViewSymbol.Label2SymbolAlignment labelAlignment, String labelText) {
        double labelTextLength = labelText.length() * 5.5;
        switch (labelAlignment) {
            case LEFT:
            case RIGHT: {
                return getSymbolSignXWidth(pointType) + labelTextLength;
            }
            case ABOVE:
            case BELOW: {
                return Math.max(getSymbolSignXWidth(pointType), labelTextLength);
            }
            default:
                throw new RuntimeException("" + labelAlignment);
        }
    }

    @Override
    public double getSymbolYLength(String pointType, TopViewSymbol.Label2SymbolAlignment labelAlignment, String labelText) {
        switch (labelAlignment) {
            case LEFT:
            case RIGHT: {
                return Math.max(getSymbolSignYLength(pointType), 7);
            }
            case ABOVE:
            case BELOW: {
                return getSymbolSignYLength(pointType) + 7;
            }
            default:
                throw new RuntimeException("" + labelAlignment);
        }
    }

    @Override
    public double getSymbolSignXWidth(String pointType) {
        switch (pointType) {
            case "S":
                return 18.5;
            case "W":
                return 15.5;
            case "M":
                return 20;
            default:
                throw new RuntimeException("No pointType supported: " + pointType);
        }
    }

    @Override
    public double getSymbolSignYLength(String pointType) {
        switch (pointType) {
            case "S":
                return 10;
            case "W":
                return 19.5;
            case "M":
                return 20;
            default:
                throw new RuntimeException("No pointType supported: " + pointType);
        }
    }

    @Override
    public double getSymbolSignXRelative(TopViewSymbol tvSymbol) {
        double symbolXWidth = getSymbolXWidth(tvSymbol);
        double symbolSignXWidth = getSymbolSignXWidth(tvSymbol.getPointType());
        switch (tvSymbol.getLabelAlignment()) {
            case LEFT:
                return symbolXWidth - symbolSignXWidth;
            case RIGHT:
                return 0;
            case ABOVE:
            case BELOW:
                return (symbolXWidth - symbolSignXWidth) / 2;
            default:
                throw new RuntimeException("" + tvSymbol.getLabelAlignment());
        }
    }

    @Override
    public double getSymbolSignYRelative(TopViewSymbol tvSymbol) {
        double symbolYLength = getSymbolYLength(tvSymbol);
        double symbolSignYLength = getSymbolSignYLength(tvSymbol.getPointType());
        switch (tvSymbol.getLabelAlignment()) {
            case LEFT:
            case RIGHT:
            case BELOW:
                return 0;
            case ABOVE:
                return symbolYLength - symbolSignYLength;
            default:
                throw new RuntimeException("" + tvSymbol.getLabelAlignment());
        }
    }

    @Override
    public double getSymbolLabelXRelative(TopViewSymbol tvSymbol) {
        double symbolSignXWidth = getSymbolSignXWidth(tvSymbol.getPointType());
        switch (tvSymbol.getLabelAlignment()) {
            case LEFT:
            case ABOVE:
            case BELOW:
                return 0;
            case RIGHT:
                return symbolSignXWidth;
            default:
                throw new RuntimeException("" + tvSymbol.getLabelAlignment());
        }
    }

    @Override
    public double getSymbolLabelYRelative(TopViewSymbol tvSymbol) {
        double symbolSignYLength = getSymbolSignYLength(tvSymbol.getPointType());
        switch (tvSymbol.getLabelAlignment()) {
            case LEFT:
            case RIGHT:
            case ABOVE:
                return 0;
            case BELOW:
                return symbolSignYLength;
            default:
                throw new RuntimeException("" + tvSymbol.getLabelAlignment());
        }
    }

    @Override
    public void generateFile(File file, TopViewModel topViewModel) throws FileNotFoundException {
        try (final PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
            new SVGRendererHelper(writer, topViewModel, this).writeToStream();
        }
    }
}
