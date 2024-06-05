package dev.isavin.reservation.poi.rental;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestPath;

@Path("/rental")
@RegisterRestClient(configKey = "rental")
public interface RentalClient {

  @POST
  @Path("/start/{userId}/{reservationId}")
  Uni<Rental> start(
      @RestPath String userId,
      @RestPath Long reservationId);

}
