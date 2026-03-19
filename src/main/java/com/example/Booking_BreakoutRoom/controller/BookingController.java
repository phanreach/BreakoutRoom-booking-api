package com.example.Booking_BreakoutRoom.controller;

import com.example.Booking_BreakoutRoom.model.Booking;
import com.example.Booking_BreakoutRoom.request.BookingRequest;
import com.example.Booking_BreakoutRoom.response.ApiResponse;
import com.example.Booking_BreakoutRoom.response.BookingResponse;
import com.example.Booking_BreakoutRoom.response.RoomImageResponse;
import com.example.Booking_BreakoutRoom.response.RoomSummary;
import com.example.Booking_BreakoutRoom.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {

        private final BookingService bookingService;

        @Value("${app.base-url}")
        private String baseUrl;

        // ✅ CREATE BOOKING
        @PostMapping
        public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
                        @RequestBody BookingRequest request) {
                Booking booking = bookingService.createBooking(request);

                BookingResponse response = mapToResponse(booking);

                return ResponseEntity.ok(
                                new ApiResponse<>("Booking created successfully", response));
        }

        // ✅ GET ALL BOOKINGS
        @GetMapping
        public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
                List<BookingResponse> responses = bookingService.getAllBookings()
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(
                                new ApiResponse<>("Fetched all bookings", responses));
        }

        // ✅ GET BY ID (FIXED)
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
                        @PathVariable Long id) {
                Booking booking = bookingService.getBookingById(id);

                return ResponseEntity.ok(
                                new ApiResponse<>("Fetched booking successfully", mapToResponse(booking)));
        }

        // ✅ GET BOOKINGS BY ROOM
        @GetMapping("/room/{roomId}")
        public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingsByRoom(
                        @PathVariable Long roomId) {
                List<BookingResponse> responses = bookingService.getBookingsByRoom(roomId)
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(
                                new ApiResponse<>("Fetched bookings by room", responses));
        }

        // ✅ MAPPER (IMPORTANT)
        private BookingResponse mapToResponse(Booking booking) {

                List<RoomImageResponse> images = booking.getRoom().getImages()
                                .stream()
                                .map(img -> new RoomImageResponse(
                                                img.getId(),
                                                baseUrl + img.getImageUrl()))
                                .toList();
                return new BookingResponse(
                                booking.getId(),
                                booking.getUser() != null ? booking.getUser().getId() : null,
                                booking.getUser() != null ? booking.getUser().getFullName() : null,
                                new RoomSummary(booking.getRoom().getId(), booking.getRoom().getName(), images),
                                booking.getDate(),
                                booking.getStartTime(),
                                booking.getEndTime(),
                                booking.getParticipants(),
                                booking.getNotes());
        }

        // ✅ UPDATE BOOKING
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<BookingResponse>> updateBooking(
                        @PathVariable Long id,
                        @RequestBody BookingRequest request) {
                Booking updated = bookingService.updateBooking(id, request);

                return ResponseEntity.ok(
                                new ApiResponse<>("Booking updated successfully", mapToResponse(updated)));
        }

        // ✅ DELETE BOOKING
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Object>> deleteBooking(
                        @PathVariable Long id) {
                bookingService.deleteBooking(id);

                return ResponseEntity.ok(
                                new ApiResponse<>("Booking deleted successfully", null));
        }

}