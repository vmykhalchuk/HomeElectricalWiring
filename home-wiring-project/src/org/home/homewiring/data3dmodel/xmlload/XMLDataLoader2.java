package org.home.homewiring.data3dmodel.xmlload;

import org.home.homewiring.data3dmodel.model.AbstractPoint;
import org.home.homewiring.data3dmodel.model.Area;
import org.home.homewiring.data3dmodel.model.AreaItem;
import org.home.homewiring.data3dmodel.model.Point;
import org.home.homewiring.data3dmodel.model.PointGroup;
import org.home.homewiring.data3dmodel.model.PointsCollection;
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

public class XMLDataLoader2 {

    public static List<Area> getAreaList(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        List<Area> areaList = new ArrayList<>();
        List<PointsCollection> pointsCollectionsList = new ArrayList<>();
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

        // now match Areas to PointsCollections
        for (PointsCollection pointsCollection : pointsCollectionsList) {
            String areaCode = pointsCollection.getAreaCode();
            Area area = areaList.stream().filter((a) -> areaCode.equals(a.getCode())).findFirst().orElse(null);
            if (area == null) {
                throw new RuntimeException(String.format("No area with code: '%s' found! Cannot set pointsCollection for this Area!", areaCode));
            }
            area.getPointsCollectionList().add(pointsCollection);
        }

        return areaList;
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
                area.getItems().add(processAreaItemElement((Element) node));
            }
        }

        area.setPointsCollectionList(new ArrayList<>());
        return area;
    }

    private static AreaItem processAreaItemElement(Element elemItem) {
        AreaItem item = new AreaItem();
        item.setType(getMandatoryAttribute(elemItem, "type"));
        item.setX(Utils.parseMeasures(getMandatoryAttribute(elemItem, "x")));
        item.setY(Utils.parseMeasures(getMandatoryAttribute(elemItem, "y")));
        item.setZ(Utils.parseMeasures(getMandatoryAttribute(elemItem, "z")));
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


    private static PointsCollection processPointsCollectionElement(Element elemPointsCollection) {
        PointsCollection pointsCollection = new PointsCollection();
        pointsCollection.setAreaCode(getMandatoryAttribute(elemPointsCollection, "areaCode"));
        pointsCollection.setPointTypeResolver(getOptionalAttribute(elemPointsCollection, "pointTypeResolver"));

        pointsCollection.setPoints(new ArrayList<>());
        NodeList nodeList = elemPointsCollection.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                AbstractPoint point = processPointsCollectionSubElement((Element) node, null);
                pointsCollection.getPoints().add(point);
            }
        }


        return pointsCollection;
    }

    private static AbstractPoint processPointsCollectionSubElement(Element elemPoint, AbstractPoint parent) {
        AbstractPoint result;
        if ("group".equals(elemPoint.getNodeName())) {
            PointGroup pointGroup = new PointGroup();

            // iterate over child nodes
            pointGroup.setChildren(new ArrayList<>());
            NodeList nodeList = elemPoint.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    AbstractPoint child = processPointsCollectionSubElement((Element) node, pointGroup);
                    pointGroup.getChildren().add(child);
                }
            }

            result = pointGroup;
        } else if ("point".equals(elemPoint.getNodeName())) {
            result = new Point();
        } else {
            throw new RuntimeException("Unexpected sub-element for points-collection elements: " + elemPoint);
        }

        // load attributes
        result.setId(getOptionalAttribute(elemPoint, "id"));
        result.setCode(getOptionalAttribute(elemPoint, "code"));
        result.setName(getOptionalAttribute(elemPoint, "name"));
        result.setDescription(getOptionalAttribute(elemPoint, "description"));

        result.setType(getOptionalAttribute(elemPoint, "type"));
        result.setX(getOptionalMeasuresAttribute(elemPoint, "x"));
        result.setY(getOptionalMeasuresAttribute(elemPoint, "y"));
        result.setZ(getOptionalMeasuresAttribute(elemPoint, "z"));
        result.setdX(getOptionalMeasuresAttribute(elemPoint, "dX"));
        result.setdY(getOptionalMeasuresAttribute(elemPoint, "dY"));
        result.setdZ(getOptionalMeasuresAttribute(elemPoint, "dZ"));

        result.setParent(parent);
        return result;
    }

}
