package com.geoqueryai.backend.dto;

// =========================
// AI QUERY REQUEST DTO
// =========================
// Receives the natural-language
// question from the React frontend.
public class AiQueryRequest {

    private String query;

    // Empty constructor required
    // for JSON conversion.
    public AiQueryRequest() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}