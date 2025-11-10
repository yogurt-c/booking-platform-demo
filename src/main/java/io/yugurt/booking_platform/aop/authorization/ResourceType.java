package io.yugurt.booking_platform.aop.authorization;

public enum ResourceType {
    ACCOMMODATION,  // 숙소 소유권 검증 (ownerId)
    RESERVATION     // 예약 소유권 검증 (guestId)
}
