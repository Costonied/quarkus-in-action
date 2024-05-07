package dev.isavin.reservation.storage;

import dev.isavin.reservation.model.Reservation;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ReservationsRepositoryTest {

  @Inject
  ReservationsRepository repository;

  @Test
  void testCreateReservation() {

    Reservation reservation = Reservation.builder()
        .startDay(LocalDate.now().plusDays(5))
        .endDay(LocalDate.now().plusDays(12))
        .carId(384L)
        .build();

    repository.save(reservation);

    assertNotNull(reservation.getId());
    assertTrue(repository.findAll().contains(reservation));

  }

}