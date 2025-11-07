package io.yugurt.booking_platform.dto.request;

import io.yugurt.booking_platform.domain.enums.Amenity;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

public record AccommodationCreateRequest(
    @NotBlank(message = "숙박 업소 이름은 필수입니다")
    String name,

    @NotBlank(message = "숙박 업소 타입은 필수입니다")
    String type,

    @NotBlank(message = "주소는 필수입니다")
    String address,

    String description,

    List<String> imageUrls,

    Set<Amenity> amenities,

    Double latitude,

    Double longitude
) {

}
