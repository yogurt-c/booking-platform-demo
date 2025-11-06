package io.yugurt.booking_platform.exception;

public class AccommodationNotFoundException extends CustomError {

    public AccommodationNotFoundException() {
        super(ErrorCode.ACCOM_NOT_FOUND);
    }
}
