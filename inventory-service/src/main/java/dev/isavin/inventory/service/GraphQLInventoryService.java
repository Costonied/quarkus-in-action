package dev.isavin.inventory.service;

import dev.isavin.inventory.model.Car;
import dev.isavin.inventory.repository.CarRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;
import java.util.Optional;

@GraphQLApi
public class GraphQLInventoryService {

  @Inject
  CarRepository carRepository;

  @Query
  public List<Car> cars() {
    return carRepository.listAll();
  }

  @Mutation
  @Transactional
  public Car register(Car car) {
    carRepository.persist(car);
    return car;
  }

  @Mutation
  @Transactional
  public boolean remove(String licensePlateNumber) {
    Optional<Car> toBeRemoved = carRepository
        .findByLicensePlateNumberOptional(licensePlateNumber);
    if (toBeRemoved.isPresent()) {
      carRepository.delete(toBeRemoved.get());
      return true;
    } else {
      return false;
    }
  }

}
