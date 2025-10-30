package com.web.recommendationservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse {
    private Integer movieId;
    private String movieTitle;
    private Double similarityScore;
    private Double predictedRating;
    private String recommendationType;
    private String reason;
}
