package com.example.ov_artifact.repository;

import com.example.ov_artifact.entity.Room;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
public interface RoomRepository extends JpaRepository<Room, String> {
    boolean existsByRoomNumber(String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.roomType.typeId = :typeId AND " +
            "r.status != 'MAINTENANCE' AND " +
            "r.roomId NOT IN (" +
            "  SELECT res.room.roomId FROM Reservation res " +
            "  WHERE (res.checkIn < :checkOut AND res.checkOut > :checkIn) " +
            "  AND (res.status = 'PENDING' OR res.status = 'CONFIRMED')" +
            ")")
    List<Room> findAvailableRooms(
            @Param("typeId") String typeId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}
