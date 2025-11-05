package io.yugurt.booking_platform.repository.rdb;

import io.yugurt.booking_platform.domain.rdb.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
