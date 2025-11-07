package io.yugurt.booking_platform.exception;

public class ReservationConflictException extends CustomError {

    public ReservationConflictException() {
        super(ErrorCode.RESERVATION_CONFLICT);
    }
}
