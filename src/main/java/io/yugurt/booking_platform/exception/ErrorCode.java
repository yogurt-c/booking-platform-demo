package io.yugurt.booking_platform.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ACCOM_NOT_FOUND("해당 숙박 업소를 찾을 수 없습니다.");

    private final String description;
}
