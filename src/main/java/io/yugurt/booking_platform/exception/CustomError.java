package io.yugurt.booking_platform.exception;

import lombok.Getter;

@Getter
public class CustomError extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomError(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public CustomError(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
