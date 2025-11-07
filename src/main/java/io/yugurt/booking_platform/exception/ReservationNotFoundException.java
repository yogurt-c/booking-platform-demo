package io.yugurt.booking_platform.exception;

public class ReservationNotFoundException extends CustomError {

    public ReservationNotFoundException() {
        super(ErrorCode.RESERVATION_NOT_FOUND);
    }
}
