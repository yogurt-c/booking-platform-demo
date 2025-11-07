package io.yugurt.booking_platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationCreateRequest(
        @NotBlank(message = "숙박 업소 ID는 필수입니다")
        String accommodationId,

        @NotBlank(message = "객실 ID는 필수입니다")
        String roomId,

        @NotBlank(message = "예약자명은 필수입니다")
        String guestName,

        @NotBlank(message = "연락처는 필수입니다")
        String guestPhone,

        @NotNull(message = "체크인 날짜는 필수입니다")
        LocalDate checkInDate,

        @NotNull(message = "체크아웃 날짜는 필수입니다")
        LocalDate checkOutDate
) {
}
