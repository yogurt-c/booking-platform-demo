package io.yugurt.booking_platform.controller;

import io.yugurt.booking_platform.dto.request.ReservationCreateRequest;
import io.yugurt.booking_platform.dto.response.ReservationDetailResponse;
import io.yugurt.booking_platform.dto.response.ReservationResponse;
import io.yugurt.booking_platform.security.UserContext;
import io.yugurt.booking_platform.security.annotation.CurrentUser;
import io.yugurt.booking_platform.service.ReservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ReservationResponse createReservation(@Valid @RequestBody ReservationCreateRequest request,
                                                 @CurrentUser UserContext user) {

        return reservationService.createReservation(user, request);
    }

    @GetMapping("/{id}")
    public ReservationDetailResponse getReservation(@PathVariable Long id) {

        return reservationService.getReservation(id);
    }

    @GetMapping
    public List<ReservationDetailResponse> getMyReservations(@CurrentUser UserContext user) {

        return reservationService.getMyReservations(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReservation(@PathVariable Long id) {

        reservationService.cancelReservation(id);
    }
}
