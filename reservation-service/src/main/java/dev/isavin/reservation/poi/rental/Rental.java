package dev.isavin.reservation.poi.rental;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class Rental {

  private final String id;
  private final String userId;
  private final Long reservationId;
  private final LocalDate startDate;



}