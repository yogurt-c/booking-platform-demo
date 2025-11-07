package io.yugurt.booking_platform.dto.response;

import io.yugurt.booking_platform.domain.enums.ReservationStatus;
import io.yugurt.booking_platform.domain.rdb.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        String accommodationId,
        String roomId,
        String guestName,
        String guestPhone,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        ReservationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getAccommodationId(),
                reservation.getRoomId(),
                reservation.getGuestName(),
                reservation.getGuestPhone(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
