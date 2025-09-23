package com.web.cinemaservice.mapper;

import com.web.cinemaservice.dto.request.SeatRequest;
import com.web.cinemaservice.dto.response.SeatResponse;
import com.web.cinemaservice.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "room", ignore = true)
    Seat toSeat(SeatRequest request);

    SeatResponse toSeatResponse(Seat seat);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "room", ignore = true)
    void updateSeat(@MappingTarget Seat seat, SeatRequest request);
}
