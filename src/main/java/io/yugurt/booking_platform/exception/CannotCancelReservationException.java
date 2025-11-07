package io.yugurt.booking_platform.exception;

public class CannotCancelReservationException extends CustomError {

    public CannotCancelReservationException() {
        super(ErrorCode.CANNOT_CANCEL_RESERVATION);
    }
}
