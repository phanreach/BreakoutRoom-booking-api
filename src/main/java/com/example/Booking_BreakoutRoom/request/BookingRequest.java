package com.example.Booking_BreakoutRoom.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private Long roomId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer participants;
    private String notes;
}