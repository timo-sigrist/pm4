package ch.zhaw.pm4.compass.backend;

import io.swagger.annotations.ApiModel;

/**
 * Enum representing the roles available in the Compass application.
 * This enum is used to differentiate between various user roles such as social worker, participant, admin, and no role.
 * Each role has a corresponding label.
 *
 * @autor baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@ApiModel
public enum UserRole {
	SOCIAL_WORKER("Social worker"), PARTICIPANT("Participant"), ADMIN("Admin"), NO_ROLE("Keine Rolle");

	public final String label;

	private UserRole(String label) {
		this.label = label;
	}
}
