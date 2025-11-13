package com.web.movieservice.service.recommendation;

import com.web.movieservice.entity.Showtime;

public interface RecommendationService {
    void startSendMailAsyncTask(Integer movieId, Showtime showtime);
    void updateUserPreferenceTask(String username);
}
