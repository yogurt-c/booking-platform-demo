package io.yugurt.booking_platform.exception;

public class AlreadyCancelledReservationException extends CustomError {

    public AlreadyCancelledReservationException() {
        super(ErrorCode.ALREADY_CANCELLED_RESERVATION);
    }
}
