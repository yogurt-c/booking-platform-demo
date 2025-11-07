package io.yugurt.booking_platform.service;

import io.yugurt.booking_platform.domain.nosql.Accommodation;
import io.yugurt.booking_platform.dto.request.AccommodationCreateRequest;
import io.yugurt.booking_platform.dto.request.AccommodationUpdateRequest;
import io.yugurt.booking_platform.dto.request.CursorPageRequest;
import io.yugurt.booking_platform.dto.response.AccommodationDetailResponse;
import io.yugurt.booking_platform.dto.response.AccommodationSummaryResponse;
import io.yugurt.booking_platform.dto.response.CursorPageResponse;
import io.yugurt.booking_platform.exception.AccommodationNotFoundException;
import io.yugurt.booking_platform.repository.nosql.AccommodationRepository;
import io.yugurt.booking_platform.util.MongoCursorQueryBuilder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final MongoTemplate mongoTemplate;

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

        accommodationRepository.save(accommodation);

        return AccommodationDetailResponse.from(accommodation);
    }

    public AccommodationDetailResponse getAccommodation(String id) {
        Accommodation accommodation = accommodationRepository.findById(id)
            .orElseThrow(AccommodationNotFoundException::new);

        return AccommodationDetailResponse.from(accommodation);
    }

    public CursorPageResponse<AccommodationSummaryResponse> getAccommodations(CursorPageRequest request) {
        Query query = MongoCursorQueryBuilder.buildDescCursorQuery(request.cursor(), request.size());

        List<Accommodation> accommodations = mongoTemplate.find(query, Accommodation.class);

        List<AccommodationSummaryResponse> responses = accommodations.stream()
            .map(AccommodationSummaryResponse::from)
            .toList();

        return CursorPageResponse.of(
            responses,
            request.size(),
            AccommodationSummaryResponse::id
        );
    }

    public AccommodationDetailResponse updateAccommodation(String id, AccommodationUpdateRequest request) {
        Accommodation accommodation = accommodationRepository.findById(id)
            .orElseThrow(AccommodationNotFoundException::new);

        accommodation.update(request);
        accommodationRepository.save(accommodation);

        return AccommodationDetailResponse.from(accommodation);
    }

    public void deleteAccommodation(String id) {
        Accommodation accommodation = accommodationRepository.findById(id)
            .orElseThrow(AccommodationNotFoundException::new);

        accommodationRepository.delete(accommodation);
    }
}
