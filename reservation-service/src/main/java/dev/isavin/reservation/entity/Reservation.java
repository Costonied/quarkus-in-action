package dev.isavin.reservation.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation extends PanacheEntity {

  private Long carId;
  private String userId;
  private LocalDate endDay;
  private LocalDate startDay;

  /**
   * Check if the given duration overlaps with this reservation
   *
   * @return true if the dates overlap with the reservation, false otherwise
   */
  public boolean isReserved(LocalDate startDay, LocalDate endDay) {
    return (!(this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay)));
  }

  public Long getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return "Reservation{" +
        "carId=" + carId +
        ", userId='" + userId + '\'' +
        ", endDay=" + endDay +
        ", startDay=" + startDay +
        ", id=" + id +
        '}';
  }
}
