package com.example.Booking_BreakoutRoom.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private RoomSummary room;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer participants;
    private String notes;
}
