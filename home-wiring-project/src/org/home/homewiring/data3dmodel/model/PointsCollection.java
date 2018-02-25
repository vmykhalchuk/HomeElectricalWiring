package org.home.homewiring.data3dmodel.model;

import java.util.List;

public class PointsCollection {
    private String areaCode;
    private String pointTypeResolver;
    private List<AbstractPoint> points;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPointTypeResolver() {
        return pointTypeResolver;
    }

    public void setPointTypeResolver(String pointTypeResolver) {
        this.pointTypeResolver = pointTypeResolver;
    }

    public List<AbstractPoint> getPoints() {
        return points;
    }

    public void setPoints(List<AbstractPoint> points) {
        this.points = points;
    }
}
