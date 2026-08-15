package com.geoqueryai.backend.dto;

// =========================
// AI QUERY RESPONSE DTO
// =========================
// Stores the structured result
// returned after AI interprets
// the user's GIS question.
public class AiQueryResponse {

    // Action selected by AI:
    // nearby, contains, or help
    private String action;

    // Short message displayed
    // to the user
    private String message;

    // GIS parameters extracted
    // from the user's question
    private Double latitude;
    private Double longitude;
    private Double distance;


    // =========================
    // EMPTY CONSTRUCTOR
    // =========================
    // Required for JSON conversion
    public AiQueryResponse() {
    }


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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


    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}