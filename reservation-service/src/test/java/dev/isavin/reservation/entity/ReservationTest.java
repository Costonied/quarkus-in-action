package dev.isavin.reservation.entity;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@QuarkusTest
class ReservationPersistenceTest {

  @Test
  @Transactional
  void testCreateReservation() {

    Reservation reservation = new Reservation();
    reservation.setStartDay(LocalDate.now().plus(5, ChronoUnit.DAYS));
    reservation.setEndDay(LocalDate.now().plus(12, ChronoUnit.DAYS));
    reservation.setCarId(384L);
    reservation.persist();

    Assertions.assertNotNull(reservation.id);
    Assertions.assertEquals(1, Reservation.count());
    Reservation persistedReservation = Reservation.findById(reservation.id);
    Assertions.assertNotNull(persistedReservation);
    Assertions.assertEquals(reservation.getCarId(), persistedReservation.getCarId());

  }

}