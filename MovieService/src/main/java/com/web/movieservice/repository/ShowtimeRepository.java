package com.web.movieservice.repository;

import com.web.movieservice.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {
    @Query("SELECT s FROM Showtime s WHERE s.roomId = :roomId AND " +
            "((s.startTime <= :startTime AND s.endTime > :startTime) OR " +
            "(s.startTime < :endTime AND s.endTime >= :endTime) OR " +
            "(s.startTime >= :startTime AND s.endTime <= :endTime))")
    List<Showtime> findConflictingShowtimes(@Param("roomId") Integer roomId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    List<Showtime> findByMovieId(Integer movieId);
    List<Showtime> findByRoomIdIn(List<Integer> roomIds);
//    List<Showtime> findByRoomCinemaId(Integer cinemaId);
//    List<Showtime> findByMovieIdAndRoomCinemaId(Integer movieId, Integer cinemaId);
    List<Showtime> findByMovieIdAndRoomIdIn(Integer movieId, List<Integer> roomIds);
    List<Showtime> findByMovieIdAndRoomId(Integer movieId, Integer roomId);
    List<Showtime> findByRoomId(Integer roomId);
}
