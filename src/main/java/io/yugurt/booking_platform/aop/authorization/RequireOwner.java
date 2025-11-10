package io.yugurt.booking_platform.aop.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireOwner {

    ResourceType resourceType();

    String resourceIdParam() default "id"; // 메서드 파라미터 이름
}
