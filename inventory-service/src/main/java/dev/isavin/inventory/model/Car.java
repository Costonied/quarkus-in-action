package dev.isavin.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String licensePlateNumber;
  private String manufacturer;
  private String model;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Car car = (Car) o;
    return Objects.equals(id, car.id) && Objects.equals(licensePlateNumber, car.licensePlateNumber) && Objects.equals(manufacturer, car.manufacturer) && Objects.equals(model, car.model);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, licensePlateNumber, manufacturer, model);
  }
}