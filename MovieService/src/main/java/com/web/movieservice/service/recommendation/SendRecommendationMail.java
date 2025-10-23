package com.web.movieservice.service.recommendation;

import com.web.movieservice.dto.response.UserRecommendationResponse;
import com.web.movieservice.entity.Showtime;

import java.util.LinkedList;

public interface SendRecommendationMail {
    void startAsyncTask(Integer movieId, Showtime showtime);
}
