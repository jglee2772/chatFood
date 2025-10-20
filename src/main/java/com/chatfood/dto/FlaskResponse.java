package com.chatfood.dto;

import java.util.List;

// Python Flask 서버의 /recommend 응답 전체를 담을 클래스
public class FlaskResponse {
    private String status;
    private List<Recommendation> recommendations;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
