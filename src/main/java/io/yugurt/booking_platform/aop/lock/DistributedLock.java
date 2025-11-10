package io.yugurt.booking_platform.aop.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 락 key (SpEL 표현식 지원)
     * <p>
     * 예: "#request.roomId()"
     * </p>
     */
    String key();

    /**
     * 락 획득 대기 시간 (기본값: 10초)
     */
    long waitTime() default 10L;

    /**
     * 락 임대 시간 (기본값: 10초)
     */
    long leaseTime() default 10L;

    /**
     * 시간 단위 (기본값: SECONDS)
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
