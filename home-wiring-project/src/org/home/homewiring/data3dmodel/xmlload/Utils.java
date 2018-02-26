package org.home.homewiring.data3dmodel.xmlload;

public class Utils {

    /**
     * Will parse measures in cm, m, dm, mm into mm.<br>
     * Examples:
     * <ul>
     * <li>"100.2" = 100.2</li>
     * <li>"55cm" = 550</li>
     * <li>"1.3m" = 1300</li>
     * <li>"1.3mm" = 1.3</li>
     * </ul>
     *
     * @param measuresStr
     * @return
     */
    public static Double parseMeasures(String measuresStr) {
        measuresStr = measuresStr.trim();

        Units units;
        int unitCharsCount;
        if (measuresStr.endsWith("mm")) {
            units = Units.mm;
            unitCharsCount = 2;
        } else if (measuresStr.endsWith("cm")) {
            units = Units.cm;
            unitCharsCount = 2;
        } else if (measuresStr.endsWith("dm")) {
            units = Units.dm;
            unitCharsCount = 2;
        } else if (measuresStr.endsWith("m")) {
            units = Units.m;
            unitCharsCount = 1;
        } else {
            units = Units.mm;
            unitCharsCount = 0;
        }

        String measuresVal = measuresStr.substring(0, measuresStr.length() - unitCharsCount).trim();
        double val = Double.parseDouble(measuresVal);
        switch (units) {
            case mm:
                return val;
            case cm:
                return val * 10;
            case dm:
                return val * 100;
            case m:
                return val * 1000;
            default:
                throw new RuntimeException();
        }
    }

    private enum Units {
        mm, cm, m, dm
    }
}
