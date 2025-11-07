package io.yugurt.booking_platform.exception;

public class InvalidReservationDateException extends CustomError {

    public InvalidReservationDateException() {
        super(ErrorCode.INVALID_RESERVATION_DATE);
    }
}
