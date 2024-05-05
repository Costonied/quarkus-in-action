package dev.isavin.reservation.rest;

import dev.isavin.reservation.inventory.GraphQLInventoryClient;
import dev.isavin.reservation.inventory.InventoryClient;
import dev.isavin.reservation.model.Car;
import dev.isavin.reservation.model.Reservation;
import dev.isavin.reservation.poi.rental.RentalClient;
import dev.isavin.reservation.storage.ReservationsRepository;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

  private final RentalClient rentalClient;
  private final InventoryClient inventoryClient;
  private final ReservationsRepository reservationsRepository;

  public ReservationResource(
      ReservationsRepository reservations,
      @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
      @RestClient RentalClient rentalClient) {
    this.reservationsRepository = reservations;
    this.inventoryClient = inventoryClient;
    this.rentalClient = rentalClient;
  }

  /**
   * @param startDate format is YYYY-MM-DD
   * @param endDate   format is YYYY-MM-DD
   */
  @GET
  @Path("availability")
  public Collection<Car> availability(
      @RestQuery LocalDate startDate,
      @RestQuery LocalDate endDate) {

    // obtain all cars from inventory
    List<Car> availableCars = inventoryClient.allCars();
    // create a map from id to car
    Map<Long, Car> carsById = new HashMap<>();
    for (Car car : availableCars) {
      carsById.put(car.getId(), car);
    }
    // get all current reservations
    List<Reservation> reservations = reservationsRepository.findAll();
    // for each reservation, remove the car from the map
    for (Reservation reservation : reservations) {
      if (reservation.isReserved(startDate, endDate)) {
        carsById.remove(reservation.getCarId());
      }
    }
    return carsById.values();

  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Reservation make(Reservation reservation) {
    Log.info("-> POST /reservation: carId = %s".formatted(reservation.getCarId()));
    Reservation result = reservationsRepository.save(reservation);
    // this is just a dummy value for the time being
    String userId = "x";
    if (reservation.getStartDay().equals(LocalDate.now())) {
      rentalClient.start(userId, result.getId());
    }
    return result;
  }

}
