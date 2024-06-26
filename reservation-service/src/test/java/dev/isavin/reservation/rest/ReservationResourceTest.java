package dev.isavin.reservation.rest;

import dev.isavin.reservation.api.rest.ReservationResource;
import dev.isavin.reservation.entity.Reservation;
import dev.isavin.reservation.inventory.GraphQLInventoryClient;
import dev.isavin.reservation.model.Car;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.DisabledOnIntegrationTest;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;

@QuarkusTest
class ReservationResourceTest {

  @TestHTTPResource
  @TestHTTPEndpoint(ReservationResource.class)
  URL reservationResource;

  @TestHTTPResource("availability")
  @TestHTTPEndpoint(ReservationResource.class)
  URL availability;

  @Test
  void testReservationIds() {

    Reservation reservation = new Reservation();
    reservation.startDay = LocalDate.parse("2025-03-20");
    reservation.endDay = LocalDate.parse("2025-03-29");
    reservation.carId = 384L;

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(reservation)
        .when()
        .post(reservationResource)
        .then()
        .statusCode(200)
        .body("id", notNullValue());
  }

  @Test
  // Because this test uses mocks
  @DisabledOnIntegrationTest(forArtifactTypes =
      DisabledOnIntegrationTest.ArtifactType.NATIVE_BINARY)
  void testMakingAReservationAndCheckAvailability() {
    // Arrange
    String startDate = "2022-01-01";
    String endDate = "2022-01-10";
    Car peugeot = new Car(1L, "ABC 123", "Peugeot", "406");

    // Prepare mocks
    GraphQLInventoryClient mock = Mockito.mock(GraphQLInventoryClient.class);
    Mockito.when(mock.allCars())
        .thenReturn(Uni.createFrom()
            .item(List.of(peugeot)));
    QuarkusMock.installMockForType(mock, GraphQLInventoryClient.class);

    // Action and asserts
    // Get the list of available cars for our requested timeslot
    Car[] cars = RestAssured.given()
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .when().get(availability)
        .then().statusCode(200)
        .extract().body().as(Car[].class);
    // Choose one of the cars
    Car car = cars[0];
    // Prepare a Reservation object
    Reservation reservation = new Reservation();
    reservation.startDay = LocalDate.parse(startDate);
    reservation.endDay = LocalDate.parse(endDate);
    reservation.carId = car.getId();

    // Submit the reservation
    RestAssured
        .given().contentType(ContentType.JSON).body(reservation)
        .when().post(reservationResource).then().statusCode(200)
        .body("carId", is(car.getId().intValue()));
    // Verify that this car doesn't show as available anymore
    RestAssured
        .given()
        .queryParam("startDate", startDate) .queryParam("endDate", endDate)
        .when().get(availability)
        .then().statusCode(200)
        .body("findAll { car -> car.id == " + car.getId() + "}", hasSize(0));
  }

}