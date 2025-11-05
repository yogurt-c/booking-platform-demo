package io.yugurt.booking_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yugurt.booking_platform.domain.enums.Amenity;
import io.yugurt.booking_platform.dto.request.AccommodationCreateRequest;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccommodationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @BeforeEach
    void setUp() {
        accommodationRepository.deleteAll();
    }

    @Test
    @DisplayName("숙박 업소 등록 - 성공")
    void createAccommodation() throws Exception {
        var request = new AccommodationCreateRequest(
                "호텔 신라",
                "호텔",
                "서울특별시 중구 동호로 249",
                "최고급 호텔입니다",
                List.of("http://localhost:8080/image1.jpg"),
                Set.of(Amenity.WIFI, Amenity.PARKING),
                37.5559,
                126.9948
        );

        mockMvc.perform(post("/api/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("호텔 신라"))
                .andExpect(jsonPath("$.type").value("호텔"))
                .andExpect(jsonPath("$.address").value("서울특별시 중구 동호로 249"));
    }
}
