package io.yugurt.booking_platform.controller;

import io.yugurt.booking_platform.dto.request.RoomCreateRequest;
import io.yugurt.booking_platform.dto.request.RoomUpdateRequest;
import io.yugurt.booking_platform.dto.response.RoomDetailResponse;
import io.yugurt.booking_platform.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
