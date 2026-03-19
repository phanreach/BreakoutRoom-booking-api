package com.example.Booking_BreakoutRoom.service;

import com.example.Booking_BreakoutRoom.model.Room;
import com.example.Booking_BreakoutRoom.model.RoomImage;
import com.example.Booking_BreakoutRoom.repository.RoomImageRepository;
import com.example.Booking_BreakoutRoom.repository.RoomRepository;
import com.example.Booking_BreakoutRoom.request.RoomRequest;
import com.example.Booking_BreakoutRoom.response.RoomResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    private final RoomImageRepository roomImageRepository;
    private final RoomRepository roomRepository;
    private final FileStorageService fileStorageService;

    @Value("${app.base-url}")
    private String baseUrl;

    public RoomService(
            RoomRepository roomRepository,
            RoomImageRepository roomImageRepository,
            FileStorageService fileStorageService) {
        this.roomImageRepository = roomImageRepository;
        this.roomRepository = roomRepository;
        this.fileStorageService = fileStorageService;
    }

    public Page<RoomResponse> getRooms(Pageable pageable) {
        return roomRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public RoomResponse getRoomById(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        return mapToResponse(room);
    }

    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        Room room = new Room();
        room.setName(request.getName());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        room.setIsAvailable(request.getIsAvailable());
        room.setDescription(request.getDescription());
        Room savedRoom = roomRepository.save(room);

        return mapToResponse(savedRoom);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setName(request.getName());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        room.setIsAvailable(request.getIsAvailable());
        room.setDescription(request.getDescription());
        Room savedRoom = roomRepository.save(room);

        return mapToResponse(savedRoom);
    }

    @Transactional
    public RoomResponse deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getImages() != null) {
            room.getImages().forEach(img -> fileStorageService.delete(img.getImageUrl()));
        }

        roomRepository.delete(room);

        return mapToResponse(room);
    }

    @Transactional
    public List<RoomImage> uploadRoomImages(Long roomId, List<MultipartFile> images) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        List<RoomImage> uploaded = new ArrayList<>();

        for (MultipartFile file : images) {

            String path = fileStorageService.save(file);

            RoomImage img = new RoomImage();
            img.setImageUrl(path);
            img.setRoom(room);

            roomImageRepository.save(img);
            uploaded.add(img);
        }

        return uploaded;
    }

    @Transactional
    public void deleteRoomImage(Long imageId) {
        RoomImage image = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        fileStorageService.delete(image.getImageUrl());

        if (image.getRoom() != null && image.getRoom().getImages() != null) {
            image.getRoom().getImages().removeIf(img -> img.getId().equals(imageId));
            roomRepository.save(image.getRoom());
        }
    }

    @Transactional
    public List<RoomImage> updateRoomImages(Long roomId, List<MultipartFile> newImages) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getImages() == null) {
            room.setImages(new ArrayList<>());
        }

        if (!room.getImages().isEmpty()) {
            List<RoomImage> existingImages = new ArrayList<>(room.getImages());
            existingImages.forEach(img -> fileStorageService.delete(img.getImageUrl()));
            roomImageRepository.deleteAll(existingImages);
            room.getImages().clear();
        }

        for (MultipartFile file : newImages) {
            String path = fileStorageService.save(file);

            RoomImage img = new RoomImage();
            img.setImageUrl(path);
            img.setRoom(room);

            room.getImages().add(img);
        }

        Room savedRoom = roomRepository.save(room);
        return savedRoom.getImages();
    }

    private RoomResponse mapToResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setName(room.getName());
        response.setFloor(room.getFloor());
        response.setCapacity(room.getCapacity());
        response.setIsAvailable(room.getIsAvailable());
        response.setDescription(room.getDescription());

        List<String> images = new ArrayList<>();
        if (room.getImages() != null) {
            room.getImages().forEach(img -> images.add(baseUrl + img.getImageUrl()));
        }
        response.setImages(images);

        return response;
    }
}