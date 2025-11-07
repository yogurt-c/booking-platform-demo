package io.yugurt.booking_platform.repository.rdb;

import io.yugurt.booking_platform.domain.enums.ReservationStatus;
import io.yugurt.booking_platform.domain.rdb.Reservation;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r " +
        "WHERE r.roomId = :roomId " +
        "AND r.status != :cancelledStatus " +
        "AND NOT (r.checkOutDate <= :checkInDate OR r.checkInDate >= :checkOutDate)")
    List<Reservation> findConflictingReservations(
        @Param("roomId") String roomId,
        @Param("checkInDate") LocalDate checkInDate,
        @Param("checkOutDate") LocalDate checkOutDate,
        @Param("cancelledStatus") ReservationStatus cancelledStatus
    );

    List<Reservation> findByGuestId(String guestId);
}
