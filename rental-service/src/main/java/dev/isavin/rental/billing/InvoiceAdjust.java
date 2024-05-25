package dev.isavin.rental.billing;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InvoiceAdjust {

  private String rentalId;
  private String userId;
  private LocalDate actualEndDate;
  private double price;


}
