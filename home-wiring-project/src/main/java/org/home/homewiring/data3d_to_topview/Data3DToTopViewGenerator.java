package org.home.homewiring.data3d_to_topview;

import org.home.homewiring.data3d_to_topview.mappers.TopViewAreaMapper;
import org.home.homewiring.data3dmodel.model.*;
import org.home.homewiring.topview.Utils;
import org.home.homewiring.topview.model.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Data3DToTopViewGenerator {

    private static void populateTopViewAreaWithAreaData(TopViewArea tvArea, Area area, TopViewConfiguration tvConf) {
        tvArea.setCode(area.getCode());
        tvArea.setX(area.getX() * tvConf.getDataScaleFactor());
        tvArea.setY(area.getY() * tvConf.getDataScaleFactor());
        tvArea.setxWidth(area.getxWidth() * tvConf.getDataScaleFactor());
        tvArea.setyLength(area.getyLength() * tvConf.getDataScaleFactor());
    }

    private static void flattenPointsList(List<AbstractPoint> points, List<AbstractPoint> flattenedPointsList) {
        for (AbstractPoint pg : points) {
            if (pg instanceof PointGroup && pg.getCode() == null) {
                flattenPointsList(((PointGroup) pg).getChildren(), flattenedPointsList);
            } else {
                flattenedPointsList.add(pg);
            }
        }
    }

    /**
     * Generate TopView 2D model out of 3D input data model
     */
    public static TopViewModel generate(HomeWiringData homeWiringData) {
        TopViewModel topViewModel = new TopViewModel();
        topViewModel.setTopViewConfiguration(TopViewConfiguration.getInstance());
        topViewModel.setAreas(new ArrayList<>());
        for (Area area : homeWiringData.getAreas()) {
            TopViewArea tvArea = new TopViewArea();
            topViewModel.getAreas().add(tvArea);
            tvArea.setSymbols(new ArrayList<>());
            populateTopViewAreaWithAreaData(tvArea, area, topViewModel.getConfiguration());

            List<AbstractPoint> flattenedPointsList = new ArrayList<>();
            for (PointsCollection pointsCollection : area.getPointsCollectionList()) {
                flattenPointsList(pointsCollection.getPoints(), flattenedPointsList);
            }

            generateTopViewSymbols(area, flattenedPointsList, tvArea, topViewModel.getConfiguration());
            tvArea.setItems(generateTopViewAreaItems(area.getItems(), topViewModel.getConfiguration()));
        }
        return topViewModel;
    }

    private static List<TopViewAreaItem> generateTopViewAreaItems(List<AreaItem> areaItems, TopViewConfiguration config) {
        TopViewAreaMapper mapper = Mappers.getMapper(TopViewAreaMapper.class);
        return areaItems.stream().map(item -> {
            TopViewAreaItem tvItem = new TopViewAreaItem();
            tvItem.setType(convertType(item.getType()));
            tvItem.setX(item.getX() * config.getDataScaleFactor());
            tvItem.setY(item.getY() * config.getDataScaleFactor());
            tvItem.setxWidth(item.getxWidth() * config.getDataScaleFactor());
            tvItem.setyLength(item.getyLength() * config.getDataScaleFactor());
            return tvItem;
        }).collect(Collectors.toList());
    }

    private static TopViewAreaItem.Type convertType(String areaItemType) {
        switch (areaItemType) {
            case "door":
                return TopViewAreaItem.Type.door;
            case "window":
                return TopViewAreaItem.Type.window;
            case "opening":
                return TopViewAreaItem.Type.opening;
            default:
                throw new RuntimeException("Wrong Area Item Type: " + areaItemType);
        }
    }

    private static void generateTopViewSymbols(Area area, List<AbstractPoint> flattenedPointsList, TopViewArea tvArea, TopViewConfiguration config) {
        for (AbstractPoint point : flattenedPointsList) {
            TopViewSymbol tvSymbol = new TopViewSymbol();
            tvArea.getSymbols().add(tvSymbol);

            tvSymbol.setPointType(point.getCalculatedType());
            tvSymbol.setPointX(point.getCalculatedX() * config.getDataScaleFactor());
            tvSymbol.setPointY(point.getCalculatedY() * config.getDataScaleFactor());

            if (point instanceof PointGroup) {
                tvSymbol.setInnerText("" + ((PointGroup) point).getChildren().size());
            }

            tvSymbol.setLabelText(point.getCode());

            tvSymbol.setColor(Utils.colorByZ(point.getCalculatedZ(), area.getzHeight()));

            if (false && point.getTopViewSymbolManualData() != null) {
                // use manual overrides
                // FIXME Implement Manual overrides!
            } else {
//                // automatically place topViewSymbol and its caption text
//                Utils.POINT_LOCATION location = Utils.locatePoint(tvSymbol.getPointX(), tvSymbol.getPointY(), tvArea.getxWidth(), tvArea.getyLength());
//                switch (location) {
//                    case LEFT:
//                    case TOP_LEFT:
//                    case BOTTOM_LEFT:
//                        tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.RIGHT);
//                        break;
//                    case RIGHT:
//                    case TOP_RIGHT:
//                    case BOTTOM_RIGHT:
//                        tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.LEFT);
//                        break;
//                    case TOP:
//                    case MIDDLE:
//                        tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.BELOW);
//                        break;
//                    case BOTTOM:
//                        tvSymbol.setLabelAlignment(TopViewSymbol.Label2SymbolAlignment.ABOVE);
//                        break;
//                    default:
//                        throw new RuntimeException(location.name());
//                }
            }
        }
    }


}
