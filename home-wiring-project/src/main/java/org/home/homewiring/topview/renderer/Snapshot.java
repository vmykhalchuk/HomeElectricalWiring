package org.home.homewiring.topview.renderer;

import org.home.homewiring.topview.model.TopViewSymbol;

import java.util.ArrayList;
import java.util.List;

public class Snapshot {

    private List<TopViewSymbol> symbolsList;
    private List<Double> symbolsXList = new ArrayList<>();
    private List<Double> symbolsYList = new ArrayList<>();
    private boolean saved = false;

    public Snapshot(List<TopViewSymbol> symbolsList) {
        this.symbolsList = symbolsList;
    }

    public boolean load() {
        if (!saved) {
            // nothing to load
            return false;
        }
        for (int i = 0; i < symbolsList.size(); i++) {
            symbolsList.get(i).setX(symbolsXList.get(i));
            symbolsList.get(i).setY(symbolsYList.get(i));
        }
        return true;
    }

    public void save() {
        symbolsXList.clear();
        symbolsYList.clear();
        for (TopViewSymbol s : symbolsList) {
            symbolsXList.add(s.getX());
            symbolsYList.add(s.getY());
        }
        saved = true;
    }

}
