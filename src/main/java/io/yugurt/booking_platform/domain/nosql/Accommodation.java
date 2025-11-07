package io.yugurt.booking_platform.domain.nosql;

import io.yugurt.booking_platform.domain.enums.Amenity;
import io.yugurt.booking_platform.dto.request.AccommodationUpdateRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accommodations")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Accommodation {

    @Id
    private String id;

    private String name;

    private String type;

    private String address;

    private String description;

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @Builder.Default
    private Set<Amenity> amenities = new HashSet<>();

    private Double latitude;

    private Double longitude;

    /**
     * 숙박 업소 정보를 수정합니다.
     */
    public void update(AccommodationUpdateRequest request) {
        this.name = request.name();
        this.type = request.type();
        this.address = request.address();
        this.description = request.description();
        this.imageUrls = request.imageUrls();
        this.amenities = request.amenities();
        this.latitude = request.latitude();
        this.longitude = request.longitude();
    }
}
