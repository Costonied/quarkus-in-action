package dev.isavin.inventory.grpc;

import dev.isavin.inventory.database.CarInventory;
import dev.isavin.inventory.model.*;
import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

import java.util.Optional;

@GrpcService
public class GrpcInventoryService implements InventoryService {

  @Inject
  CarInventory inventory;

  @Override
  public Multi<CarResponse> add(Multi<InsertCarRequest> requests) {

    return requests
        .map(request -> {
          Car car = new Car();
          car.setLicensePlateNumber(request.getLicensePlateNumber());
          car.setManufacturer(request.getManufacturer());
          car.setModel(request.getModel());
          car.setId(CarInventory.ids.incrementAndGet());
          return car;
        }).onItem().invoke(car -> {
          Log.info("Persisting " + car);
          inventory.getCars().add(car);
        }).map(car -> CarResponse.newBuilder()
              .setLicensePlateNumber(car.getLicensePlateNumber())
              .setManufacturer(car.getManufacturer())
              .setModel(car.getModel())
              .setId(car.getId()).build());

  }

  @Override
  public Uni<CarResponse> remove(RemoveCarRequest request) {
    Optional<Car> optionalCar = inventory.getCars().stream()
        .filter(car -> request.getLicensePlateNumber().equals(car.getLicensePlateNumber()))
        .findFirst();

    if (optionalCar.isPresent()) {
      Car removedCar = optionalCar.get();
      inventory.getCars().remove(removedCar);
      return Uni.createFrom().item(CarResponse.newBuilder()
          .setLicensePlateNumber(removedCar.getLicensePlateNumber())
          .setManufacturer(removedCar.getManufacturer())
          .setModel(removedCar.getModel())
          .setId(removedCar.getId())
          .build());
    }
    return Uni.createFrom().nullItem();
  }
}
