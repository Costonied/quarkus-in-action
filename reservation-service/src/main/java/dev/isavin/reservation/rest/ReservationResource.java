package dev.isavin.reservation.rest;

import dev.isavin.reservation.inventory.InventoryClient;
import dev.isavin.reservation.model.Car;
import dev.isavin.reservation.model.Reservation;
import dev.isavin.reservation.storage.ReservationsRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ReservationResource {

  private final InventoryClient inventoryClient;
  private final ReservationsRepository reservationsRepository;

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
    return reservationsRepository.save(reservation);
  }

}
