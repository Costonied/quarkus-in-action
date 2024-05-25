package dev.isavin.rental.rest;

import dev.isavin.rental.billing.InvoiceAdjust;
import dev.isavin.rental.entity.Rental;
import dev.isavin.rental.reservation.Reservation;
import dev.isavin.rental.reservation.ReservationClient;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Path("/rental")
public class RentalResource {

  public static final double STANDARD_REFUND_RATE_PER_DAY = -10.99;
  public static final double STANDARD_PRICE_FOR_PROLONGED_DAY = 25.99;

  @Inject
//  @RestClient
  ReservationClient reservationClient;

  @Inject
  @Channel("invoices-adjust")
  Emitter<InvoiceAdjust> adjustmentEmitter;

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

    Rental rental = Rental
        .findByUserAndReservationIdsOptional(userId, reservationId)
        .orElseThrow(() -> new NotFoundException("Rental not found"));

    Reservation reservation = reservationClient.getById(reservationId);

    LocalDate today = LocalDate.now();
    if (!reservation.getEndDate().isEqual(today)) {
      adjustmentEmitter
          .send(InvoiceAdjust.builder()
              .rentalId(rental.id.toString())
              .userId(userId)
              .actualEndDate(today)
              .price(computePrice(reservation.getEndDate(), today))
          .build());
    }

    rental.setEndDate(LocalDate.now());
    rental.setActive(false);
    rental.update();

    return rental;
  }

  private double computePrice(LocalDate endDate, LocalDate today) { return endDate.isBefore(today) ?
      ChronoUnit.DAYS.between(endDate, today) * STANDARD_PRICE_FOR_PROLONGED_DAY :
      ChronoUnit.DAYS.between(today, endDate) * STANDARD_REFUND_RATE_PER_DAY;
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
