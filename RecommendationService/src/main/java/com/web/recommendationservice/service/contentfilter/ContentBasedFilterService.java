package com.web.recommendationservice.service.contentfilter;

import com.web.recommendationservice.dto.response.MovieResponse;
import com.web.recommendationservice.entity.UserPreference;

import java.util.List;

public interface ContentBasedFilterService {
    double calculateSimilarityScore(MovieResponse movie, List<UserPreference> userPreferences);
}
