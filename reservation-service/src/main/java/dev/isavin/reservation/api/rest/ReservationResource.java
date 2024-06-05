package dev.isavin.reservation.api.rest;

import dev.isavin.reservation.billing.Invoice;
import dev.isavin.reservation.entity.Reservation;
import dev.isavin.reservation.inventory.GraphQLInventoryClient;
import dev.isavin.reservation.inventory.InventoryClient;
import dev.isavin.reservation.model.Car;
import dev.isavin.reservation.poi.rental.RentalClient;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.graphql.client.GraphQLClient;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

  public static final double STANDARD_RATE_PER_DAY = 19.99;

  @Inject
  @Channel("invoices")
  MutinyEmitter<Invoice> invoiceEmitter;

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
  @Retry(maxRetries = 5, delay = 1000)
  @Fallback(fallbackMethod = "availabilityFallback")
  public Uni<Collection<Car>> availability(
      @RestQuery LocalDate startDate,
      @RestQuery LocalDate endDate) {

    Log.info("-> /reservation/availability: startDate = %s, endDate=%s"
        .formatted(startDate, endDate));

    Uni<Map<Long, Car>> carsUni = inventoryClient.allCars()
        .map(cars -> cars.stream().collect(Collectors
            .toMap(Car::getId, Function.identity())));

    Uni<List<Reservation>> reservationsUni = Reservation.listAll();

    return Uni
        .combine().all()
        .unis(carsUni, reservationsUni).asTuple()
        .chain(tuple -> {
          Map<Long, Car> carsById = tuple.getItem1();
          List<Reservation> reservations = tuple.getItem2();
          // for each reservation, remove the car from the map
          for (Reservation reservation : reservations) {
            if (reservation.isReserved(startDate, endDate)) {
              carsById.remove(reservation.carId);
            }
          }
          return Uni.createFrom()
              .item(carsById.values());
        });

  }

  /**
   * Fallback method.
   * Used when original method "availability" spent all retry attempts.
   * Signature of the method should be the same.
   */
  public Uni<Collection<Car>> availabilityFallback(
      LocalDate startDate,
      LocalDate endDate) {
    Log.info("--> availabilityFallback");
    return Uni.createFrom().item(List.of());
  }

  /**
   * Create a new reservation
   */
  @Consumes(MediaType.APPLICATION_JSON)
  @POST
  @WithTransaction
  public Uni<Reservation> make(Reservation reservation) {

    reservation.userId = securityContext.getUserPrincipal() != null ?
        securityContext.getUserPrincipal().getName() : "anonymous";

    return reservation.<Reservation>persist().onItem()
        .call(persistedReservation -> {
          Log.info("Successfully reserved reservation " + persistedReservation);

          Uni<Void> invoiceUni = invoiceEmitter.send(new Invoice(reservation, computePrice(reservation)))
              .onFailure().invoke(
                  throwable ->
                      Log.warn("Couldn't create invoice for %s. %s%n"
                          .formatted(persistedReservation, throwable.getMessage())));

          if (persistedReservation.startDay.equals(LocalDate.now())) {
            return invoiceUni
                .chain(() -> rentalClient.start(persistedReservation.userId, persistedReservation.id)
                    .onItem().invoke(rental -> Log.info("Successfully started rental " + rental))
                    .replaceWith(persistedReservation));

          }
          return Uni.createFrom().item(persistedReservation);
        });
  }

  private double computePrice(Reservation reservation) {
    return (ChronoUnit.DAYS
        .between(reservation.startDay, reservation.endDay) + 1) * STANDARD_RATE_PER_DAY;
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
