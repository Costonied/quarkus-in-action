package dev.isavin.reservation.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Reservation {

  private Long id;
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

}
