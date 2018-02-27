package org.home.homewiring;

import org.home.homewiring.data3d_to_topview.Data3DToTopViewGenerator;
import org.home.homewiring.data3dmodel.model.Area;
import org.home.homewiring.data3dmodel.xmlload.XMLDataLoader2;
import org.home.homewiring.topview.TopViewSymbolsPlacer;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.homewiring.topview.renderer.svg.SVGRenderingEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

//        final XMLDataLoaderDummy loader = new XMLDataLoaderDummy("data/dubljany_hatka_23/main_floor.data.xml");
//        loader.loadData();
//        final List<Area> areaList = loader.getAreaList();

        final List<Area> areaList = XMLDataLoader2.getAreaList(new File("data/dubljany_hatka_23/main_floor.data.xml"));

        final TopViewModel topViewModel = Data3DToTopViewGenerator.generate(areaList);
        final TopViewRenderingEngine renderingEngine = new SVGRenderingEngine(); // we will use SVG rendering engine

        TopViewSymbolsPlacer.placeSymbolsProperly(topViewModel, renderingEngine);

        renderingEngine.generateFile(new File("output.svg"), topViewModel);

        System.out.println("Ok!");
    }

}
