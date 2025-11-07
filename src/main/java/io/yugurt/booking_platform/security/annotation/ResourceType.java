package io.yugurt.booking_platform.security.annotation;

public enum ResourceType {
    ACCOMMODATION,  // 숙소 소유권 검증 (ownerId)
    RESERVATION     // 예약 소유권 검증 (guestId)
}
