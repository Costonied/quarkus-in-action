package dev.isavin.rental.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class Rental extends PanacheMongoEntity {

  private String userId;
  private Long reservationId;
  private LocalDate startDate;
  @Setter
  private LocalDate endDate;
  @Setter
  private boolean active;

  public static Optional<Rental> findByUserAndReservationIdsOptional(
      String userId, Long reservationId) {
    return find("userId = ?1 and reservationId = ?2", userId, reservationId)
        .firstResultOptional();
  }

  public static List<Rental> listActive() {
    return list("active", true);
  }

}