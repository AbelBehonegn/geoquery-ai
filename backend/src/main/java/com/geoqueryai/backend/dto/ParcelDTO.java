package com.geoqueryai.backend.dto;

public class ParcelDTO {

    private String ownerName;
    private String address;
    private Double area;

    public ParcelDTO() {
    }

    public ParcelDTO(String ownerName, String address, Double area) {
        this.ownerName = ownerName;
        this.address = address;
        this.area = area;
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
}