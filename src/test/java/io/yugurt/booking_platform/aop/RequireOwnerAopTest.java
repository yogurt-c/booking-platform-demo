package io.yugurt.booking_platform.aop;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.yugurt.booking_platform.config.MockRedisConfig;
import io.yugurt.booking_platform.domain.enums.UserRole;
import io.yugurt.booking_platform.domain.rdb.Reservation;
import io.yugurt.booking_platform.exception.ForbiddenException;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import io.yugurt.booking_platform.repository.rdb.ReservationRepository;
import io.yugurt.booking_platform.security.UserContext;
import io.yugurt.booking_platform.security.UserContextHolder;
import io.yugurt.booking_platform.service.ReservationService;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(MockRedisConfig.class)
class RequireOwnerAopTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RoomRepository roomRepository;

    private static final String OWNER_ID = "GUEST-001";
    private static final String OTHER_ID = "GUEST-002";

    private Long reservationId;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        accommodationRepository.deleteAll();

        Reservation reservation = reservationRepository.save(
            Reservation.builder()
                .accommodationId("acc-1")
                .roomId("room-1")
                .guestId(OWNER_ID)
                .guestName("홍길동")
                .guestPhone("010-1234-5678")
                .checkInDate(LocalDate.now().plusDays(5))
                .checkOutDate(LocalDate.now().plusDays(7))
                .build()
        );
        reservationId = reservation.getId();
    }

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    @DisplayName("예약 소유자가 취소 요청 시 정상 처리")
    void cancelReservation_byOwner_succeeds() {
        UserContextHolder.setContext(new UserContext(OWNER_ID, UserRole.GUEST));

        assertThatCode(() -> reservationService.cancelReservation(reservationId))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("예약 소유자가 아닌 사용자가 취소 요청 시 ForbiddenException 발생")
    void cancelReservation_byNonOwner_throwsForbiddenException() {
        UserContextHolder.setContext(new UserContext(OTHER_ID, UserRole.GUEST));

        assertThatThrownBy(() -> reservationService.cancelReservation(reservationId))
            .isInstanceOf(ForbiddenException.class);
    }
}
