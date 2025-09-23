package com.web.movieservice.controller;

import com.web.movieservice.dto.response.ActorResponse;
import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.service.actor.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/actors")
public class ActorController {

    @Autowired
    private ActorService actorService;

    @GetMapping
    public ApiResponse<List<ActorResponse>> getAllActors() {
        return ApiResponse.<List<ActorResponse>>builder()
                .result(actorService.getAllActors())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ActorResponse> getActorById(@PathVariable Integer id) {
        return ApiResponse.<ActorResponse>builder()
                .result(actorService.getActorById(id))
                .build();
    }
}
