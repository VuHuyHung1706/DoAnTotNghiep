package com.web.movieservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRequestMultipart {
    @NotBlank(message = "TITLE_NOT_BLANK")
    private String title;

    private String description;

    @NotNull(message = "DURATION_NOT_NULL")
    @Min( value = 1, message = "DURATION_MIN")
    private Integer duration;

    private String language;
    private MultipartFile posterFile;
    private String trailer;
    private LocalDate releaseDate;
    private Set<Integer> genreIds;
    private Set<Integer> actorIds;

}
