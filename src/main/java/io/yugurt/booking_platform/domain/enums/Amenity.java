package io.yugurt.booking_platform.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Amenity {
    WIFI("무선 인터넷"),
    PARKING("주차장"),
    BREAKFAST("조식 제공"),
    POOL("수영장"),
    GYM("피트니스 센터"),
    RESTAURANT("레스토랑"),
    ROOM_SERVICE("룸서비스"),
    SPA("스파"),
    BAR("바"),
    BUSINESS_CENTER("비즈니스 센터"),
    PET_FRIENDLY("반려동물 동반 가능"),
    AIR_CONDITIONING("에어컨"),
    LAUNDRY("세탁 서비스");

    private final String description;
}
