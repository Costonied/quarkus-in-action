package dev.isavin.inventory.database;

import dev.isavin.inventory.model.Car;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class CarInventory {

  private List<Car> cars;
  public static final AtomicLong ids = new AtomicLong(0);

  @PostConstruct
  void initialize() {
    cars = new CopyOnWriteArrayList<>();
    initialData();
  }

  public List<Car> getCars() {
    return cars;
  }

  private void initialData() {

    Car mazda = new Car();
    mazda.setId(ids.incrementAndGet());
    mazda.setManufacturer("Mazda");
    mazda.setModel("6");
    mazda.setLicensePlateNumber("ABC123");
    cars.add(mazda);

    Car ford = new Car();
    ford.setId(ids.incrementAndGet());
    ford.setManufacturer("Ford");
    ford.setModel("Mustang");
    ford.setLicensePlateNumber("XYZ987");
    cars.add(ford);

  }

}
