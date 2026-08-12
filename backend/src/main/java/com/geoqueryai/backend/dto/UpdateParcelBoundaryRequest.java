package com.geoqueryai.backend.dto;

import java.util.List;

public class UpdateParcelBoundaryRequest {

    private List<List<Double>> boundary;

    public UpdateParcelBoundaryRequest() {
    }

    public List<List<Double>> getBoundary() {
        return boundary;
    }

    public void setBoundary(List<List<Double>> boundary) {
        this.boundary = boundary;
    }
}