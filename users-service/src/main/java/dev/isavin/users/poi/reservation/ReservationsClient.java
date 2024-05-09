package dev.isavin.users.poi.reservation;

import dev.isavin.users.model.Car;
import dev.isavin.users.model.Reservation;
import io.quarkus.oidc.token.propagation.AccessToken;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.LocalDate;
import java.util.Collection;

@AccessToken
@Path("reservation")
@RegisterRestClient(baseUri = "http://localhost:8081")
public interface ReservationsClient {

  @GET
  @Path("all")
  Collection<Reservation> allReservations();

  @POST
  Reservation make(Reservation reservation);

  @GET
  @Path("availability")
  Collection<Car> availability(
      @RestQuery LocalDate startDate,
      @RestQuery LocalDate endDate);

}
