package com.geoqueryai.backend.entity;

public class Parcel {

    private Long id;
    private String ownerName;
    private String address;
    private Double area;

    // Default Constructor
    public Parcel() {
    }

    // Parameterized Constructor
    public Parcel(Long id, String ownerName, String address, Double area) {
        this.id = id;
        this.ownerName = ownerName;
        this.address = address;
        this.area = area;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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