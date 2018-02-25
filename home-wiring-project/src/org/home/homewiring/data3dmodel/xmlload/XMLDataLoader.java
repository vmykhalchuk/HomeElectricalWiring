package org.home.homewiring.data3dmodel.xmlload;

import org.home.homewiring.data3dmodel.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads data from *.data.xml file into {@link org.home.homewiring.data3dmodel} data models.
 */
public class XMLDataLoader {

    public XMLDataLoader(String fileName) {

    }

    public void loadData() {
    }

    public List<Area> getAreaList() {
        List<Area> resultList = new ArrayList<>();
        {
            Area area = new Area();
            resultList.add(area);
            area.setCode("K");
            area.setName("Kitchen");
            area.setX(100d);
            area.setY(100d);
            area.setZ(3500d);
            area.setxWidth(4600d);
            area.setyLength(5000d);
            area.setzHeight(2900d);

            area.setPointsCollectionList(getKitchenPointsCollections());
        }

        return resultList;
    }

    private List<PointsCollection> getKitchenPointsCollections() {
        List<PointsCollection> resultList = new ArrayList<>();
        {
            PointsCollection collection = new PointsCollection();
            resultList.add(collection);
            collection.setAreaCode("K");
            collection.setPointTypeResolver("Resolvers.getByCodeResolver()");
            List<AbstractPoint> points = new ArrayList<>();
            collection.setPoints(points);
            {
                Point point = new Point();
                points.add(point);
                point.setCode("ZK99");
                point.setType("S"); // should be resolved automatically
                point.setX(4600d);
                point.setY(2000d);
                point.setZ(220d);
            }
            {
                PointGroup group = new PointGroup();
                points.add(group);
                group.setZ(300d);
                group.setType("S");
                group.setId("Kitchen-Sockets-Group");
                group.setChildren(new ArrayList<>());
                {
                    Point point = new Point();
                    group.getChildren().add(point);
                    point.setParent(group);
                    point.setCode("SK1");
                    point.setX(300d);
                    point.setY(0d);
                }
                {
                    PointGroup group2 = new PointGroup();
                    group.getChildren().add(group2);
                    group2.setParent(group);
                    group2.setCode("SK2x");
                    group2.setX(0d);
                    group2.setY(200d);
                    group2.setZ(2050d);
                    group2.setChildren(new ArrayList<>());
                    {
                        Point point = new Point();
                        group2.getChildren().add(point);
                        point.setParent(group2);
                        point.setCode("SK21");
                        point.setdX(0d);
                    }
                    {
                        Point point = new Point();
                        group2.getChildren().add(point);
                        point.setParent(group2);
                        point.setCode("SK22");
                        point.setdX(50d);
                    }
                }
                {
                    Point point = new Point();
                    group.getChildren().add(point);
                    point.setParent(group);
                    point.setCode("SK5");
                    point.setX(2500d);
                    point.setY(2500d);
                    point.setdZ(-70d);
                }
                {
                    Point point = new Point();
                    group.getChildren().add(point);
                    point.setParent(group);
                    point.setCode("SK6");
                    point.setX(2500d);
                    point.setY(1000d);
                    point.setZ(800d);
                }
            }
            {
                Point point = new Point();
                points.add(point);
                point.setCode("MK1");
                point.setType("M");
                point.setX(120d);
                point.setY(120d);
                point.setZ(120d);
            }
            {
                Point point = new Point();
                points.add(point);
                point.setCode("WK99");
                point.setType("W"); // should be resolved automatically
                point.setX(220d);
                point.setY(220d);
                point.setZ(220d);
            }
        }

        return resultList;
    }

    public void populatePointCollectionsWithTopViewManualOverrides(List<PointsCollection> data) {
        for (PointsCollection collection : data) {
            populatePointCollectionsWithTopViewManualOverrides(collection.getAreaCode(), collection.getPoints());
        }
    }

    void populatePointCollectionsWithTopViewManualOverrides(String areaCode, List<AbstractPoint> data) {
        for (AbstractPoint pg : data) {
            if (pg instanceof PointGroup) {
                populatePointCollectionsWithTopViewManualOverrides(areaCode, ((PointGroup) pg).getChildren());

                if ("Kitchen-Sockets-Group".equals(pg.getId())) {
                    pg.setTopViewSymbolManualData(new AbstractPoint.TopViewSymbolManualData());

                }
            } else {

            }
        }
    }
}
