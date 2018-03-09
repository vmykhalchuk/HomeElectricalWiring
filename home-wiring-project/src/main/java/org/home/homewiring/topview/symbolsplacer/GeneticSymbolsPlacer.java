package org.home.homewiring.topview.symbolsplacer;

import org.home.homewiring.topview.RandomTopViewSymbolsPlacer;
import org.home.homewiring.topview.TopViewSymbolsPlacer;
import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneticSymbolsPlacer extends RandomTopViewSymbolsPlacer {

    public GeneticSymbolsPlacer(TopViewModel topViewModel, TopViewRenderingEngine renderingEngine) {
        super(topViewModel, renderingEngine);
    }

    public void placeSymbolsProperly() {
        TopViewSymbolsPlacer.placeSymbolsProperly(topViewModel, renderingEngine);

        for (final TopViewArea tvArea : topViewModel.getAreas()) {
            final List<SymbolData> symbolsList = new ArrayList<>();
            for (final TopViewSymbol tvSymbol : tvArea.getSymbols()) {
                symbolsList.add(new SymbolData(tvSymbol, renderingEngine));
            }

            // now place symbols randomly
            geneticAlgorithmPlacing(tvArea, symbolsList);
        }
    }

    private void geneticAlgorithmPlacing(TopViewArea tvArea, List<SymbolData> symbolsList) {
        // find colliding symbols (to be placed randomly)
        List<SymbolData> collidingSymbols = symbolsList.stream().filter(a -> symbolCollides(a, symbolsList)).collect(Collectors.toList());

        List<Integer> prioritiesList = new ArrayList<>(collidingSymbols.size());
        for (int i = 0; i < collidingSymbols.size(); i++) {
            prioritiesList.add(i);
        }
        placeSymbolsOptimally(tvArea, symbolsList, collidingSymbols, prioritiesList);
    }

    private void placeSymbolsOptimally(TopViewArea tvArea, List<SymbolData> symbolsList, List<SymbolData> symbolsToPlace, List<Integer> symbolsToPlacePriorities) {
        // reset coordinates, so colliding symbols can be placed afterwards
        for (SymbolData s : symbolsToPlace) {
            s.setX(-1000000d);
            s.setY(-1000000d);
        }

        for (Integer prio : symbolsToPlacePriorities) {
            placeSymbolOptimally(tvArea, symbolsList, symbolsToPlace.get(prio));
        }
    }

    private void placeSymbolOptimally(TopViewArea tvArea, List<SymbolData> symbolsList, SymbolData symbolToPlace) {
        placeSymbolOptimallyRightFromPoint(tvArea, symbolsList, symbolToPlace);
    }

    private void placeSymbolOptimallyRightFromPoint(TopViewArea tvArea, List<SymbolData> symbolsList, SymbolData symbolToPlace) {
        double startPointX = symbolToPlace.getPointRect().getX2();
        double startPointY = symbolToPlace.getPointPoint().getY();
        double symbolSignCentreYRelative = symbolToPlace.getSymbolSignCentre().getY() - symbolToPlace.getSymbolRect().getY1();

        double startingX = startPointX;
        double startingY = startPointY - symbolSignCentreYRelative;

    }

    private void placeSymbolOptimallyBelowFromPoint(TopViewArea tvArea, List<SymbolData> symbolsList, SymbolData symbolToPlace) {
        double startPointX = symbolToPlace.getPointPoint().getX();
        double startPointY = symbolToPlace.getPointRect().getY2();
        double symbolSignCentreXRelative = symbolToPlace.getSymbolSignCentre().getX() - symbolToPlace.getSymbolRect().getX1();

        double startingX = startPointX - symbolSignCentreXRelative;
        double startingY = startPointY;

    }
}
