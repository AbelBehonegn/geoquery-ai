package com.geoqueryai.backend.dto;

public class DistanceResponse {

    private Long fromParcelId;
    private Long toParcelId;
    private Double distanceMeters;

    public DistanceResponse() {
    }

    public DistanceResponse(Long fromParcelId,
                            Long toParcelId,
                            Double distanceMeters) {
        this.fromParcelId = fromParcelId;
        this.toParcelId = toParcelId;
        this.distanceMeters = distanceMeters;
    }

    public Long getFromParcelId() {
        return fromParcelId;
    }

    public void setFromParcelId(Long fromParcelId) {
        this.fromParcelId = fromParcelId;
    }

    public Long getToParcelId() {
        return toParcelId;
    }

    public void setToParcelId(Long toParcelId) {
        this.toParcelId = toParcelId;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }
}