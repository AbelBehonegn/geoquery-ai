package com.geoqueryai.backend.dto;

import java.util.Map;

public class GeoJsonFeature {

    private String type = "Feature";

    private Map<String, Object> geometry;

    private Map<String, Object> properties;

    public GeoJsonFeature() {
    }

    public GeoJsonFeature(
            Map<String, Object> geometry,
            Map<String, Object> properties) {

        this.geometry = geometry;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getGeometry() {
        return geometry;
    }

    public void setGeometry(Map<String, Object> geometry) {
        this.geometry = geometry;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}