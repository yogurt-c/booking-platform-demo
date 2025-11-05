package io.yugurt.booking_platform.domain.nosql;

import io.yugurt.booking_platform.domain.enums.Amenity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "accommodations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
