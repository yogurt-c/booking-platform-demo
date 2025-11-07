package io.yugurt.booking_platform.security;

import io.yugurt.booking_platform.domain.enums.UserRole;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.domain.rdb.Reservation;
import io.yugurt.booking_platform.exception.AccommodationNotFoundException;
import io.yugurt.booking_platform.exception.ErrorCode;
import io.yugurt.booking_platform.exception.ForbiddenException;
import io.yugurt.booking_platform.exception.ReservationNotFoundException;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.repository.rdb.ReservationRepository;
import io.yugurt.booking_platform.security.annotation.RequireOwner;
import io.yugurt.booking_platform.security.annotation.ResourceType;
import java.lang.reflect.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final AccommodationRepository accommodationRepository;
    private final ReservationRepository reservationRepository;

    @Before("@annotation(requireOwner)")
    public void checkOwnership(JoinPoint joinPoint, RequireOwner requireOwner) {
        UserContext userContext = UserContextHolder.getContext();

        if (userContext == null) {

            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        // ADMIN은 모든 리소스 접근 가능
        if (userContext.role() == UserRole.ADMIN) {

            return;
        }

        String resourceId = extractResourceId(joinPoint, requireOwner.resourceIdParam());
        ResourceType resourceType = requireOwner.resourceType();

        validateOwnership(userContext, resourceType, resourceId);
    }

    private String extractResourceId(JoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {

            if (parameters[i].getName().equals(paramName)) {

                Object arg = args[i];

                if (arg instanceof String) {

                    return (String) arg;
                } else if (arg instanceof Long) {

                    return String.valueOf(arg);
                }
            }
        }

        throw new IllegalArgumentException("Resource ID parameter not found: " + paramName);
    }

    private void validateOwnership(UserContext userContext, ResourceType resourceType, String resourceId) {
        switch (resourceType) {
            case ACCOMMODATION -> validateAccommodationOwnership(userContext, resourceId);
            case RESERVATION -> validateReservationOwnership(userContext, resourceId);
        }
    }

    private void validateAccommodationOwnership(UserContext userContext, String accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);

        if (!accommodation.getOwnerId().equals(userContext.userId())) {
            log.warn(
                "User {} attempted to access accommodation {} owned by {}",
                userContext.userId(),
                accommodationId,
                accommodation.getOwnerId()
            );

            throw new ForbiddenException(ErrorCode.NOT_RESOURCE_OWNER);
        }
    }

    private void validateReservationOwnership(UserContext userContext, String reservationId) {
        Reservation reservation = reservationRepository.findById(Long.parseLong(reservationId))
            .orElseThrow(ReservationNotFoundException::new);

        if (!reservation.getGuestId().equals(userContext.userId())) {
            log.warn(
                "User {} attempted to access reservation {} owned by {}",
                userContext.userId(),
                reservationId,
                reservation.getGuestId()
            );

            throw new ForbiddenException(ErrorCode.NOT_RESOURCE_OWNER);
        }
    }
}
