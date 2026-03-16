package com.example.Booking_BreakoutRoom.repository;

import com.example.Booking_BreakoutRoom.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
