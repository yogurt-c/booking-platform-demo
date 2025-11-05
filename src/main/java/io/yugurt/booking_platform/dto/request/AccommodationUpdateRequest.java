package io.yugurt.booking_platform.dto.request;

import io.yugurt.booking_platform.domain.enums.Amenity;

import java.util.List;
import java.util.Set;

public record AccommodationUpdateRequest(
        String name,

        String type,

        String address,

        String description,

        List<String> imageUrls,

        Set<Amenity> amenities,

        Double latitude,

        Double longitude
) {
}
