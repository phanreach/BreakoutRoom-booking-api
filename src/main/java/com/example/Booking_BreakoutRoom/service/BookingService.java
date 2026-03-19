package com.example.Booking_BreakoutRoom.service;

import com.example.Booking_BreakoutRoom.model.Booking;
import com.example.Booking_BreakoutRoom.model.Room;
import com.example.Booking_BreakoutRoom.model.User;
import com.example.Booking_BreakoutRoom.repository.BookingRepository;
import com.example.Booking_BreakoutRoom.repository.RoomRepository;
import com.example.Booking_BreakoutRoom.repository.UserRepository;
import com.example.Booking_BreakoutRoom.request.BookingRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    private void validateWorkingHours(BookingRequest request) {
        DayOfWeek day = request.getDate().getDayOfWeek();
        LocalTime start = request.getStartTime();
        LocalTime end = request.getEndTime();

        if (day == DayOfWeek.SUNDAY) {
            throw new RuntimeException("Bookings are not allowed on Sunday");
        }

        if (day != DayOfWeek.SATURDAY) {
            LocalTime open = LocalTime.of(8, 0);
            LocalTime close = LocalTime.of(17, 0);

            if (start.isBefore(open) || end.isAfter(close)) {
                throw new RuntimeException(
                        "Bookings must be between 08:00 and 17:00 (Mon-Fri)"
                );
            }
        }

        if (day == DayOfWeek.SATURDAY) {
            LocalTime open = LocalTime.of(8, 0);
            LocalTime close = LocalTime.of(12, 0);

            if (start.isBefore(open) || end.isAfter(close)) {
                throw new RuntimeException(
                        "Bookings must be between 08:00 and 12:00 (Saturday)"
                );
            }
        }
    }
    public Booking createBooking(BookingRequest request) {

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // ✅ Determine who is booking
        User user = getAuthenticatedUser();

        // ✅ Validate time
        if (request.getStartTime().isAfter(request.getEndTime()) ||
                request.getStartTime().equals(request.getEndTime())) {
            throw new RuntimeException("Invalid time range");
        }

        validateWorkingHours(request);
        // ✅ Check conflict
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                request.getRoomId(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for this time");
        }

        // ✅ Optional: capacity check
        if (request.getParticipants() > room.getCapacity()) {
            throw new RuntimeException("Room capacity exceeded");
        }

        // ✅ Create booking
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setParticipants(request.getParticipants());
        booking.setNotes(request.getNotes());
        booking.setUser(user);
        user.getBookings().add(booking);

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByRoom(Long roomId) {
        return bookingRepository.findAllByRoomId(roomId);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Unauthenticated");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ UPDATE BOOKING
    public Booking updateBooking(Long id, BookingRequest request) {

        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // ✅ Ensure booking has an owner when updating (fix legacy records)
        if (existing.getUser() == null) {
            existing.setUser(getAuthenticatedUser());
        }

        // ✅ Validate time
        if (request.getStartTime().isAfter(request.getEndTime()) ||
                request.getStartTime().equals(request.getEndTime())) {
            throw new RuntimeException("Invalid time range");
        }

        validateWorkingHours(request);
        // ✅ Check conflict (exclude current booking)
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                request.getRoomId(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()).stream()
                .filter(b -> !b.getId().equals(id)) // exclude itself
                .toList();

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Room is already booked for this time");
        }

        // ✅ Capacity check
        if (request.getParticipants() != null &&
                request.getParticipants() > room.getCapacity()) {
            throw new RuntimeException("Room capacity exceeded");
        }

        // ✅ Update fields
        existing.setRoom(room);
        existing.setDate(request.getDate());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setParticipants(request.getParticipants());
        existing.setNotes(request.getNotes());

        return bookingRepository.save(existing);
    }

    // ✅ DELETE BOOKING
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        bookingRepository.delete(booking);
    }
}