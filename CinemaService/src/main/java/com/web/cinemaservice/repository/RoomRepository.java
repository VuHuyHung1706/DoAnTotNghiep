package com.web.cinemaservice.repository;

import com.web.cinemaservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByCinemaId(Integer cinemaId);

    @Query("SELECT r FROM Room r WHERE " +
            "(:roomName IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :roomName, '%'))) AND " +
            "(:cinemaId IS NULL OR r.cinema.id = :cinemaId)")
    List<Room> searchRooms(@Param("roomName") String roomName, @Param("cinemaId") Integer cinemaId);
}
