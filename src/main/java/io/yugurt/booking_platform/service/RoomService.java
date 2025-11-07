package io.yugurt.booking_platform.service;

import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.dto.request.RoomCreateRequest;
import io.yugurt.booking_platform.dto.request.RoomUpdateRequest;
import io.yugurt.booking_platform.dto.response.RoomDetailResponse;
import io.yugurt.booking_platform.exception.RoomNotFoundException;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomDetailResponse createRoom(RoomCreateRequest request) {
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

    public RoomDetailResponse updateRoom(String id, RoomUpdateRequest request) {
        Room room = roomRepository.findById(id)
            .orElseThrow(RoomNotFoundException::new);

        room.setName(request.name());
        room.setRoomType(request.roomType());
        room.setPricePerNight(request.pricePerNight());
        room.setMaxOccupancy(request.maxOccupancy());
        room.setDescription(request.description());

        roomRepository.save(room);

        return RoomDetailResponse.from(room);
    }
}
