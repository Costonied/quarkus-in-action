package dev.isavin.reservation.billing;

import dev.isavin.reservation.entity.Reservation;
import lombok.Data;

@Data
public class Invoice {

  private final Reservation reservation;
  private final double price;

}
