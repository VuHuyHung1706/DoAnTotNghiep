package com.web.movieservice.repository;

import com.web.movieservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByMovieId(Integer movieId);

    List<Review> findByUsername(String username);

    Optional<Review> findByMovieIdAndUsername(Integer movieId, String username);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movieId = :movieId AND r.isDefault = 0")
    Double getAverageRatingByMovieId(Integer movieId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.movieId = :movieId AND r.isDefault = 0")
    Long getReviewCountByMovieId(Integer movieId);

    boolean existsByMovieIdAndUsername(Integer movieId, String username);

    @Query("SELECT r FROM Review r WHERE r.movieId = :movieId AND r.isDefault = 0")
    List<Review> findNonDefaultReviewsByMovieId(Integer movieId);

    void deleteByMovieIdAndUsernameAndIsDefault(Integer movieId, String username, Integer isDefault);
}
