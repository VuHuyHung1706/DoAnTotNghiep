package com.web.movieservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {
    private Integer id;
    private String title;
    private String description;
    private Integer duration;
    private String language;
    private String poster;
    private String trailer;
    private LocalDate releaseDate;
    private Set<GenreResponse> genres;
    private Set<ActorResponse> actors;
    private boolean nowShowing;
    private boolean upcoming;
}
