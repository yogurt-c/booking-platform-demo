package io.yugurt.booking_platform.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.yugurt.booking_platform.config.TestRedisConfig;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.dto.request.ReservationCreateRequest;
import io.yugurt.booking_platform.exception.ReservationConflictException;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import io.yugurt.booking_platform.repository.rdb.ReservationRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestRedisConfig.class)
@ActiveProfiles("test")
class ReservationServiceConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        accommodationRepository.deleteAll();
    }

    @Test
    @DisplayName("동시성 테스트 - 동일 객실/날짜 중복 예약 방지")
    void concurrentReservationTest() throws InterruptedException {
        // Given
        var accommodation = accommodationRepository.save(
            Accommodation.builder()
                .name("호텔 신라")
                .type("호텔")
                .address("서울특별시 중구")
                .build()
        );

        var room = roomRepository.save(
            Room.builder()
                .accommodationId(accommodation.getId())
                .name("디럭스 더블")
                .roomType("디럭스")
                .pricePerNight(new BigDecimal("150000"))
                .maxOccupancy(2)
                .build()
        );

        LocalDate checkInDate = LocalDate.now().plusDays(1);
        LocalDate checkOutDate = LocalDate.now().plusDays(3);

        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        // 5개의 스레드가 동시에 같은 객실/날짜로 예약 시도
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    var request = new ReservationCreateRequest(
                        accommodation.getId(),
                        room.getId(),
                        "게스트" + index,
                        "010-1234-" + String.format("%04d", index),
                        checkInDate,
                        checkOutDate
                    );

                    reservationService.createReservation(request);
                    successCount.incrementAndGet();
                } catch (ReservationConflictException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        // 1개 성공, 나머지는 실패
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(4);
        assertThat(reservationRepository.count()).isEqualTo(1);
    }
}
