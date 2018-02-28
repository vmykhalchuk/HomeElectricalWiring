package org.home.homewiring;

import org.home.homewiring.data3d_to_topview.Data3DToTopViewGenerator;
import org.home.homewiring.data3d_to_topview.mappers.TopViewAreaMapper;
import org.home.homewiring.data3dmodel.model.HomeWiringData;
import org.home.homewiring.data3dmodel.xmlload.XMLDataLoader2;
import org.home.homewiring.data3dmodel.yamlload.YAMLDataLoader;
import org.home.homewiring.topview.TopViewSymbolsPlacer;
import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.renderer.TopViewRenderingEngine;
import org.home.homewiring.topview.renderer.svg.SVGRenderingEngine;
import org.mapstruct.factory.Mappers;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        YAMLDataLoader.getAreaList(new File("output.data.yaml"));

//        final XMLDataLoaderDummy loader = new XMLDataLoaderDummy("data/dubljany_hatka_23/main_floor.data.xml");
//        loader.loadData();
//        final List<Area> areaList = loader.getAreaList();

        final HomeWiringData homeWiringData = XMLDataLoader2.getAreaList(new File("data/dubljany_hatka_23/main_floor.data.xml"));

        final TopViewModel topViewModel = Data3DToTopViewGenerator.generate(homeWiringData.getAreas());
        final TopViewRenderingEngine renderingEngine = new SVGRenderingEngine(); // we will use SVG rendering engine

        TopViewSymbolsPlacer.placeSymbolsProperly(topViewModel, renderingEngine);

        renderingEngine.generateFile(new File("output.svg"), topViewModel);

        System.out.println("Ok!");

        YAMLDataLoader.writeToFile(new File("output.data.yaml"), homeWiringData);

        TopViewAreaMapper mapper = Mappers.getMapper(TopViewAreaMapper.class);
        TopViewArea area = mapper.areaToTopViewArea(homeWiringData.getAreas().get(0));
        System.out.println("area: " + area);
    }

}
