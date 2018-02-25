package org.home.homewiring.data3dmodel.model;

import java.util.List;

public class PointGroup extends AbstractPoint {
    private List<AbstractPoint> children;

    public List<AbstractPoint> getChildren() {
        return children;
    }

    public void setChildren(List<AbstractPoint> children) {
        this.children = children;
    }
}
