package com.web.showtimeservice.mapper;

import com.web.showtimeservice.dto.request.ShowtimeRequest;
import com.web.showtimeservice.dto.response.ShowtimeResponse;
import com.web.showtimeservice.entity.Showtime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    Showtime toShowtime(ShowtimeRequest request);

    ShowtimeResponse toShowtimeResponse(Showtime showtime);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    void updateShowtime(@MappingTarget Showtime showtime, ShowtimeRequest request);
}
