package io.yugurt.booking_platform.service;

import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.dto.request.RoomCreateRequest;
import io.yugurt.booking_platform.dto.request.RoomUpdateRequest;
import io.yugurt.booking_platform.dto.response.RoomDetailResponse;
import io.yugurt.booking_platform.exception.AccommodationNotFoundException;
import io.yugurt.booking_platform.exception.ErrorCode;
import io.yugurt.booking_platform.exception.ForbiddenException;
import io.yugurt.booking_platform.exception.RoomNotFoundException;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import io.yugurt.booking_platform.security.UserContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final AccommodationRepository accommodationRepository;

    public RoomDetailResponse createRoom(UserContext user, RoomCreateRequest request) {
        // 숙소 소유권 검증
        validateAccommodationOwnership(user, request.accommodationId());

        Room room = Room.builder()
            .accommodationId(request.accommodationId())
            .name(request.name())
            .roomType(request.roomType())
            .pricePerNight(request.pricePerNight())
            .maxOccupancy(request.maxOccupancy())
            .description(request.description())
            .build();

        roomRepository.save(room);

        return RoomDetailResponse.from(room);
    }

    public RoomDetailResponse getRoom(String id) {
        Room room = roomRepository.findById(id)
            .orElseThrow(RoomNotFoundException::new);

        return RoomDetailResponse.from(room);
    }

    public List<RoomDetailResponse> getRoomsByAccommodationId(String accommodationId) {
        List<Room> rooms = roomRepository.findByAccommodationId(accommodationId);

        return rooms.stream()
            .map(RoomDetailResponse::from)
            .toList();
    }

    public RoomDetailResponse updateRoom(UserContext user, String id, RoomUpdateRequest request) {
        Room room = roomRepository.findById(id)
            .orElseThrow(RoomNotFoundException::new);

        // 숙소 소유권 검증
        validateAccommodationOwnership(user, room.getAccommodationId());

        room.update(request);
        roomRepository.save(room);

        return RoomDetailResponse.from(room);
    }

    public void deleteRoom(UserContext user, String id) {
        Room room = roomRepository.findById(id)
            .orElseThrow(RoomNotFoundException::new);

        // 숙소 소유권 검증
        validateAccommodationOwnership(user, room.getAccommodationId());

        roomRepository.delete(room);
    }

    private void validateAccommodationOwnership(UserContext user, String accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
            .orElseThrow(AccommodationNotFoundException::new);

        if (!accommodation.getOwnerId().equals(user.userId())) {
            throw new ForbiddenException(ErrorCode.NOT_RESOURCE_OWNER);
        }
    }
}
