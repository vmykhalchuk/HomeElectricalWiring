package org.home.homewiring.data3dmodel.xmlload;

import java.util.Arrays;

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
    public static Double parseMeasures(final String measuresStr) {
        final String measuresStrTrimmed = measuresStr.trim();
        int areaRefVarCount;
        AreaRef areaRef = Arrays.stream(AreaRef.values()).filter(a -> measuresStrTrimmed.startsWith(a.name())).
                findAny().orElse(null);
        final String measuresValueStr;
        if (areaRef != null) {
            measuresValueStr = measuresStrTrimmed.substring(areaRef.name().length()).trim();
        } else {
            measuresValueStr = measuresStrTrimmed;
        }

        // FIXME find a way to propagate this areaRef value!

        Units units;
        int unitCharsCount;
        if (measuresValueStr.endsWith("mm")) {
            units = Units.mm;
            unitCharsCount = 2;
        } else if (measuresValueStr.endsWith("cm")) {
            units = Units.cm;
            unitCharsCount = 2;
        } else if (measuresValueStr.endsWith("dm")) {
            units = Units.dm;
            unitCharsCount = 2;
        } else if (measuresValueStr.endsWith("m")) {
            units = Units.m;
            unitCharsCount = 1;
        } else {
            units = Units.mm;
            unitCharsCount = 0;
        }

        String measuresVal = measuresValueStr.substring(0, measuresValueStr.length() - unitCharsCount).trim();
        double val = measuresVal.isEmpty() ? 0 : Double.parseDouble(measuresVal);
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

    private enum AreaRef {
        areaXWidth, areaYLength, areaZHeight
    }
}
