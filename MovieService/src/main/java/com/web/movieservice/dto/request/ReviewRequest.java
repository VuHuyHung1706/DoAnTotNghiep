package com.web.movieservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

    @NotNull(message = "MOVIE_ID_NOT_NULL")
    private Integer movieId;

    @NotNull(message = "RATING_NOT_NULL")
    @Min(value = 1, message = "RATING_MIN")
    @Max(value = 5, message = "RATING_MAX")
    private Integer rating;

    private String comment;
}
