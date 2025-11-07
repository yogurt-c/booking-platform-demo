package io.yugurt.booking_platform.domain.rdb;

import io.yugurt.booking_platform.domain.enums.ReservationStatus;
import io.yugurt.booking_platform.exception.AlreadyCancelledReservationException;
import io.yugurt.booking_platform.exception.CannotCancelReservationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "reservations")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reservation {

    private static final int CANCELLATION_DEADLINE_DAYS = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accommodationId;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String guestId;  // 예약한 게스트 ID (GUEST)

    @Column(nullable = false)
    private String guestName;

    @Column(nullable = false)
    private String guestPhone;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.PENDING;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    public void cancel(LocalDate today) {
        validateNotCancelled();
        validateCancellationDeadline(today);
        this.status = ReservationStatus.CANCELLED;
    }

    private void validateNotCancelled() {
        if (this.status == ReservationStatus.CANCELLED) {

            throw new AlreadyCancelledReservationException();
        }
    }

    private void validateCancellationDeadline(LocalDate today) {
        LocalDate deadline = this.checkInDate.minusDays(CANCELLATION_DEADLINE_DAYS);

        if (today.isAfter(deadline)) {

            throw new CannotCancelReservationException();
        }
    }
}
