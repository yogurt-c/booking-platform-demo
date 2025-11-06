package io.yugurt.booking_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yugurt.booking_platform.domain.enums.Amenity;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.dto.request.AccommodationCreateRequest;
import io.yugurt.booking_platform.exception.ErrorCode;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    @DisplayName("숙박 업소 상세 조회 - 성공")
    void getAccommodation() throws Exception {
        String name = "파크 하얏트 서울";
        String type = "호텔";
        String address = "서울특별시 강남구 테헤란로 606";
        String description = "강남 중심의 럭셔리 호텔";
        List<String> imageUrls = List.of("http://localhost:8080/image1.jpg", "http://localhost:8080/image2.jpg");
        Set<Amenity> amenities = Set.of(Amenity.WIFI, Amenity.PARKING, Amenity.BREAKFAST, Amenity.POOL);
        double latitude = 37.5096;
        double longitude = 127.0594;

        var accommodation = accommodationRepository.save(
            Accommodation.builder()
                .name(name)
                .type(type)
                .address(address)
                .description(description)
                .imageUrls(imageUrls)
                .amenities(amenities)
                .latitude(latitude)
                .longitude(longitude)
                .build()
        );

        String accommodationId = accommodation.getId();

        mockMvc.perform(get("/api/accommodations/{id}", accommodationId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(accommodationId))
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.type").value(type))
            .andExpect(jsonPath("$.address").value(address))
            .andExpect(jsonPath("$.description").value(description))
            .andExpect(jsonPath("$.imageUrls").isArray())
            .andExpect(jsonPath("$.imageUrls.length()").value(imageUrls.size()))
            .andExpect(jsonPath("$.amenities").isArray())
            .andExpect(jsonPath("$.amenities.length()").value(amenities.size()))
            .andExpect(jsonPath("$.latitude").value(latitude))
            .andExpect(jsonPath("$.longitude").value(longitude));
    }

    @Test
    @DisplayName("숙박 업소 상세 조회 - 실패 (존재하지 않는 ID)")
    void getAccommodationNotFound() throws Exception {
        String nonExistentId = "id-not-exists";

        mockMvc.perform(get("/api/accommodations/{id}", nonExistentId))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value(ErrorCode.ACCOM_NOT_FOUND.name()));
    }
}
