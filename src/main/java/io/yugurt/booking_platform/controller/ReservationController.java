package io.yugurt.booking_platform.controller;

import io.yugurt.booking_platform.dto.request.ReservationCreateRequest;
import io.yugurt.booking_platform.dto.response.ReservationDetailResponse;
import io.yugurt.booking_platform.dto.response.ReservationResponse;
import io.yugurt.booking_platform.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public List<ReservationDetailResponse> getMyReservations(@RequestParam String guestPhone) {

        return reservationService.getMyReservations(guestPhone);
    }
}
