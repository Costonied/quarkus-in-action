package dev.isavin.reservation;

public class Car {

  public final Long id;
  public final String model;
  public final String manufacturer;
  public final String licensePlateNumber;

  public Car(Long id, String licensePlateNumber, String manufacturer, String model) {
    this.id =id;
    this.model =model;
    this.manufacturer =manufacturer;
    this.licensePlateNumber =licensePlateNumber;
  }

}