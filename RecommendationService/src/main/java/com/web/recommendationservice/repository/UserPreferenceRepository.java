package com.web.recommendationservice.repository;

import com.web.recommendationservice.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer> {

    List<UserPreference> findByUsername(String username);

    Optional<UserPreference> findByUsernameAndGenreId(String username, Integer genreId);

    Optional<UserPreference> findByUsernameAndActorId(String username, Integer actorId);

    @Query("SELECT up FROM UserPreference up WHERE up.username = :username AND up.genreId IS NOT NULL ORDER BY up.preferenceScore DESC")
    List<UserPreference> findTopGenresByUsername(String username);

    @Query("SELECT up FROM UserPreference up WHERE up.username = :username AND up.actorId IS NOT NULL ORDER BY up.preferenceScore DESC")
    List<UserPreference> findTopActorsByUsername(String username);

    @Query("SELECT DISTINCT up.username FROM UserPreference up")
    List<String> findAllDistinctUsernames();
}
