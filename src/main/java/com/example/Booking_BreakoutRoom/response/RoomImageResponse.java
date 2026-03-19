package com.example.Booking_BreakoutRoom.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomImageResponse {
    private Long id;
    private String imageUrl;
}