package io.yugurt.booking_platform.controller;

import io.yugurt.booking_platform.dto.request.AccommodationCreateRequest;
import io.yugurt.booking_platform.dto.response.AccommodationDetailResponse;
import io.yugurt.booking_platform.service.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accommodations")
@RequiredArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    @PostMapping
    public AccommodationDetailResponse createAccommodation(@Valid @RequestBody AccommodationCreateRequest request) {

        return accommodationService.createAccommodation(request);
    }
}
