package io.yugurt.booking_platform.repository.nosql;

import io.yugurt.booking_platform.domain.nosql.Room;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {

    List<Room> findByAccommodationId(String accommodationId);
}
