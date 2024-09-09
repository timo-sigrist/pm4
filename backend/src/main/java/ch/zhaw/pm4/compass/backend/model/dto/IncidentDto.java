package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing an incident.
 * This class encapsulates the details of an incident, including its unique identifier, title, description,
 * the date on which it occurred, and the associated user.
 *
 * Lombok annotations (@Data, @Builder, @AllArgsConstructor, @NoArgsConstructor) are used to automatically
 * generate getters, setters, constructors, toString, equals, and hashCode methods.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private UserDto user;
}
