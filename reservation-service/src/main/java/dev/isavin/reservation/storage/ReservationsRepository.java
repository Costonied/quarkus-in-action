package dev.isavin.reservation.storage;

import dev.isavin.reservation.model.Reservation;

import java.util.List;

public interface ReservationsRepository {
  List<Reservation> findAll();
  Reservation save(Reservation reservation);
}
