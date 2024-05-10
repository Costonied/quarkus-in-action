package dev.isavin.inventory.grpc;

import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import dev.isavin.inventory.model.Car;
import dev.isavin.inventory.model.CarResponse;
import dev.isavin.inventory.model.InsertCarRequest;
import dev.isavin.inventory.model.InventoryService;
import dev.isavin.inventory.model.RemoveCarRequest;
import dev.isavin.inventory.repository.CarRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;

@GrpcService
public class GrpcInventoryService implements InventoryService {

  @Inject
  CarRepository carRepository;

  @Override
  @Blocking
  @Transactional
  public Uni<CarResponse> add(InsertCarRequest request) {

    Car car = Car.builder()
        .licensePlateNumber(request.getLicensePlateNumber())
        .manufacturer(request.getManufacturer())
        .model(request.getModel())
        .build();

    carRepository.persist(car);

    return Uni.createFrom().item(CarResponse.newBuilder()
        .setLicensePlateNumber(car.getLicensePlateNumber())
        .setModel(car.getModel())
        .setManufacturer(car.getManufacturer())
        .setId(car.getId())
        .build());
  }

  @Override
  @Blocking
  public Multi<CarResponse> addViaStream(Multi<InsertCarRequest> requests) {
    return requests
        .map(request -> {
          Car car = new Car();
          car.setLicensePlateNumber(request.getLicensePlateNumber());
          car.setManufacturer(request.getManufacturer());
          car.setModel(request.getModel());
          return car;
        }).onItem().invoke(car -> {
          Log.info("Persisting " + car);
          QuarkusTransaction.requiringNew().run(() ->  carRepository.persist(car));
        }).map(car -> CarResponse.newBuilder()
            .setLicensePlateNumber(car.getLicensePlateNumber())
            .setManufacturer(car.getManufacturer())
            .setModel(car.getModel())
            .setId(car.getId())
            .build());
  }

  @Override
  @Blocking
  @Transactional
  public Uni<CarResponse> remove(RemoveCarRequest request) {
    Optional<Car> optionalCar = carRepository
        .findByLicensePlateNumberOptional(
            request.getLicensePlateNumber());

    if (optionalCar.isPresent()) {
      Car removedCar = optionalCar.get();
      carRepository.delete(removedCar);
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