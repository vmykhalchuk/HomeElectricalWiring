package org.home.homewiring.topview.model;

import java.util.List;

public class TopViewModel {
    private TopViewConfiguration topViewConfiguration;
    private List<TopViewArea> areas;

    public TopViewConfiguration getConfiguration() {
        return topViewConfiguration;
    }

    public void setTopViewConfiguration(TopViewConfiguration topViewConfiguration) {
        this.topViewConfiguration = topViewConfiguration;
    }

    public List<TopViewArea> getAreas() {
        return areas;
    }

    public void setAreas(List<TopViewArea> areas) {
        this.areas = areas;
    }

}
