package io.yugurt.booking_platform.service;

import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.dto.request.AccommodationCreateRequest;
import io.yugurt.booking_platform.dto.response.AccommodationDetailResponse;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    public AccommodationDetailResponse createAccommodation(AccommodationCreateRequest request) {
        Accommodation accommodation = Accommodation.builder()
                .name(request.name())
                .type(request.type())
                .address(request.address())
                .description(request.description())
                .imageUrls(request.imageUrls())
                .amenities(request.amenities())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();

        Accommodation saved = accommodationRepository.save(accommodation);
        return AccommodationDetailResponse.from(saved);
    }
}
