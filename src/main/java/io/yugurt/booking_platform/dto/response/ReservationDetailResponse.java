package io.yugurt.booking_platform.dto.response;

import io.yugurt.booking_platform.domain.enums.ReservationStatus;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.domain.rdb.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationDetailResponse(
    Long id,
    String guestName,
    String guestPhone,
    LocalDate checkInDate,
    LocalDate checkOutDate,
    ReservationStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    AccommodationSummaryResponse accommodation,
    RoomSummaryResponse room
) {

    public static ReservationDetailResponse of(
        Reservation reservation,
        Accommodation accommodation,
        Room room
    ) {

        return new ReservationDetailResponse(
            reservation.getId(),
            reservation.getGuestName(),
            reservation.getGuestPhone(),
            reservation.getCheckInDate(),
            reservation.getCheckOutDate(),
            reservation.getStatus(),
            reservation.getCreatedAt(),
            reservation.getUpdatedAt(),
            AccommodationSummaryResponse.from(accommodation),
            RoomSummaryResponse.from(room)
        );
    }

    public record RoomSummaryResponse(
        String id,
        String name,
        String roomType,
        Integer maxOccupancy
    ) {

        public static RoomSummaryResponse from(Room room) {

            return new RoomSummaryResponse(
                room.getId(),
                room.getName(),
                room.getRoomType(),
                room.getMaxOccupancy()
            );
        }
    }
}
