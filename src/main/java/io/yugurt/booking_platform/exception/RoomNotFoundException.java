package io.yugurt.booking_platform.exception;

public class RoomNotFoundException extends CustomError {

    public RoomNotFoundException() {
        super(ErrorCode.ROOM_NOT_FOUND);
    }
}
