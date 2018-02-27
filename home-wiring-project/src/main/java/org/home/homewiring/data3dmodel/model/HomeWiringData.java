package org.home.homewiring.data3dmodel.model;

import java.util.List;

public class HomeWiringData {
    private List<Area> areas;
    private List<PointsCollection> pointsCollections;

    public HomeWiringData() {
    }

    public HomeWiringData(List<Area> areas, List<PointsCollection> pointsCollections) {
        this.areas = areas;
        this.pointsCollections = pointsCollections;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }

    public List<PointsCollection> getPointsCollections() {
        return pointsCollections;
    }

    public void setPointsCollections(List<PointsCollection> pointsCollections) {
        this.pointsCollections = pointsCollections;
    }
}
