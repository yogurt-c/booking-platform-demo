package io.yugurt.booking_platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import io.yugurt.booking_platform.domain.enums.ReservationStatus;
import io.yugurt.booking_platform.domain.enums.UserRole;
import io.yugurt.booking_platform.domain.rdb.Reservation;
import io.yugurt.booking_platform.dto.request.ReservationCreateRequest;
import io.yugurt.booking_platform.dto.response.ReservationResponse;
import io.yugurt.booking_platform.exception.CannotCancelReservationException;
import io.yugurt.booking_platform.exception.InvalidReservationDateException;
import io.yugurt.booking_platform.exception.PastReservationDateException;
import io.yugurt.booking_platform.exception.ReservationConflictException;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import io.yugurt.booking_platform.repository.rdb.ReservationRepository;
import io.yugurt.booking_platform.security.UserContext;
import io.yugurt.booking_platform.util.DateTimeUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private RoomRepository roomRepository;

    private static final LocalDate FIXED_TODAY = LocalDate.of(2024, 6, 1);
    private final UserContext guestContext = new UserContext("GUEST-001", UserRole.GUEST);

    @Test
    @DisplayName("체크인 날짜가 과거이면 PastReservationDateException 발생")
    void createReservation_pastCheckIn_throwsPastReservationDateException() {
        try (MockedStatic<DateTimeUtil> mock = mockStatic(DateTimeUtil.class)) {
            mock.when(DateTimeUtil::now).thenReturn(FIXED_TODAY);

            var request = new ReservationCreateRequest(
                "acc-1", "room-1", "홍길동", "010-1234-5678",
                FIXED_TODAY.minusDays(1),  // 어제
                FIXED_TODAY.plusDays(1)
            );

            assertThatThrownBy(() -> reservationService.createReservation(guestContext, request))
                .isInstanceOf(PastReservationDateException.class);
        }
    }

    @Test
    @DisplayName("체크인 날짜가 체크아웃 날짜와 같거나 이후이면 InvalidReservationDateException 발생")
    void createReservation_checkInNotBeforeCheckOut_throwsInvalidReservationDateException() {
        try (MockedStatic<DateTimeUtil> mock = mockStatic(DateTimeUtil.class)) {
            mock.when(DateTimeUtil::now).thenReturn(FIXED_TODAY);

            var request = new ReservationCreateRequest(
                "acc-1", "room-1", "홍길동", "010-1234-5678",
                FIXED_TODAY.plusDays(3),  // 체크인
                FIXED_TODAY.plusDays(1)   // 체크아웃 < 체크인
            );

            assertThatThrownBy(() -> reservationService.createReservation(guestContext, request))
                .isInstanceOf(InvalidReservationDateException.class);
        }
    }

    @Test
    @DisplayName("이미 예약된 날짜에 중복 예약 시 ReservationConflictException 발생")
    void createReservation_conflictingDates_throwsReservationConflictException() {
        try (MockedStatic<DateTimeUtil> mock = mockStatic(DateTimeUtil.class)) {
            mock.when(DateTimeUtil::now).thenReturn(FIXED_TODAY);

            var request = new ReservationCreateRequest(
                "acc-1", "room-1", "홍길동", "010-1234-5678",
                FIXED_TODAY.plusDays(1),
                FIXED_TODAY.plusDays(3)
            );

            when(reservationRepository.findConflictingReservations(any(), any(), any(), any()))
                .thenReturn(List.of(Reservation.builder().build()));

            assertThatThrownBy(() -> reservationService.createReservation(guestContext, request))
                .isInstanceOf(ReservationConflictException.class);
        }
    }

    @Test
    @DisplayName("정상 예약 생성 시 예약 정보 반환")
    void createReservation_validRequest_returnsReservationResponse() {
        try (MockedStatic<DateTimeUtil> mock = mockStatic(DateTimeUtil.class)) {
            mock.when(DateTimeUtil::now).thenReturn(FIXED_TODAY);

            var request = new ReservationCreateRequest(
                "acc-1", "room-1", "홍길동", "010-1234-5678",
                FIXED_TODAY.plusDays(1),
                FIXED_TODAY.plusDays(3)
            );

            when(reservationRepository.findConflictingReservations(any(), any(), any(), any()))
                .thenReturn(List.of());

            Reservation saved = Reservation.builder()
                .accommodationId("acc-1")
                .roomId("room-1")
                .guestId("GUEST-001")
                .guestName("홍길동")
                .guestPhone("010-1234-5678")
                .checkInDate(FIXED_TODAY.plusDays(1))
                .checkOutDate(FIXED_TODAY.plusDays(3))
                .build();
            when(reservationRepository.save(any())).thenReturn(saved);

            ReservationResponse response = reservationService.createReservation(guestContext, request);

            assertThat(response).isNotNull();
            assertThat(response.guestName()).isEqualTo("홍길동");
            assertThat(response.accommodationId()).isEqualTo("acc-1");
            assertThat(response.roomId()).isEqualTo("room-1");
            assertThat(response.status()).isEqualTo(ReservationStatus.PENDING);
        }
    }

    @Test
    @DisplayName("체크인 당일 취소 요청 시 CannotCancelReservationException 발생")
    void cancelReservation_afterDeadline_throwsCannotCancelReservationException() {
        try (MockedStatic<DateTimeUtil> mock = mockStatic(DateTimeUtil.class)) {
            mock.when(DateTimeUtil::now).thenReturn(FIXED_TODAY);

            // 체크인이 오늘 → 취소 마감(전날)이 어제 → 오늘은 마감 이후
            Reservation reservation = Reservation.builder()
                .accommodationId("acc-1")
                .roomId("room-1")
                .guestId("GUEST-001")
                .guestName("홍길동")
                .guestPhone("010-1234-5678")
                .checkInDate(FIXED_TODAY)
                .checkOutDate(FIXED_TODAY.plusDays(2))
                .build();

            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

            assertThatThrownBy(() -> reservationService.cancelReservation(1L))
                .isInstanceOf(CannotCancelReservationException.class);
        }
    }
}
