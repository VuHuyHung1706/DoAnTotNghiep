package com.web.movieservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieWithShowtimesResponse {
    private Integer movieId;
    private String title;
    private String description;
    private Integer duration;
    private String poster;
    private String trailer;
    private Double rating;
    private List<ShowtimeDetailResponse> showtimes;
}
