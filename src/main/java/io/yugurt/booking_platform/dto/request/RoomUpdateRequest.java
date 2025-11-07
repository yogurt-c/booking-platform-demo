package io.yugurt.booking_platform.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RoomUpdateRequest(
        @NotBlank(message = "객실 이름은 필수입니다")
        String name,

        @NotBlank(message = "객실 타입은 필수입니다")
        String roomType,

        @NotNull(message = "1박 가격은 필수입니다")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다")
        BigDecimal pricePerNight,

        @NotNull(message = "최대 인원은 필수입니다")
        @Min(value = 1, message = "최대 인원은 1명 이상이어야 합니다")
        Integer maxOccupancy,

        String description
) {
}
