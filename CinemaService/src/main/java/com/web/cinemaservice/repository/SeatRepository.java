package com.web.cinemaservice.repository;

import com.web.cinemaservice.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByRoomIdOrderByName(Integer roomId);
}
