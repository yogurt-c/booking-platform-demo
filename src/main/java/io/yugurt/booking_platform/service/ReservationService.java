package io.yugurt.booking_platform.service;

import io.yugurt.booking_platform.domain.enums.ReservationStatus;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.domain.rdb.Reservation;
import io.yugurt.booking_platform.dto.request.ReservationCreateRequest;
import io.yugurt.booking_platform.dto.response.ReservationDetailResponse;
import io.yugurt.booking_platform.dto.response.ReservationResponse;
import io.yugurt.booking_platform.exception.AccommodationNotFoundException;
import io.yugurt.booking_platform.exception.InvalidReservationDateException;
import io.yugurt.booking_platform.exception.PastReservationDateException;
import io.yugurt.booking_platform.exception.ReservationConflictException;
import io.yugurt.booking_platform.exception.ReservationNotFoundException;
import io.yugurt.booking_platform.exception.RoomNotFoundException;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import io.yugurt.booking_platform.repository.rdb.ReservationRepository;
import io.yugurt.booking_platform.util.DateTimeUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final String LOCK_KEY_PREFIX = "reservation:lock:";
    private static final long LOCK_WAIT_TIME_SECONDS = 10L;
    private static final long LOCK_LEASE_TIME_SECONDS = 10L;

    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;
    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    public ReservationResponse createReservation(ReservationCreateRequest request) {
        validateReservationDates(request.checkInDate(), request.checkOutDate());

        String lockKey = LOCK_KEY_PREFIX + request.roomId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(LOCK_WAIT_TIME_SECONDS, LOCK_LEASE_TIME_SECONDS, TimeUnit.SECONDS);
            if (!acquired) {
                throw new ReservationConflictException();
            }

            try {
                return transactionTemplate.execute(status -> {
                    validateNoConflictingReservations(request);
                    Reservation reservation = createReservationEntity(request);
                    reservationRepository.save(reservation);

                    return ReservationResponse.from(reservation);
                });
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ReservationConflictException();
        }
    }

    private void validateNoConflictingReservations(ReservationCreateRequest request) {
        List<Reservation> conflictingReservations = reservationRepository.findConflictingReservations(
            request.roomId(),
            request.checkInDate(),
            request.checkOutDate(),
            ReservationStatus.CANCELLED
        );

        if (!conflictingReservations.isEmpty()) {

            throw new ReservationConflictException();
        }
    }

    private Reservation createReservationEntity(ReservationCreateRequest request) {
        return Reservation.builder()
            .accommodationId(request.accommodationId())
            .roomId(request.roomId())
            .guestName(request.guestName())
            .guestPhone(request.guestPhone())
            .checkInDate(request.checkInDate())
            .checkOutDate(request.checkOutDate())
            .build();
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(ReservationNotFoundException::new);

        Accommodation accommodation = accommodationRepository.findById(reservation.getAccommodationId())
            .orElseThrow(AccommodationNotFoundException::new);

        Room room = roomRepository.findById(reservation.getRoomId())
            .orElseThrow(RoomNotFoundException::new);

        return ReservationDetailResponse.of(reservation, accommodation, room);
    }

    @Transactional(readOnly = true)
    public List<ReservationDetailResponse> getMyReservations(String guestPhone) {
        List<Reservation> reservations = reservationRepository.findByGuestPhone(guestPhone);

        if (reservations.isEmpty()) {
            return List.of();
        }

        Map<String, Accommodation> accommodationMap = fetchAccommodationsMap(reservations);
        Map<String, Room> roomMap = fetchRoomsMap(reservations);

        return reservations.stream()
            .map(reservation -> createDetailResponse(reservation, accommodationMap, roomMap))
            .toList();
    }

    private Map<String, Accommodation> fetchAccommodationsMap(List<Reservation> reservations) {
        List<String> accommodationIds = reservations.stream()
            .map(Reservation::getAccommodationId)
            .distinct()
            .toList();

        return accommodationRepository.findAllById(accommodationIds)
            .stream()
            .collect(Collectors.toMap(Accommodation::getId, a -> a));
    }

    private Map<String, Room> fetchRoomsMap(List<Reservation> reservations) {
        List<String> roomIds = reservations.stream()
            .map(Reservation::getRoomId)
            .distinct()
            .toList();

        return roomRepository.findAllById(roomIds)
            .stream()
            .collect(Collectors.toMap(Room::getId, r -> r));
    }

    private ReservationDetailResponse createDetailResponse(
        Reservation reservation,
        Map<String, Accommodation> accommodationMap,
        Map<String, Room> roomMap
    ) {
        Accommodation accommodation = accommodationMap.get(reservation.getAccommodationId());
        Room room = roomMap.get(reservation.getRoomId());

        return ReservationDetailResponse.of(reservation, accommodation, room);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(ReservationNotFoundException::new);

        LocalDate today = DateTimeUtil.now();
        reservation.cancel(today);

        reservationRepository.save(reservation);
    }

    private void validateReservationDates(LocalDate checkInDate, LocalDate checkOutDate) {
        LocalDate today = DateTimeUtil.now();

        if (checkInDate.isBefore(today)) {

            throw new PastReservationDateException();
        }

        if (!checkOutDate.isAfter(checkInDate)) {

            throw new InvalidReservationDateException();
        }
    }
}
