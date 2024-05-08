package dev.isavin.reservation.rest;

import dev.isavin.reservation.model.Reservation;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.LocalDate;

import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ReservationResourceTest {

  @TestHTTPResource
  @TestHTTPEndpoint(ReservationResource.class)
  URL reservationResource;

  @Test
  void testReservationIds() {

    Reservation reservation = Reservation.builder()
        .carId(12345L)
        .startDay(LocalDate.parse("2025-03-20"))
        .endDay(LocalDate.parse("2025-03-29"))
        .build();

    RestAssured.given()
        .contentType(ContentType.JSON)
        .body(reservation)
        .when()
        .post(reservationResource)
        .then()
        .statusCode(200)
        .body("id", notNullValue());
  }

}