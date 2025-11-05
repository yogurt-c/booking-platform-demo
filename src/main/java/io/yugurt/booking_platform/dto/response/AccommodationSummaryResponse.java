package io.yugurt.booking_platform.dto.response;

import io.yugurt.booking_platform.domain.nosql.Accommodation;

public record AccommodationSummaryResponse(
        String id,
        String name,
        String type,
        String address,
        String thumbnailUrl
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
                thumbnail
        );
    }
}
