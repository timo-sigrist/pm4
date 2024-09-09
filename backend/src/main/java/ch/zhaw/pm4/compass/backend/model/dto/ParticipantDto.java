package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing a participant.
 * This class encapsulates the unique identifier for a participant within the system.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors
 *
 * This class ensures that every participant has a non-null identifier, which is crucial for tracking and managing participants across the application.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class ParticipantDto {
	@NonNull
	private String id;
}
