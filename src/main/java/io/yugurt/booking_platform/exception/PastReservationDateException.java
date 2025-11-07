package io.yugurt.booking_platform.exception;

public class PastReservationDateException extends CustomError {

    public PastReservationDateException() {
        super(ErrorCode.PAST_RESERVATION_DATE);
    }
}
