package com.example.Booking_BreakoutRoom.service;

import com.example.Booking_BreakoutRoom.enumeration.EnumRole;
import com.example.Booking_BreakoutRoom.model.User;
import com.example.Booking_BreakoutRoom.repository.UserRepository;
import com.example.Booking_BreakoutRoom.request.UserCreateRequest;
import com.example.Booking_BreakoutRoom.request.UserUpdateRequest;
import com.example.Booking_BreakoutRoom.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return toResponse(getUserEntity(id));
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : EnumRole.USER);
        user.setEnabled(request.isEnabled());

        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = getUserEntity(id);

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new RuntimeException("Email already taken");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole() != null ? request.getRole() : EnumRole.USER);

        if (request.getEnabled() != null) {
            validateSelfManagement(user, request.getEnabled());
            user.setEnabled(request.getEnabled());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUserStatus(Long id, boolean enabled) {
        User user = getUserEntity(id);
        validateSelfManagement(user, enabled);
        user.setEnabled(enabled);
        return toResponse(userRepository.save(user));
    }

    public UserResponse deleteUser(Long id) {
        User user = getUserEntity(id);

        if (isCurrentUser(user)) {
            throw new RuntimeException("You cannot delete your own account");
        }

        if (!user.getBookings().isEmpty()) {
            throw new RuntimeException("Cannot delete user with booking history. Disable the user instead.");
        }

        UserResponse response = toResponse(user);
        userRepository.delete(user);
        return response;
    }

    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }

    private void validateSelfManagement(User user, boolean enabled) {
        if (!enabled && isCurrentUser(user)) {
            throw new RuntimeException("You cannot disable your own account");
        }
    }

    private boolean isCurrentUser(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getName().equalsIgnoreCase(user.getEmail());
    }
}
