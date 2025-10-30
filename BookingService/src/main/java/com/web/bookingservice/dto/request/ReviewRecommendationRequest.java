package com.web.bookingservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRecommendationRequest {
    @NotNull(message = "MOVIE_ID_NOT_NULL")
    private Integer movieId;

    @NotNull(message = "USERNAME_NOT_BLANK")
    private String username;
}
