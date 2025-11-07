package io.yugurt.booking_platform.domain.nosql;

import io.yugurt.booking_platform.dto.request.RoomUpdateRequest;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rooms")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Room {

    @Id
    private String id;

    private String accommodationId;

    private String name;

    private String roomType;

    private BigDecimal pricePerNight;

    private Integer maxOccupancy;

    private String description;

    /**
     * 객실 정보를 수정합니다.
     */
    public void update(RoomUpdateRequest request) {
        this.name = request.name();
        this.roomType = request.roomType();
        this.pricePerNight = request.pricePerNight();
        this.maxOccupancy = request.maxOccupancy();
        this.description = request.description();
    }
}
