package io.yugurt.booking_platform.dto.response;

import io.yugurt.booking_platform.domain.nosql.Room;

import java.math.BigDecimal;

public record RoomDetailResponse(
        String id,
        String accommodationId,
        String name,
        String roomType,
        BigDecimal pricePerNight,
        Integer maxOccupancy,
        String description
) {
    public static RoomDetailResponse from(Room room) {
        return new RoomDetailResponse(
                room.getId(),
                room.getAccommodationId(),
                room.getName(),
                room.getRoomType(),
                room.getPricePerNight(),
                room.getMaxOccupancy(),
                room.getDescription()
        );
    }
}
