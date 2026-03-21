package com.example.Booking_BreakoutRoom.controller;

import com.example.Booking_BreakoutRoom.request.UserCreateRequest;
import com.example.Booking_BreakoutRoom.request.UserStatusRequest;
import com.example.Booking_BreakoutRoom.request.UserUpdateRequest;
import com.example.Booking_BreakoutRoom.response.ApiResponse;
import com.example.Booking_BreakoutRoom.response.UserResponse;
import com.example.Booking_BreakoutRoom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers() {
        return ResponseEntity.ok(new ApiResponse<>("Users fetched successfully", userService.getUsers()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by id")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("User fetched successfully", userService.getUserById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("User created successfully", userService.createUser(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(new ApiResponse<>("User updated successfully", userService.updateUser(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable or disable user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusRequest request
    ) {
        String message = request.getEnabled() ? "User enabled successfully" : "User disabled successfully";
        return ResponseEntity.ok(new ApiResponse<>(message, userService.updateUserStatus(id, request.getEnabled())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("User deleted successfully", userService.deleteUser(id)));
    }
}
