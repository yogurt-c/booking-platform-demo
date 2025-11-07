package io.yugurt.booking_platform.repository.nosql;

import io.yugurt.booking_platform.domain.nosql.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {

    List<Room> findByAccommodationId(String accommodationId);
}
