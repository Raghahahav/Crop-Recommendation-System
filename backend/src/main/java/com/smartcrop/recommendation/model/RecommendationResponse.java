package com.smartcrop.recommendation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecommendationResponse {

    @JsonProperty("recommended_crop")
    private final String recommendedCrop;

    public RecommendationResponse(String recommendedCrop) {
        this.recommendedCrop = recommendedCrop;
    }

    public String getRecommendedCrop() {
        return recommendedCrop;
    }
}
