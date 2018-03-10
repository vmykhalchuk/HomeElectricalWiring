package org.home.homewiring.data3dmodel.xmlload;

import org.home.utils.Pair;

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
    public static Pair<Double, AreaRef> parseMeasuresWithAreaRef(final String measuresStr, AreaRef allowedAreaRef) {
        final String measuresStrTrimmed = measuresStr == null ? "" : measuresStr.trim();

        AreaRef areaRef = Arrays.stream(AreaRef.values()).filter(a -> measuresStrTrimmed.startsWith(a.name())).
                findAny().orElse(null);
        final String measuresValueStr;
        if (areaRef != null) {
            measuresValueStr = measuresStrTrimmed.substring(areaRef.name().length()).trim();
        } else {
            measuresValueStr = measuresStrTrimmed;
        }

        if (areaRef != null && allowedAreaRef != null && areaRef != allowedAreaRef) {
            throw new RuntimeException(String.format("areaRef '%s' not allowed! Allowed is: %s. InputString: %s", areaRef, allowedAreaRef, measuresStr));
        }

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
                return new Pair<>(val, areaRef);
            case cm:
                return new Pair<>(val * 10, areaRef);
            case dm:
                return new Pair<>(val * 100, areaRef);
            case m:
                return new Pair<>(val * 1000, areaRef);
            default:
                throw new RuntimeException();
        }
    }

    public static Double parseMeasures(final String measuresStr) {
        Pair<Double, AreaRef> parsedVal = parseMeasuresWithAreaRef(measuresStr, null);
        if (parsedVal.getV() != null) {
            throw new RuntimeException("Not allowed to use areaXWidth, areaYLength or areaZHeight in here! For inputString: " + measuresStr);
        }
        return parsedVal.getU();
    }

    private enum Units {
        mm, cm, m, dm
    }

    public enum AreaRef {
        areaXWidth, areaYLength, areaZHeight
    }
}
