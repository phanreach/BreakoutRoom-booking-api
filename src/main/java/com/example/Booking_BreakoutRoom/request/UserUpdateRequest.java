package com.example.Booking_BreakoutRoom.request;

import com.example.Booking_BreakoutRoom.enumeration.EnumRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String password;

    private EnumRole role = EnumRole.USER;

    private Boolean enabled;
}
