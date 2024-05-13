package dev.isavin.reservation.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Reservation extends PanacheEntity {

  public Long carId;
  public String userId;
  public LocalDate endDay;
  public LocalDate startDay;

  /**
   * Check if the given duration overlaps with this reservation
   *
   * @return true if the dates overlap with the reservation, false otherwise
   */
  public boolean isReserved(LocalDate startDay, LocalDate endDay) {
    return (!(this.endDay.isBefore(startDay) || this.startDay.isAfter(endDay)));
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
