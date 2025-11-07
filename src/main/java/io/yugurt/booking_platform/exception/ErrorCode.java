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
    RESERVATION_CONFLICT("예약 날짜가 겹칩니다.", HttpStatus.CONFLICT),
    INVALID_RESERVATION_DATE("체크아웃 날짜는 체크인 날짜보다 이후여야 합니다.", HttpStatus.BAD_REQUEST),
    PAST_RESERVATION_DATE("과거 날짜로 예약할 수 없습니다.", HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_RESERVATION("체크인 1일 전까지만 취소 가능합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_CANCELLED_RESERVATION("이미 취소된 예약입니다.", HttpStatus.BAD_REQUEST);

    private final String description;
    private final HttpStatus status;
}
