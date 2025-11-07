package io.yugurt.booking_platform.util;

import java.time.LocalDate;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtil {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    public static LocalDate now() {
        return LocalDate.now(ZONE_ID);
    }

}
