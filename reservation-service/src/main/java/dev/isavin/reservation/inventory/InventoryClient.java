package dev.isavin.reservation.inventory;

import dev.isavin.reservation.model.Car;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

public interface InventoryClient {
  // Don't work properly without '@Query(value = "cars")' here
  @Query(value = "cars")
  Uni<List<Car>> allCars();
}
