package com.geoqueryai.backend.dto;

import java.util.List;

public class CreateParcelRequest {

    private String ownerName;
    private String address;
    private Double area;
    private Double latitude;
    private Double longitude;

    /*
     * Each inner list contains:
     * [longitude, latitude]
     *
     * Example:
     * [
     *   [-77.1530, 39.0838],
     *   [-77.1525, 39.0838],
     *   [-77.1525, 39.0842],
     *   [-77.1530, 39.0842],
     *   [-77.1530, 39.0838]
     * ]
     */
    private List<List<Double>> boundary;

    public CreateParcelRequest() {
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<List<Double>> getBoundary() {
        return boundary;
    }

    public void setBoundary(List<List<Double>> boundary) {
        this.boundary = boundary;
    }
}