package io.yugurt.booking_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.domain.nosql.Room;
import io.yugurt.booking_platform.domain.rdb.Reservation;
import io.yugurt.booking_platform.dto.request.ReservationCreateRequest;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.repository.nosql.RoomRepository;
import io.yugurt.booking_platform.repository.rdb.ReservationRepository;
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
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        accommodationRepository.deleteAll();
    }

    @Test
    @DisplayName("예약 생성 - 성공")
    void createReservation() throws Exception {
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
                .name("디럭스 더블")
                .roomType("디럭스")
                .pricePerNight(new BigDecimal("150000"))
                .maxOccupancy(2)
                .build()
        );

        String guestName = "홍길동";
        String guestPhone = "010-1234-5678";
        LocalDate checkInDate = LocalDate.now().plusDays(1);
        LocalDate checkOutDate = LocalDate.now().plusDays(3);

        var request = new ReservationCreateRequest(
            accommodation.getId(),
            room.getId(),
            guestName,
            guestPhone,
            checkInDate,
            checkOutDate
        );

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.accommodationId").value(accommodation.getId()))
            .andExpect(jsonPath("$.roomId").value(room.getId()))
            .andExpect(jsonPath("$.guestName").value(guestName))
            .andExpect(jsonPath("$.guestPhone").value(guestPhone))
            .andExpect(jsonPath("$.checkInDate").value(checkInDate.toString()))
            .andExpect(jsonPath("$.checkOutDate").value(checkOutDate.toString()))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("예약 생성 - 날짜 겹침으로 실패")
    void createReservationFailDueToDateConflict() throws Exception {
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
                .name("디럭스 더블")
                .roomType("디럭스")
                .pricePerNight(new BigDecimal("150000"))
                .maxOccupancy(2)
                .build()
        );

        LocalDate checkInDate = LocalDate.now().plusDays(1);
        LocalDate checkOutDate = LocalDate.now().plusDays(3);

        // 기존 예약 생성
        reservationRepository.save(
            Reservation.builder()
                .accommodationId(accommodation.getId())
                .roomId(room.getId())
                .guestName("김철수")
                .guestPhone("010-1111-2222")
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .build()
        );

        // 겹치는 날짜로 예약 시도
        var request = new ReservationCreateRequest(
            accommodation.getId(),
            room.getId(),
            "홍길동",
            "010-1234-5678",
            checkInDate.plusDays(1),  // 기존 예약과 겹침
            checkOutDate.plusDays(1)
        );

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isConflict());
    }
}
