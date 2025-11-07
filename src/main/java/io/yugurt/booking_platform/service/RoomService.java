package io.yugurt.booking_platform.service;

import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.dto.request.RoomCreateRequest;
import io.yugurt.booking_platform.dto.response.RoomDetailResponse;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
