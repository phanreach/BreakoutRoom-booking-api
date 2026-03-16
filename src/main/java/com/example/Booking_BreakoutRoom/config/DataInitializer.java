package com.example.Booking_BreakoutRoom.config;

import com.example.Booking_BreakoutRoom.enumeration.EnumRole;
import com.example.Booking_BreakoutRoom.model.User;
import com.example.Booking_BreakoutRoom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByEmail("admin@camtech.edu.kh")) {
                User admin = new User();
                admin.setFullName("admin");
                admin.setEmail("admin@camtech.edu.kh");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setPhone("010203040");
                admin.setRole(EnumRole.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
