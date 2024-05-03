package dev.isavin.reservation.inventory;

import dev.isavin.reservation.model.Car;

import java.util.List;

public interface InventoryClient {
  List<Car> allCars();
}
