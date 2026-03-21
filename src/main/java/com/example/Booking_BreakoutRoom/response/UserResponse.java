package com.example.Booking_BreakoutRoom.response;

import com.example.Booking_BreakoutRoom.enumeration.EnumRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private EnumRole role;
    private boolean enabled;
}
