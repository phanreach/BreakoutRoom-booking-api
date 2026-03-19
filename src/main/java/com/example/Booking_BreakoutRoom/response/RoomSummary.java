package com.example.Booking_BreakoutRoom.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSummary {
    private Long id;
    private String name;
    private List<RoomImageResponse> images;
}