package dev.isavin.rental.rest;

import dev.isavin.rental.model.Rental;
import io.quarkus.logging.Log;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

@Path("/rental")
public class RentalResource {

  private final AtomicLong id = new AtomicLong(0);

  @POST
  @Path("/start/{userId}/{reservationId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Rental start(
      String userId,
      Long reservationId) {

    Log.infof("Starting rental for %s with reservation %s", userId, reservationId);
    return new Rental(id.incrementAndGet(), userId, reservationId, LocalDate.now());

  }

}
