package com.web.movieservice.repository;

import com.web.movieservice.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    List<Movie> findByTitleContainingIgnoreCase(String title);
    List<Movie> findByIdIn(List<Integer> ids);
    List<Movie> findByReleaseDateAfterOrderByReleaseDateAsc(LocalDate date);
}
