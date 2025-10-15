package com.web.movieservice.mapper;


import com.web.movieservice.dto.response.ActorResponse;
import com.web.movieservice.entity.Actor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActorMapper {
    ActorResponse toActorResponse(Actor actor);
}
