package org.home.homewiring.topview.model;

public class TopViewConfiguration {
    private static TopViewConfiguration INSTANCE = new TopViewConfiguration();
    private double dataScaleFactor = 0.10;

    private TopViewConfiguration() {
    }

    public static TopViewConfiguration getInstance() {
        return INSTANCE;
    }

    public double getDataScaleFactor() {
        return dataScaleFactor;
    }

}
