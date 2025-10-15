package com.web.cinemaservice.mapper;

import com.web.cinemaservice.dto.request.CinemaRequest;
import com.web.cinemaservice.dto.response.CinemaResponse;
import com.web.cinemaservice.entity.Cinema;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CinemaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    Cinema toCinema(CinemaRequest request);

    CinemaResponse toCinemaResponse(Cinema cinema);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    void updateCinema(@MappingTarget Cinema cinema, CinemaRequest request);
}
