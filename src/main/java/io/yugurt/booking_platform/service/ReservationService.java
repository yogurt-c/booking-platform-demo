package io.yugurt.booking_platform.service;

import io.yugurt.booking_platform.domain.enums.ReservationStatus;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.domain.rdb.Reservation;
import io.yugurt.booking_platform.dto.request.ReservationCreateRequest;
import io.yugurt.booking_platform.dto.response.ReservationDetailResponse;
import io.yugurt.booking_platform.dto.response.ReservationResponse;
import io.yugurt.booking_platform.exception.AccommodationNotFoundException;
import io.yugurt.booking_platform.exception.ReservationConflictException;
import io.yugurt.booking_platform.exception.ReservationNotFoundException;
import io.yugurt.booking_platform.exception.RoomNotFoundException;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import io.yugurt.booking_platform.repository.rdb.ReservationRepository;
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

    @Transactional
    public ReservationResponse createReservation(ReservationCreateRequest request) {
        // 겹치는 예약 검증
        List<Reservation> conflictingReservations = reservationRepository.findConflictingReservations(
            request.roomId(),
            request.checkInDate(),
            request.checkOutDate(),
            ReservationStatus.CANCELLED
        );

        if (!conflictingReservations.isEmpty()) {

            throw new ReservationConflictException();
        }

        // 예약 등록
        Reservation reservation = Reservation.builder()
            .accommodationId(request.accommodationId())
            .roomId(request.roomId())
            .guestName(request.guestName())
            .guestPhone(request.guestPhone())
            .checkInDate(request.checkInDate())
            .checkOutDate(request.checkOutDate())
            .build();

        reservationRepository.save(reservation);

        return ReservationResponse.from(reservation);
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

        // N+1 방지: accommodationId, roomId 리스트 추출
        List<String> accommodationIds = reservations.stream()
            .map(Reservation::getAccommodationId)
            .distinct()
            .toList();

        List<String> roomIds = reservations.stream()
            .map(Reservation::getRoomId)
            .distinct()
            .toList();

        // MongoDB에서 한 번에 조회
        Map<String, Accommodation> accommodationMap = accommodationRepository.findAllById(accommodationIds)
            .stream()
            .collect(Collectors.toMap(Accommodation::getId, a -> a));

        Map<String, Room> roomMap = roomRepository.findAllById(roomIds)
            .stream()
            .collect(Collectors.toMap(Room::getId, r -> r));

        // 결합
        return reservations.stream()
            .map(reservation -> {
                Accommodation accommodation = accommodationMap.get(reservation.getAccommodationId());
                Room room = roomMap.get(reservation.getRoomId());
                return ReservationDetailResponse.of(reservation, accommodation, room);
            })
            .toList();
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(ReservationNotFoundException::new);

        reservation.setStatus(ReservationStatus.CANCELLED);

        reservationRepository.save(reservation);
    }
}
