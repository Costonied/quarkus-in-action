package dev.isavin.rental.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class Rental {

  private final Long id;
  private final String userId;
  private final Long reservationId;
  private final LocalDate startDate;



}