package com.geoqueryai.backend.dto;

import java.util.List;

public class GeoJsonFeatureCollection {

    private String type = "FeatureCollection";

    private List<GeoJsonFeature> features;

    public GeoJsonFeatureCollection() {
    }

    public GeoJsonFeatureCollection(List<GeoJsonFeature> features) {
        this.features = features;
    }

    public String getType() {
        return type;
    }

    public List<GeoJsonFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<GeoJsonFeature> features) {
        this.features = features;
    }
}