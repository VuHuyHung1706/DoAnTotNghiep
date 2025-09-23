package com.web.movieservice.service.actor;


import com.web.movieservice.dto.response.ActorResponse;
import com.web.movieservice.entity.Actor;
import com.web.movieservice.exception.AppException;
import com.web.movieservice.exception.ErrorCode;
import com.web.movieservice.mapper.ActorMapper;
import com.web.movieservice.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActorServiceImpl implements ActorService {

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private ActorMapper actorMapper;

    @Override
    public List<ActorResponse> getAllActors() {
        return actorRepository.findAll().stream()
                .map(actorMapper::toActorResponse)
                .toList();
    }

    @Override
    public ActorResponse getActorById(Integer id) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ACTOR_NOT_EXISTED));
        return actorMapper.toActorResponse(actor);
    }
}
