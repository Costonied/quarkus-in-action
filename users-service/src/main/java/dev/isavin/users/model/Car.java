package dev.isavin.users.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Car {

  private Long id;
  private String model;
  private String manufacturer;
  private String licensePlateNumber;

}