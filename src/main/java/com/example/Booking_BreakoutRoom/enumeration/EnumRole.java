package com.example.Booking_BreakoutRoom.enumeration;

public enum EnumRole {
    USER, ADMIN;
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
