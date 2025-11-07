package io.yugurt.booking_platform.controller;

import io.yugurt.booking_platform.dto.request.ReservationCreateRequest;
import io.yugurt.booking_platform.dto.response.ReservationDetailResponse;
import io.yugurt.booking_platform.dto.response.ReservationResponse;
import io.yugurt.booking_platform.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ReservationResponse createReservation(@Valid @RequestBody ReservationCreateRequest request) {

        return reservationService.createReservation(request);
    }

    @GetMapping("/{id}")
    public ReservationDetailResponse getReservation(@PathVariable Long id) {

        return reservationService.getReservation(id);
    }
}
