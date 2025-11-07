package io.yugurt.booking_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.dto.request.RoomCreateRequest;
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
}
