package io.yugurt.booking_platform.service;

import io.yugurt.booking_platform.aop.authorization.RequireOwner;
import io.yugurt.booking_platform.aop.authorization.ResourceType;
import io.yugurt.booking_platform.aop.lock.DistributedLock;
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
import io.yugurt.booking_platform.security.UserContext;
import io.yugurt.booking_platform.util.DateTimeUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;

    @DistributedLock(key = "#request.roomId()")
    public ReservationResponse createReservation(UserContext user, ReservationCreateRequest request) {
        validateReservationDates(request.checkInDate(), request.checkOutDate());

        validateNoConflictingReservations(request);
        Reservation reservation = createReservationEntity(user, request);
        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
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

    private Reservation createReservationEntity(UserContext user, ReservationCreateRequest request) {
        // 현재 사용자를 게스트로 설정
        String guestId = user.userId();

        return Reservation.builder()
            .accommodationId(request.accommodationId())
            .roomId(request.roomId())
            .guestId(guestId)
            .guestName(request.guestName())
            .guestPhone(request.guestPhone())
            .checkInDate(request.checkInDate())
            .checkOutDate(request.checkOutDate())
            .build();
    }

    @Transactional(readOnly = true)
    @RequireOwner(resourceType = ResourceType.RESERVATION)
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
    public List<ReservationDetailResponse> getMyReservations(UserContext user) {
        List<Reservation> reservations = reservationRepository.findByGuestId(user.userId());

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
    @RequireOwner(resourceType = ResourceType.RESERVATION)
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
