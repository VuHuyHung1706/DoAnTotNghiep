package com.web.movieservice.mapper;

import com.web.movieservice.dto.request.ShowtimeRequest;
import com.web.movieservice.dto.response.ShowtimeResponse;
import com.web.movieservice.entity.Showtime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "movie", ignore = true)
    Showtime toShowtime(ShowtimeRequest request);

    @Mapping(target = "movie", source = "movie")
    ShowtimeResponse toShowtimeResponse(Showtime showtime);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "movie", ignore = true)
    void updateShowtime(@MappingTarget Showtime showtime, ShowtimeRequest request);
}
