package dev.isavin.rental.rest;

import dev.isavin.rental.entity.Rental;
import io.quarkus.logging.Log;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("/rental")
public class RentalResource {

  @POST
  @Path("/start/{userId}/{reservationId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Rental start(String userId, Long reservationId) {

    Log.infof("Starting rental for %s with reservation %s", userId, reservationId);
    Rental rental = Rental.builder()
        .userId(userId)
        .reservationId(reservationId)
        .startDate(LocalDate.now())
        .active(true)
        .build();
    rental.persist();
    return rental;

  }

  @PUT
  @Path("/end/{userId}/{reservationId}")
  public Rental end(String userId, Long reservationId) {
    Log.infof("Ending rental for %s with reservation %s", userId, reservationId);
    Optional<Rental> optionalRental = Rental.findByUserAndReservationIdsOptional(userId, reservationId);
    if (optionalRental.isPresent()) {
      Rental rental = optionalRental.get();
      rental.setEndDate(LocalDate.now());
      rental.setActive(false);
      rental.update();
      return rental;
    } else {
      throw new NotFoundException("Rental not found");
    }
  }

  @GET
  public List<Rental> list() {
    return Rental.listAll();
  }

  @GET
  @Path("/active")
  public List<Rental> listActive() {
    return Rental.listActive();
  }

}
