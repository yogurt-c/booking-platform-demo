package io.yugurt.booking_platform.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ACCOM_NOT_FOUND("해당 숙박 업소를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ROOM_NOT_FOUND("해당 객실을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RESERVATION_NOT_FOUND("해당 예약을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    RESERVATION_CONFLICT("예약 날짜가 겹칩니다.", HttpStatus.CONFLICT);

    private final String description;
    private final HttpStatus status;
}
