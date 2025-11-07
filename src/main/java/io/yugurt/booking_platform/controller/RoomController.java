package io.yugurt.booking_platform.controller;

import io.yugurt.booking_platform.dto.request.RoomCreateRequest;
import io.yugurt.booking_platform.dto.response.RoomDetailResponse;
import io.yugurt.booking_platform.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
