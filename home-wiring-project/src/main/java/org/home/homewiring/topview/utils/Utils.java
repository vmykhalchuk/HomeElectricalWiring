package org.home.homewiring.topview.utils;

public class Utils {

    public static double POINT_LOCATION_MARGIN = 0.15; // 15%

    private Utils() {
    }


    public static POINT_LOCATION locatePoint(double pointX, double pointY, double areaXWidth, double areaYLength) {
        // check if point is within margin% of left border
        boolean left = pointX < (areaXWidth * POINT_LOCATION_MARGIN);
        // check if point is within margin% of left border
        boolean right = pointX > (areaXWidth * (1 - POINT_LOCATION_MARGIN));
        // check if point is within margin% of top border
        boolean top = pointY < (areaYLength * POINT_LOCATION_MARGIN);
        // check if point is within margin% of bottom border
        boolean bottom = pointY > (areaYLength * (1 - POINT_LOCATION_MARGIN));

        if (left) {
            if (top) {
                return POINT_LOCATION.TOP_LEFT;
            } else if (bottom) {
                return POINT_LOCATION.BOTTOM_LEFT;
            } else {
                return POINT_LOCATION.LEFT;
            }
        } else if (right) {
            if (top) {
                return POINT_LOCATION.TOP_RIGHT;
            } else if (bottom) {
                return POINT_LOCATION.BOTTOM_RIGHT;
            } else {
                return POINT_LOCATION.RIGHT;
            }
        } else if (top) {
            return POINT_LOCATION.TOP;
        } else if (bottom) {
            return POINT_LOCATION.BOTTOM;
        } else {
            return POINT_LOCATION.MIDDLE;
        }
    }

    public static String colorByZ(double z, double zHeight) {
        if (z < 0) {
            return "#ff0000"; // red
        } else if (z >= 0 && z < zHeight * 0.25) {
            return "#0000ff"; // blue
        } else if (z >= zHeight * 0.25 && z < zHeight * 0.5) {
            return "#00ff00"; // green
        } else if (z >= zHeight * 0.5 && z < zHeight * 0.75) {
            return "#990000"; // dark red
        } else if (z >= zHeight * 0.75 && z <= zHeight) {
            return "#999999"; // grey
        } else {
            return "#ff0000"; // red
        }

    }

    public enum POINT_LOCATION {
        LEFT, RIGHT, TOP, BOTTOM, MIDDLE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

}
