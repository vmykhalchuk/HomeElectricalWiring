package org.home.homewiring.data3dmodel.model;

import javax.validation.Valid;
import java.util.List;

public class Area {
    private String id;
    private String code;
    private String name;
    private String description;
    private Double x;
    private Double y;
    private Double z;
    private Double xWidth;
    private Double yLength;
    private Double zHeight;

    @Valid
    private List<AreaItem> items;
    private List<PointsCollection> pointsCollectionList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Double getxWidth() {
        return xWidth;
    }

    public void setxWidth(Double xWidth) {
        this.xWidth = xWidth;
    }

    public Double getyLength() {
        return yLength;
    }

    public void setyLength(Double yLength) {
        this.yLength = yLength;
    }

    public Double getzHeight() {
        return zHeight;
    }

    public void setzHeight(Double zHeight) {
        this.zHeight = zHeight;
    }

    public List<AreaItem> getItems() {
        return items;
    }

    public void setItems(List<AreaItem> items) {
        this.items = items;
    }

    public List<PointsCollection> getPointsCollectionList() {
        return pointsCollectionList;
    }

    public void setPointsCollectionList(List<PointsCollection> pointsCollectionList) {
        this.pointsCollectionList = pointsCollectionList;
    }

}
