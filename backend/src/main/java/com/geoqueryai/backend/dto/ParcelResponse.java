package com.geoqueryai.backend.dto;

public class ParcelResponse {

    private Long id;
    private String ownerName;
    private String address;
    private Double area;
    private Double latitude;
    private Double longitude;

    public ParcelResponse() {
    }

    public ParcelResponse(
            Long id,
            String ownerName,
            String address,
            Double area,
            Double latitude,
            Double longitude) {

        this.id = id;
        this.ownerName = ownerName;
        this.address = address;
        this.area = area;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getAddress() {
        return address;
    }

    public Double getArea() {
        return area;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}