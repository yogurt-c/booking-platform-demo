package io.yugurt.booking_platform.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends CustomError {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }

}
