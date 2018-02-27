package org.home.homewiring.data3d_to_topview;

import org.home.homewiring.data3dmodel.model.AbstractPoint;
import org.home.homewiring.data3dmodel.model.Area;
import org.home.homewiring.data3dmodel.model.PointGroup;
import org.home.homewiring.data3dmodel.model.PointsCollection;
import org.home.homewiring.topview.Utils;
import org.home.homewiring.topview.model.TopViewArea;
import org.home.homewiring.topview.model.TopViewConfiguration;
import org.home.homewiring.topview.model.TopViewModel;
import org.home.homewiring.topview.model.TopViewSymbol;

import java.util.ArrayList;
import java.util.List;

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

    public static TopViewModel generate(List<Area> areaList) {
        TopViewModel topViewModel = new TopViewModel();
        topViewModel.setTopViewConfiguration(TopViewConfiguration.getInstance());
        topViewModel.setAreas(new ArrayList<>());
        for (Area area : areaList) {
            TopViewArea tvArea = new TopViewArea();
            topViewModel.getAreas().add(tvArea);
            tvArea.setSymbols(new ArrayList<>());
            populateTopViewAreaWithAreaData(tvArea, area, topViewModel.getConfiguration());

            List<AbstractPoint> flattenedPointsList = new ArrayList<>();
            for (PointsCollection pointsCollection : area.getPointsCollectionList()) {
                flattenPointsList(pointsCollection.getPoints(), flattenedPointsList);
            }

            generateTopViewSymbols(area, flattenedPointsList, tvArea, topViewModel);
        }
        return topViewModel;
    }


    private static void generateTopViewSymbols(Area area, List<AbstractPoint> flattenedPointsList, TopViewArea tvArea, TopViewModel topViewModel) {
        TopViewConfiguration config = topViewModel.getConfiguration();
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
