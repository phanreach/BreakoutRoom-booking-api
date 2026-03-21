package com.example.Booking_BreakoutRoom.auth;

import com.example.Booking_BreakoutRoom.auth.dto.AuthResponse;
import com.example.Booking_BreakoutRoom.auth.dto.LoginRequest;
import com.example.Booking_BreakoutRoom.auth.dto.RefreshTokenRequest;
import com.example.Booking_BreakoutRoom.auth.dto.RegisterRequest;
import com.example.Booking_BreakoutRoom.enumeration.EnumRole;
import com.example.Booking_BreakoutRoom.model.User;
import com.example.Booking_BreakoutRoom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(EnumRole.USER);
        user.setEnabled(true);

        userRepository.save(user);

        return generateAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        return generateAuthResponse(user);

    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh token is required");
        }

        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        if (!jwtService.isTokenValid(refreshToken, user.getEmail())) {
            throw new RuntimeException("Refresh token is not valid");
        }

        return generateAuthResponse(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .token(accessToken)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
