package org.home.homewiring.topview.renderer.svg.symbolslibrary;

import java.util.HashMap;
import java.util.Map;

public class SymbolRendererFactory {

    private SymbolRendererFactory() {
    }

    private static SymbolRendererFactory INSTANCE = new SymbolRendererFactory();

    public static SymbolRendererFactory getInstance() {
        return INSTANCE;
    }

    private static Map<String, SymbolRenderer> renderers = new HashMap<>();

    private SymbolRenderer loadRenderer(String pointType) {
        if (true/*"W".equals(pointType) FIXME*/) {
            return new SwitchSymbolRenderer();
        } else {
            throw new RuntimeException("No TopViewSymbol renderer for point type: " + pointType);
        }
    }

    public SymbolRenderer getSymbolRenderer(String pointType) {
        if (!renderers.containsKey(pointType)) {
            synchronized (renderers) {
                if (!renderers.containsKey(pointType)) {
                    renderers.put(pointType, loadRenderer(pointType));
                }
            }
        }
        return renderers.get(pointType);
    }

}
