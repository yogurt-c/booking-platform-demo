package io.yugurt.booking_platform.security;

import io.yugurt.booking_platform.domain.enums.UserRole;

public record UserContext(
    String userId,
    UserRole role
) {
}
