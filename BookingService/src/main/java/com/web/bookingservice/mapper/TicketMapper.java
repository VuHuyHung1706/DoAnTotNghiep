package com.web.bookingservice.mapper;

import com.web.bookingservice.dto.response.TicketDetailResponse;
import com.web.bookingservice.dto.response.TicketResponse;
import com.web.bookingservice.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(source = "seat.name", target = "seatName")
    TicketResponse toTicketResponse(Ticket ticket);

    TicketDetailResponse toTicketDetailResponse(Ticket ticket);
}
