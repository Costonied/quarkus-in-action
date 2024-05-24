package dev.isavin.rental.billing;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceAdjust {

  private String rentalId;
  private String userId;
  private LocalDate actualEndDate; public double price;


}
