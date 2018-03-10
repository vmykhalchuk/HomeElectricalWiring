package org.home.homewiring.topview.symbolsplacer;

import org.home.homewiring.topview.Utils;
import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;

public class TopViewSymbolsPlacer {

    public static double POINT_BORDER_MARGIN = 5; // 5 points from border // FIXME This constant depends on SVG / PNG Rendering engine!

    protected TopViewModel topViewModel;
    protected TopViewRenderingEngine renderingEngine;

    public TopViewSymbolsPlacer(TopViewModel topViewModel, TopViewRenderingEngine renderingEngine) {
        this.topViewModel = topViewModel;
        this.renderingEngine = renderingEngine;
    }

    public void populateInitialLabelAlignment(TopViewSymbol tvSymbol, TopViewArea tvArea) {
        final Utils.POINT_LOCATION location = Utils.locatePoint(tvSymbol.getPointX(), tvSymbol.getPointY(), tvArea.getxWidth(), tvArea.getyLength());
        switch (location) {
            case LEFT:
            case TOP_LEFT:
            case BOTTOM_LEFT:
                tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.RIGHT);
                break;
            case RIGHT:
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.LEFT);
                break;
            case TOP:
            case MIDDLE:
                tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.BELOW);
                break;
            case BOTTOM:
                tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.ABOVE);
                break;
            default:
                throw new RuntimeException(location.name());
        }
    }

    public void placeSymbolsProperly() {
        for (final TopViewArea tvArea : topViewModel.getAreas()) {
            for (final TopViewSymbol tvSymbol : tvArea.getSymbols()) {
                // FIXME Detect which tvSymbol is placed manually - so we do not override its configuration!

                populateInitialLabelAlignment(tvSymbol, tvArea);

                Utils.POINT_LOCATION location = Utils.locatePoint(tvSymbol.getPointX(), tvSymbol.getPointY(), tvArea.getxWidth(), tvArea.getyLength());
                double symbolXWidth = renderingEngine.getSymbolXWidth(tvSymbol);
                double symbolYLength = renderingEngine.getSymbolYLength(tvSymbol);

                switch (location) {
                    case LEFT:
                        tvSymbol.setX(tvSymbol.getPointX() + POINT_BORDER_MARGIN);
                        tvSymbol.setY(tvSymbol.getPointY() - symbolYLength / 2);
                        break;
                    case RIGHT:
                    case TOP_RIGHT: // FIXME assure Y is not out of range
                    case BOTTOM_RIGHT: // FIXME assure Y is not out of range
                        tvSymbol.setX(tvSymbol.getPointX() - symbolXWidth - POINT_BORDER_MARGIN);
                        tvSymbol.setY(tvSymbol.getPointY() - symbolYLength / 2);
                        break;
                    case TOP:
                    case TOP_LEFT:
                    case MIDDLE:
                        tvSymbol.setX(Math.max(tvSymbol.getPointX() - symbolXWidth / 2, POINT_BORDER_MARGIN));
                        tvSymbol.setY(tvSymbol.getPointY() + POINT_BORDER_MARGIN);
                        break;
                    case BOTTOM:
                    case BOTTOM_LEFT:
                        tvSymbol.setX(Math.max(tvSymbol.getPointX() - symbolXWidth / 2, POINT_BORDER_MARGIN));
                        tvSymbol.setY(tvSymbol.getPointY() - symbolYLength - POINT_BORDER_MARGIN);
                        break;
                }
            }
        }
    }

}
