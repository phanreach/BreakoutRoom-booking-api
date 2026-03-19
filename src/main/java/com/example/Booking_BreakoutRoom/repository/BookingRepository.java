package com.example.Booking_BreakoutRoom.repository;

import com.example.Booking_BreakoutRoom.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByRoomId(Long roomId);

    // ✅ CHECK TIME CONFLICT
    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id = :roomId
        AND b.date = :date
        AND (
            :startTime < b.endTime AND :endTime > b.startTime
        )
    """)
    List<Booking> findConflictingBookings(
            Long roomId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );
}