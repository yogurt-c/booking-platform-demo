package io.yugurt.booking_platform.repository.nosql;

import io.yugurt.booking_platform.domain.nosql.Accommodation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccommodationRepository extends MongoRepository<Accommodation, String> {

}
