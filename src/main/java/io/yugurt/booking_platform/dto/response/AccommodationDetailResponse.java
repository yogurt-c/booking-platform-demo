package io.yugurt.booking_platform.dto.response;

import io.yugurt.booking_platform.domain.enums.Amenity;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import java.util.List;
import java.util.Set;

public record AccommodationDetailResponse(
    String id,
    String name,
    String type,
    String address,
    String description,
    List<String> imageUrls,
    Set<Amenity> amenities,
    Double latitude,
    Double longitude
) {

    public static AccommodationDetailResponse from(Accommodation accommodation) {
        return new AccommodationDetailResponse(
            accommodation.getId(),
            accommodation.getName(),
            accommodation.getType(),
            accommodation.getAddress(),
            accommodation.getDescription(),
            accommodation.getImageUrls(),
            accommodation.getAmenities(),
            accommodation.getLatitude(),
            accommodation.getLongitude()
        );
    }
}
