package dev.isavin.reservation.api.rest;

import dev.isavin.reservation.entity.Reservation;
import dev.isavin.reservation.inventory.GraphQLInventoryClient;
import dev.isavin.reservation.inventory.InventoryClient;
import dev.isavin.reservation.model.Car;
import dev.isavin.reservation.poi.rental.Rental;
import dev.isavin.reservation.poi.rental.RentalClient;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

  @Inject
  SecurityContext securityContext;

  private final RentalClient rentalClient;
  private final InventoryClient inventoryClient;

  public ReservationResource(
      @GraphQLClient("inventory")
      GraphQLInventoryClient inventoryClient,
      @RestClient RentalClient rentalClient) {
    this.inventoryClient = inventoryClient;
    this.rentalClient = rentalClient;
  }

  /**
   * @param startDate format is YYYY-MM-DD
   * @param endDate   format is YYYY-MM-DD
   */
  @GET
  @Path("availability")
  public Uni<Collection<Car>> availability(
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
    return Reservation
        .<Reservation>listAll().onItem()
        .transform(reservations -> {
          // for each reservation, remove the car from the map
          for (Reservation reservation : reservations) {
            if (reservation.isReserved(startDate, endDate)) {
              carsById.remove(reservation.carId);
            }
          }
          return carsById.values();
        });

  }

  /**
   * Create a new reservation
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @WithTransaction
  public Uni<Reservation> make(Reservation reservation) {

    reservation.userId = securityContext.getUserPrincipal() != null ?
        securityContext.getUserPrincipal().getName() : "anonymous";

    Log.info("-> POST /reservation: carId = %s, userId = %s"
        .formatted(reservation.carId, reservation.userId));

    return reservation
        .<Reservation>persist().onItem()
        .call(persistedReservation -> {
          Log.info("Successfully reserved reservation " + persistedReservation);
          if (persistedReservation.startDay.equals(LocalDate.now()) && persistedReservation.userId != null) {
            Rental rental = rentalClient.start(
                persistedReservation.userId,
                persistedReservation.id);
            Log.info("Successfully started rental " + rental);
          }
          return Uni.createFrom().item(persistedReservation);
        });
  }

  /**
   * Get all reservations belonging to the current user
   */
  @GET
  @Path("all")
  public Uni<List<Reservation>> allReservations() {
    String userId = securityContext.getUserPrincipal() != null ? securityContext.getUserPrincipal().getName() : null;
    return Reservation
        .<Reservation>listAll().onItem()
        .transform(reservations ->
            reservations.stream()
                .filter(reservation -> userId == null || userId.equals(reservation.userId))
                .collect(Collectors.toList()));
  }

}
