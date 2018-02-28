package org.home.homewiring.data3dmodel.xmlload;

import org.home.homewiring.data3dmodel.model.AbstractPoint;
import org.home.homewiring.data3dmodel.model.Area;
import org.home.homewiring.data3dmodel.model.AreaItem;
import org.home.homewiring.data3dmodel.model.HomeWiringData;
import org.home.homewiring.data3dmodel.model.Point;
import org.home.homewiring.data3dmodel.model.PointGroup;
import org.home.homewiring.data3dmodel.model.PointsCollection;
import org.home.homewiring.util.Pair;
import org.home.homewiring.util.Props;
import org.home.homewiring.util.Tripple;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads data from *.data.xml file into {@link org.home.homewiring.data3dmodel} data models.
 */
public class XMLDataLoader2 {

    public static HomeWiringData getAreaList(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        List<Area> areaList = new ArrayList<>();
        List<PairOfPointCollections> pointsCollectionsList = new ArrayList<>();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            NodeList nodeList = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if ("areas".equals(node.getNodeName())) {
                        areaList.addAll(processAreasElement((Element) node));
                    } else if ("points-collection".equals(node.getNodeName())) {
                        pointsCollectionsList.add(processPointsCollectionElement((Element) node));
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // make sure we have no Area duplicates
        for (Area area : areaList) {
            if (areaList.stream().filter((a) -> area.getCode().equals(a.getCode())).count() > 1) {
                throw new RuntimeException(String.format("Area with code: '%s' is duplicated!", area.getCode()));
            }
        }

        // now match Areas to PointsCollections and put PointsCollections into Area
        for (PairOfPointCollections pointsCollectionPair : pointsCollectionsList) {
            PointsCollection pointsCollection = pointsCollectionPair.getU();
            String areaCode = pointsCollection.getAreaCode();
            Area area = areaList.stream().filter((a) -> areaCode.equals(a.getCode())).findFirst().orElse(null);
            if (area == null) {
                throw new RuntimeException(String.format("No area with code: '%s' found! Cannot set pointsCollection for this Area!", areaCode));
            }
            area.getPointsCollectionList().add(pointsCollection);

            // now fix dimensions of X, Y, Z (all occurences of areaXWidth, areaYLength and areaZHeight!
            fixOccurencesOfDimensions(pointsCollectionPair.getV(), area);
        }

        return new HomeWiringData(areaList, pointsCollectionsList.stream().map(a -> a.getU()).collect(Collectors.toList()));
    }

    private static void fixOccurencesOfDimensions(List<APTripple> apTripples, Area area) {
        for (APTripple apTripple : apTripples) {
            AbstractPoint aPoint = apTripple.getU();
            Props props = apTripple.getV();
            if (props.containsKey("x-areaXWidth-computation") && props.get("x-areaXWidth-computation").equals(true)) {
                aPoint.setX(area.getxWidth() + aPoint.getX());
            }
            if (props.containsKey("y-areaYLength-computation") && props.get("y-areaYLength-computation").equals(true)) {
                aPoint.setY(area.getyLength() + aPoint.getY());
            }
            if (props.containsKey("z-areaZHeight-computation") && props.get("z-areaZHeight-computation").equals(true)) {
                aPoint.setZ(area.getzHeight() + aPoint.getZ());
            }
        }
    }

    private static List<Area> processAreasElement(Element elemAreas) {
        List<Area> resultList = new ArrayList<>();
        NodeList nodeList = elemAreas.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE && "area".equals(node.getNodeName())) {
                resultList.add(processAreaElement((Element) node));
            }
        }
        return resultList;
    }

    private static Area processAreaElement(Element elemArea) {
        Area area = new Area();
        area.setId(getOptionalAttribute(elemArea, "id"));
        area.setCode(getMandatoryAttribute(elemArea, "code"));
        area.setName(getOptionalAttribute(elemArea, "name"));
        area.setDescription(getOptionalAttribute(elemArea, "description"));
        area.setX(Utils.parseMeasures(getMandatoryAttribute(elemArea, "x")));
        area.setY(Utils.parseMeasures(getMandatoryAttribute(elemArea, "y")));
        area.setZ(Utils.parseMeasures(getMandatoryAttribute(elemArea, "z")));
        area.setxWidth(Utils.parseMeasures(getOneOfAttributes(elemArea, "xWidth", "xW")));
        area.setyLength(Utils.parseMeasures(getOneOfAttributes(elemArea, "yLength", "yL")));
        area.setzHeight(Utils.parseMeasures(getOneOfAttributes(elemArea, "zHeight", "zH")));

        area.setItems(new ArrayList<>());
        // now load items
        NodeList nodeList = elemArea.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE && "item".equals(node.getNodeName())) {
                area.getItems().add(processAreaItemElement((Element) node, area));
            }
        }

        area.setPointsCollectionList(new ArrayList<>());
        return area;
    }

    private static AreaItem processAreaItemElement(Element elemItem, Area area) {
        AreaItem item = new AreaItem();
        item.setType(getMandatoryAttribute(elemItem, "type"));

        Pair<Double, Utils.AreaRef> xMeasures = Utils.parseMeasuresWithAreaRef(getMandatoryAttribute(elemItem, "x"), Utils.AreaRef.areaXWidth);
        item.setX(xMeasures.getV() == Utils.AreaRef.areaXWidth ? area.getxWidth() + xMeasures.getU() : xMeasures.getU());
        Pair<Double, Utils.AreaRef> yMeasures = Utils.parseMeasuresWithAreaRef(getMandatoryAttribute(elemItem, "y"), Utils.AreaRef.areaYLength);
        item.setY(yMeasures.getV() == Utils.AreaRef.areaYLength ? area.getyLength() + yMeasures.getU() : yMeasures.getU());
        Pair<Double, Utils.AreaRef> zMeasures = Utils.parseMeasuresWithAreaRef(getMandatoryAttribute(elemItem, "z"), Utils.AreaRef.areaZHeight);
        item.setZ(zMeasures.getV() == Utils.AreaRef.areaZHeight ? area.getzHeight() + zMeasures.getU() : zMeasures.getU());

        item.setxWidth(Utils.parseMeasures(getOneOfAttributes(elemItem, "xWidth", "xW")));
        item.setyLength(Utils.parseMeasures(getOneOfAttributes(elemItem, "yLength", "yL")));
        item.setzHeight(Utils.parseMeasures(getOneOfAttributes(elemItem, "zHeight", "zH")));
        return item;
    }

    private static String getOptionalAttribute(Element elem, String attrName) {
        return elem.hasAttribute(attrName) ? elem.getAttribute(attrName) : null;
    }

    private static Double getOptionalMeasuresAttribute(Element elem, String attrName) {
        return elem.hasAttribute(attrName) ? Utils.parseMeasures(elem.getAttribute(attrName)) : null;
    }

    private static Pair<Double, Utils.AreaRef> getOptionalMeasuresAttributeWithAreaRef(Element elem, String attrName, Utils.AreaRef allowedAreaRef) {
        return elem.hasAttribute(attrName) ? Utils.parseMeasuresWithAreaRef(elem.getAttribute(attrName), allowedAreaRef) : null;
    }

    private static String getMandatoryAttribute(Element elem, String attrName) {
        if (!elem.hasAttribute(attrName)) {
            throw new RuntimeException(String.format("No attr: %s present for element: %s", attrName, elem));
        } else {
            return elem.getAttribute(attrName);
        }
    }

    private static String getOneOfAttributes(Element elem, String attrName1, String attrName2) {
        boolean attr1Present = elem.hasAttribute(attrName1);
        boolean attr2Present = elem.hasAttribute(attrName2);
        if (attr1Present && attr2Present) {
            throw new RuntimeException(String.format("Ambiguous attributes present: %s, %s. For element: %s", attrName1, attrName2, elem));
        }
        if (attr1Present) {
            return elem.getAttribute(attrName1);
        } else if (attr2Present) {
            return elem.getAttribute(attrName2);
        } else {
            return null;
        }
    }

    private static class PairOfPointCollections extends Pair<PointsCollection, List<APTripple>> {
        public PairOfPointCollections(PointsCollection pointsCollection, List<APTripple> apTripples) {
            super(pointsCollection, apTripples);
        }
    }

    private static PairOfPointCollections processPointsCollectionElement(Element elemPointsCollection) {
        PointsCollection pointsCollection = new PointsCollection();
        pointsCollection.setAreaCode(getMandatoryAttribute(elemPointsCollection, "areaCode"));
        pointsCollection.setPointTypeResolver(getOptionalAttribute(elemPointsCollection, "pointTypeResolver"));

        pointsCollection.setPoints(new ArrayList<>());
        List<APTripple> aChildren = new ArrayList<>();
        NodeList nodeList = elemPointsCollection.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                APTripple res = processPointsCollectionSubElement((Element) node, null);
                pointsCollection.getPoints().add(res.getU());
                aChildren.add(res);
            }
        }

        return new PairOfPointCollections(pointsCollection, aChildren);
    }

    public static class APTripple extends Tripple<AbstractPoint, Props, List<APTripple>> {

        public APTripple(AbstractPoint abstractPoint, Props sp, List<APTripple> apTrippleList) {
            super(abstractPoint, sp, apTrippleList);
        }
    }

    private static APTripple processPointsCollectionSubElement(Element elemPoint, AbstractPoint parent) {
        List<APTripple> aChildren = new ArrayList<>();
        AbstractPoint aPoint;
        if ("group".equals(elemPoint.getNodeName())) {
            PointGroup pointGroup = new PointGroup();

            // iterate over child nodes
            pointGroup.setChildren(new ArrayList<>());
            NodeList nodeList = elemPoint.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    APTripple child = processPointsCollectionSubElement((Element) node, pointGroup);
                    pointGroup.getChildren().add(child.getU());
                    aChildren.add(child);
                }
            }

            aPoint = pointGroup;
        } else if ("point".equals(elemPoint.getNodeName())) {
            aPoint = new Point();
        } else {
            throw new RuntimeException("Unexpected sub-element for points-collection elements: " + elemPoint);
        }

        // load attributes
        aPoint.setId(getOptionalAttribute(elemPoint, "id"));
        aPoint.setCode(getOptionalAttribute(elemPoint, "code"));
        aPoint.setName(getOptionalAttribute(elemPoint, "name"));
        aPoint.setDescription(getOptionalAttribute(elemPoint, "description"));

        aPoint.setType(getOptionalAttribute(elemPoint, "type"));
        Pair<Double, Utils.AreaRef> xMeasures = getOptionalMeasuresAttributeWithAreaRef(elemPoint, "x", Utils.AreaRef.areaXWidth);
        Pair<Double, Utils.AreaRef> yMeasures = getOptionalMeasuresAttributeWithAreaRef(elemPoint, "y", Utils.AreaRef.areaYLength);
        Pair<Double, Utils.AreaRef> zMeasures = getOptionalMeasuresAttributeWithAreaRef(elemPoint, "z", Utils.AreaRef.areaZHeight);
        aPoint.setX(xMeasures != null ? xMeasures.getU() : null);
        aPoint.setY(yMeasures != null ? yMeasures.getU() : null);
        aPoint.setZ(zMeasures != null ? zMeasures.getU() : null);
        aPoint.setdX(getOptionalMeasuresAttribute(elemPoint, "dX"));
        aPoint.setdY(getOptionalMeasuresAttribute(elemPoint, "dY"));
        aPoint.setdZ(getOptionalMeasuresAttribute(elemPoint, "dZ"));

        aPoint.setParent(parent);

        Props stringProp = new Props();
        if (xMeasures != null && xMeasures.getV() != null) {
            stringProp.put("x-areaXWidth-computation", true);
        }
        if (yMeasures != null && yMeasures.getV() != null) {
            stringProp.put("y-areaYLength-computation", true);
        }
        if (zMeasures != null && zMeasures.getV() != null) {
            stringProp.put("z-areaZHeight-computation", true);
        }
        return new APTripple(aPoint, stringProp, aChildren);
    }

}
