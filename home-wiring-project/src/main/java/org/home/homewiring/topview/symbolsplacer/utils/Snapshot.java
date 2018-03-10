package org.home.homewiring.topview.symbolsplacer.utils;

import org.home.homewiring.topview.symbolsplacer.SymbolData;

import java.util.ArrayList;
import java.util.List;

public class Snapshot {

    private List<SymbolData> symbolsList;
    private List<Double> symbolsXList = new ArrayList<>();
    private List<Double> symbolsYList = new ArrayList<>();
    private boolean saved = false;

    public Snapshot(List<SymbolData> symbolsList) {
        this.symbolsList = symbolsList;
    }

    public boolean load() {
        if (!saved) {
            // nothing to load
            return false;
        }
        for (int i = 0; i < symbolsList.size(); i++) {
            symbolsList.get(i).setXY(symbolsXList.get(i), symbolsYList.get(i));
        }
        return true;
    }

    public void save() {
        symbolsXList.clear();
        symbolsYList.clear();
        for (SymbolData s : symbolsList) {
            symbolsXList.add(s.getRect().getX1());
            symbolsYList.add(s.getRect().getY1());
        }
        saved = true;
    }

}
