package io.yugurt.booking_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.dto.request.RoomCreateRequest;
import io.yugurt.booking_platform.dto.request.RoomUpdateRequest;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
        accommodationRepository.deleteAll();
    }

    @Test
    @DisplayName("객실 등록 - 성공")
    void createRoom() throws Exception {
        var accommodation = accommodationRepository.save(
            Accommodation.builder()
                .name("호텔 신라")
                .type("호텔")
                .address("서울특별시 중구")
                .build()
        );

        String name = "디럭스 더블룸";
        String roomType = "디럭스";
        BigDecimal pricePerNight = new BigDecimal("150000");
        Integer maxOccupancy = 2;
        String description = "킹사이즈 침대가 있는 디럭스 객실";

        var request = new RoomCreateRequest(
            accommodation.getId(),
            name,
            roomType,
            pricePerNight,
            maxOccupancy,
            description
        );

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.accommodationId").value(accommodation.getId()))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.roomType").value(roomType))
            .andExpect(jsonPath("$.pricePerNight").value(pricePerNight))
            .andExpect(jsonPath("$.maxOccupancy").value(maxOccupancy))
            .andExpect(jsonPath("$.description").value(description));
    }

    @Test
    @DisplayName("객실 상세 조회 - 성공")
    void getRoom() throws Exception {
        var accommodation = accommodationRepository.save(
            Accommodation.builder()
                .name("호텔 신라")
                .type("호텔")
                .address("서울특별시 중구")
                .build()
        );

        String name = "스위트룸";
        String roomType = "스위트";
        BigDecimal pricePerNight = new BigDecimal("300000");
        Integer maxOccupancy = 4;
        String description = "최고급 스위트 객실";

        var room = roomRepository.save(
            Room.builder()
                .accommodationId(accommodation.getId())
                .name(name)
                .roomType(roomType)
                .pricePerNight(pricePerNight)
                .maxOccupancy(maxOccupancy)
                .description(description)
                .build()
        );

        mockMvc.perform(get("/api/rooms/{id}", room.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(room.getId()))
            .andExpect(jsonPath("$.accommodationId").value(accommodation.getId()))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.roomType").value(roomType))
            .andExpect(jsonPath("$.pricePerNight").value(pricePerNight))
            .andExpect(jsonPath("$.maxOccupancy").value(maxOccupancy))
            .andExpect(jsonPath("$.description").value(description));
    }

    @Test
    @DisplayName("특정 숙소의 객실 목록 조회 - 성공")
    void getRoomsByAccommodationId() throws Exception {
        var accommodation = accommodationRepository.save(
            Accommodation.builder()
                .name("호텔 신라")
                .type("호텔")
                .address("서울특별시 중구")
                .build()
        );

        // 3개의 객실 생성
        roomRepository.save(
            Room.builder()
                .accommodationId(accommodation.getId())
                .name("스탠다드 트윈")
                .roomType("스탠다드")
                .pricePerNight(new BigDecimal("100000"))
                .maxOccupancy(2)
                .build()
        );
        roomRepository.save(
            Room.builder()
                .accommodationId(accommodation.getId())
                .name("디럭스 더블")
                .roomType("디럭스")
                .pricePerNight(new BigDecimal("150000"))
                .maxOccupancy(2)
                .build()
        );
        roomRepository.save(
            Room.builder()
                .accommodationId(accommodation.getId())
                .name("스위트")
                .roomType("스위트")
                .pricePerNight(new BigDecimal("300000"))
                .maxOccupancy(4)
                .build()
        );

        mockMvc.perform(get("/api/accommodations/{accommodationId}/rooms", accommodation.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].accommodationId").value(accommodation.getId()))
            .andExpect(jsonPath("$[1].accommodationId").value(accommodation.getId()))
            .andExpect(jsonPath("$[2].accommodationId").value(accommodation.getId()));
    }

    @Test
    @DisplayName("특정 숙소의 객실 목록 조회 - 빈 결과")
    void getRoomsByAccommodationIdEmpty() throws Exception {
        var accommodation = accommodationRepository.save(
            Accommodation.builder()
                .name("호텔 신라")
                .type("호텔")
                .address("서울특별시 중구")
                .build()
        );

        mockMvc.perform(get("/api/accommodations/{accommodationId}/rooms", accommodation.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("객실 수정 - 성공")
    void updateRoom() throws Exception {
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
                .name("스탠다드 더블")
                .roomType("스탠다드")
                .pricePerNight(new BigDecimal("100000"))
                .maxOccupancy(2)
                .description("기본 객실")
                .build()
        );

        String updatedName = "디럭스 더블룸";
        String updatedRoomType = "디럭스";
        BigDecimal updatedPrice = new BigDecimal("180000");
        Integer updatedMaxOccupancy = 3;
        String updatedDescription = "업그레이드된 디럭스 객실";

        var updateRequest = new RoomUpdateRequest(
            updatedName,
            updatedRoomType,
            updatedPrice,
            updatedMaxOccupancy,
            updatedDescription
        );

        mockMvc.perform(put("/api/rooms/{id}", room.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(room.getId()))
            .andExpect(jsonPath("$.accommodationId").value(accommodation.getId()))
            .andExpect(jsonPath("$.name").value(updatedName))
            .andExpect(jsonPath("$.roomType").value(updatedRoomType))
            .andExpect(jsonPath("$.pricePerNight").value(updatedPrice))
            .andExpect(jsonPath("$.maxOccupancy").value(updatedMaxOccupancy))
            .andExpect(jsonPath("$.description").value(updatedDescription));
    }

    @Test
    @DisplayName("객실 삭제 - 성공")
    void deleteRoom() throws Exception {
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
                .name("스탠다드 더블")
                .roomType("스탠다드")
                .pricePerNight(new BigDecimal("100000"))
                .maxOccupancy(2)
                .description("기본 객실")
                .build()
        );

        String roomId = room.getId();

        mockMvc.perform(delete("/api/rooms/{id}", roomId))
            .andDo(print())
            .andExpect(status().isNoContent());

        // DB에서 삭제 확인
        var deletedRoom = roomRepository.findById(roomId);
        assert deletedRoom.isEmpty();
    }
}
