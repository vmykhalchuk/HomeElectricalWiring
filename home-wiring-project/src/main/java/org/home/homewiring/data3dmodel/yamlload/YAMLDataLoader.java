package org.home.homewiring.data3dmodel.yamlload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.home.homewiring.data3dmodel.model.AbstractPoint;
import org.home.homewiring.data3dmodel.model.Area;
import org.home.homewiring.data3dmodel.model.HomeWiringData;
import org.home.homewiring.data3dmodel.model.PointGroup;
import org.home.homewiring.data3dmodel.model.PointsCollection;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class YAMLDataLoader {
    public static List<Area> getAreaList(File file) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            //mapper.readValue(file, HashMap.class);
            if (file.exists()) {
                JsonNode node = mapper.readTree(file);
                System.out.println(node);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        XmlMapper xmlMapper = new XmlMapper();
        try {
            JsonNode node = xmlMapper.readTree(new File("data/dubljany_hatka_23/main_floor.data.xml"));
            System.out.println(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void writeToFile(File file, HomeWiringData homeWiringData) {
        for (Area area : homeWiringData.getAreas()) {
            area.setPointsCollectionList(null);
        }
        for (PointsCollection pointsCollection : homeWiringData.getPointsCollections()) {
            cleanParentLink(pointsCollection.getPoints());
        }
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            mapper.writeValue(file, homeWiringData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void cleanParentLink(List<AbstractPoint> points) {
        for (AbstractPoint p : points) {
            p.setParent(null);
            if (p instanceof PointGroup) {
                cleanParentLink(((PointGroup) p).getChildren());
            }
        }
    }
}
