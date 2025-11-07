package io.yugurt.booking_platform.controller;

import io.yugurt.booking_platform.dto.request.RoomCreateRequest;
import io.yugurt.booking_platform.dto.request.RoomUpdateRequest;
import io.yugurt.booking_platform.dto.response.RoomDetailResponse;
import io.yugurt.booking_platform.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public RoomDetailResponse createRoom(@Valid @RequestBody RoomCreateRequest request) {

        return roomService.createRoom(request);
    }

    @GetMapping("/{id}")
    public RoomDetailResponse getRoom(@PathVariable String id) {

        return roomService.getRoom(id);
    }

    @PutMapping("/{id}")
    public RoomDetailResponse updateRoom(
        @PathVariable String id,
        @Valid @RequestBody RoomUpdateRequest request) {

        return roomService.updateRoom(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(@PathVariable String id) {

        roomService.deleteRoom(id);
    }
}
