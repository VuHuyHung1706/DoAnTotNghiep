package com.web.cinemaservice.mapper;

import com.web.cinemaservice.dto.request.RoomRequest;
import com.web.cinemaservice.dto.response.RoomResponse;
import com.web.cinemaservice.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cinema", ignore = true)
    @Mapping(target = "seats", ignore = true)
    Room toRoom(RoomRequest request);

    @Mapping(source = "cinema.name", target = "cinemaName")
    RoomResponse toRoomResponse(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cinema", ignore = true)
    @Mapping(target = "seats", ignore = true)
    void updateRoom(@MappingTarget Room room, RoomRequest request);
}
