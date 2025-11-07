package io.yugurt.booking_platform.controller;

import io.yugurt.booking_platform.dto.request.AccommodationCreateRequest;
import io.yugurt.booking_platform.dto.request.AccommodationUpdateRequest;
import io.yugurt.booking_platform.dto.request.CursorPageRequest;
import io.yugurt.booking_platform.dto.response.AccommodationDetailResponse;
import io.yugurt.booking_platform.dto.response.AccommodationSummaryResponse;
import io.yugurt.booking_platform.dto.response.CursorPageResponse;
import io.yugurt.booking_platform.dto.response.RoomDetailResponse;
import io.yugurt.booking_platform.service.AccommodationService;
import io.yugurt.booking_platform.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final RoomService roomService;

    @PostMapping
    public AccommodationDetailResponse createAccommodation(@Valid @RequestBody AccommodationCreateRequest request) {

        return accommodationService.createAccommodation(request);
    }

    @GetMapping("/{id}")
    public AccommodationDetailResponse getAccommodation(@PathVariable String id) {

        return accommodationService.getAccommodation(id);
    }

    @GetMapping
    public CursorPageResponse<AccommodationSummaryResponse> getAccommodations(@ModelAttribute @Valid CursorPageRequest request) {

        return accommodationService.getAccommodations(request);
    }

    @PutMapping("/{id}")
    public AccommodationDetailResponse updateAccommodation(
            @PathVariable String id,
            @Valid @RequestBody AccommodationUpdateRequest request) {

        return accommodationService.updateAccommodation(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccommodation(@PathVariable String id) {

        accommodationService.deleteAccommodation(id);
    }

    @GetMapping("/{accommodationId}/rooms")
    public List<RoomDetailResponse> getRoomsByAccommodationId(@PathVariable String accommodationId) {

        return roomService.getRoomsByAccommodationId(accommodationId);
    }
}
