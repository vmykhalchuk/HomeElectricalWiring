package org.home.homewiring.data3dmodel.model;

public abstract class AbstractPoint {

    private String id;
    private String code;
    private String name;
    private String description;
    /**
     * S - Socket, W - Switch, M - Misc, etc.
     */
    private String type;
    private Double x;
    private Double y;
    private Double z;
    private Double dX;
    private Double dY;
    private Double dZ;
    private AbstractPoint parent;
    private TopViewSymbolManualData topViewSymbolManualData;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Double getdX() {
        return dX;
    }

    public void setdX(Double dX) {
        this.dX = dX;
    }

    public Double getdY() {
        return dY;
    }

    public void setdY(Double dY) {
        this.dY = dY;
    }

    public Double getdZ() {
        return dZ;
    }

    public void setdZ(Double dZ) {
        this.dZ = dZ;
    }

    public AbstractPoint getParent() {
        return parent;
    }

    public void setParent(AbstractPoint parent) {
        this.parent = parent;
    }

    public TopViewSymbolManualData getTopViewSymbolManualData() {
        return topViewSymbolManualData;
    }

    public void setTopViewSymbolManualData(TopViewSymbolManualData topViewSymbolManualData) {
        this.topViewSymbolManualData = topViewSymbolManualData;
    }

    public String getCalculatedType() {
        if (type == null) {
            String type = parent == null ? null : parent.getCalculatedType();
            if (type == null) {
                // TODO: load type resolver here and call it to run below logic
                return code == null || code.length() == 0 ? null : code.substring(0, 1);
            } else {
                return type;
            }
        } else {
            return type;
        }
    }

    public Double getCalculatedX() {
        if (x != null) {
            return x;
        } else {
            Double parentX = parent == null ? 0d : parent.getCalculatedX();
            Double deltaX = this.getdX() == null ? 0d : this.getdX();
            return parentX + deltaX;
        }
    }

    public Double getCalculatedY() {
        if (y != null) {
            return y;
        } else {
            Double parentY = parent == null ? 0d : parent.getCalculatedY();
            Double deltaY = this.getdY() == null ? 0d : this.getdY();
            return parentY + deltaY;
        }
    }

    public Double getCalculatedZ() {
        if (z != null) {
            return z;
        } else {
            Double parentZ = parent == null ? 0d : parent.getCalculatedZ();
            Double deltaZ = this.getdZ() == null ? 0d : this.getdZ();
            return parentZ + deltaZ;
        }
    }

    public static class TopViewSymbolManualData {
        private Double x;
        private Double y;
        private Double dX;
        private Double dY;

        private Double textX;
        private Double textY;
        private Double textDX;
        private Double textDY;
        private Double textRotation;

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

        public Double getdX() {
            return dX;
        }

        public void setdX(Double dX) {
            this.dX = dX;
        }

        public Double getdY() {
            return dY;
        }

        public void setdY(Double dY) {
            this.dY = dY;
        }

        public Double getTextX() {
            return textX;
        }

        public void setTextX(Double textX) {
            this.textX = textX;
        }

        public Double getTextY() {
            return textY;
        }

        public void setTextY(Double textY) {
            this.textY = textY;
        }

        public Double getTextDX() {
            return textDX;
        }

        public void setTextDX(Double textDX) {
            this.textDX = textDX;
        }

        public Double getTextDY() {
            return textDY;
        }

        public void setTextDY(Double textDY) {
            this.textDY = textDY;
        }

        public Double getTextRotation() {
            return textRotation;
        }

        public void setTextRotation(Double textRotation) {
            this.textRotation = textRotation;
        }
    }
}
