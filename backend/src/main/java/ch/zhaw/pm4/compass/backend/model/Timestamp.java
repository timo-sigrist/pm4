package ch.zhaw.pm4.compass.backend.model;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a time record within a day sheet.
 * This class stores start and end times for activities or events recorded in a specific day sheet.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Timestamp {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalTime startTime;
  private LocalTime endTime;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private DaySheet daySheet;
}
