package dev.isavin.reservation.model;

import lombok.Getter;

@Getter
public class Car {

  private Long id;
  private String model;
  private String manufacturer;
  private String licensePlateNumber;

  public Car() {
    // need here for correct work of deserialization
  }

  public Car(Long id, String licensePlateNumber, String manufacturer, String model) {
    this.id =id;
    this.model =model;
    this.manufacturer =manufacturer;
    this.licensePlateNumber =licensePlateNumber;
  }

}