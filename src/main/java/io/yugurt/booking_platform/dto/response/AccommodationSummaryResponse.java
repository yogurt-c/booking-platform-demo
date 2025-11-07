package io.yugurt.booking_platform.dto.response;

import io.yugurt.booking_platform.domain.enums.Amenity;
import io.yugurt.booking_platform.domain.nosql.Accommodation;

import java.util.Set;

public record AccommodationSummaryResponse(
    String id,
    String name,
    String type,
    String address,
    String thumbnailUrl,
    Set<Amenity> amenities
) {
    public static AccommodationSummaryResponse from(Accommodation accommodation) {
        String thumbnail = accommodation.getImageUrls() != null && !accommodation.getImageUrls().isEmpty()
            ? accommodation.getImageUrls().get(0)
            : null;
        
        return new AccommodationSummaryResponse(
            accommodation.getId(),
            accommodation.getName(),
            accommodation.getType(),
            accommodation.getAddress(),
            thumbnail,
            accommodation.getAmenities()
        );
    }
}
