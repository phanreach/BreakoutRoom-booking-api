package com.example.Booking_BreakoutRoom.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoomRequest {
    private String name;
    private String description;
    private String floor;
    private Integer capacity;
    private Boolean isAvailable;
}
