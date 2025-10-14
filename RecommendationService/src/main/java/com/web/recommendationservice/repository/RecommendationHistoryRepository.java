package com.web.recommendationservice.repository;

import com.web.recommendationservice.entity.RecommendationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommendationHistoryRepository extends JpaRepository<RecommendationHistory, Integer> {
    
    List<RecommendationHistory> findByUsername(String username);
    
    List<RecommendationHistory> findByUsernameAndRecommendedAtAfter(String username, LocalDateTime after);
    
    boolean existsByUsernameAndMovieId(String username, Integer movieId);
}
