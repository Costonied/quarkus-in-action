package dev.isavin.reservation.model;

import lombok.Getter;

@Getter
public class Car {

  private final Long id;
  private final String model;
  private final String manufacturer;
  private final String licensePlateNumber;

  public Car(Long id, String licensePlateNumber, String manufacturer, String model) {
    this.id =id;
    this.model =model;
    this.manufacturer =manufacturer;
    this.licensePlateNumber =licensePlateNumber;
  }

}