package com.web.movieservice.service.actor;


import com.web.movieservice.dto.response.ActorResponse;

import java.util.List;

public interface ActorService {
    List<ActorResponse> getAllActors();
    ActorResponse getActorById(Integer id);
}
