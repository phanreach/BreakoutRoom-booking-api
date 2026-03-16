package com.example.Booking_BreakoutRoom.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String name;
    private String floor;
    private Integer capacity;
    private Boolean isAvailable;
    private String description;
    private List<String> images;
}