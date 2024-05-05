package dev.isavin.reservation.inventory;

import dev.isavin.reservation.model.Car;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import org.eclipse.microprofile.graphql.Query;

import java.util.List;

@GraphQLClientApi(configKey = "inventory")
public interface GraphQLInventoryClient extends InventoryClient {

  @Query(value = "cars")
  List<Car> allCars();

}
