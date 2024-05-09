package dev.isavin.users.model;

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

}
