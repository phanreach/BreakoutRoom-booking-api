package com.example.Booking_BreakoutRoom.controller;

import com.example.Booking_BreakoutRoom.model.RoomImage;
import com.example.Booking_BreakoutRoom.request.RoomRequest;
import com.example.Booking_BreakoutRoom.response.ApiResponse;
import com.example.Booking_BreakoutRoom.response.RoomResponse;
import com.example.Booking_BreakoutRoom.service.RoomService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public Page<RoomResponse> getRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return roomService.getRooms(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long id) {
        RoomResponse room = roomService.getRoomById(id);
        return ResponseEntity.ok(new ApiResponse<>("Room fetched successfully", room));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@RequestBody RoomRequest request) {
        RoomResponse room = roomService.createRoom(request);
        return ResponseEntity.ok(new ApiResponse<>("Room created successfully", room));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long id,
            @RequestBody RoomRequest request
    ) {
        RoomResponse room = roomService.updateRoom(id, request);
        return ResponseEntity.ok(new ApiResponse<>("Room updated successfully", room));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> deleteRoom(@PathVariable Long id) {
        RoomResponse room = roomService.deleteRoom(id);
        return ResponseEntity.ok(new ApiResponse<>("Room deleted successfully", room));
    }

    @PostMapping(
            value = "/upload-images/{roomId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<String>>> uploadImages(
            @PathVariable Long roomId,
            @RequestPart("images") List<MultipartFile> images
    ) {
        List<String> uploadedUrls = roomService.uploadRoomImages(roomId, images)
                .stream().map(img -> img.getImageUrl()).toList();

        return ResponseEntity.ok(new ApiResponse<>("Images uploaded successfully", uploadedUrls));
    }
}