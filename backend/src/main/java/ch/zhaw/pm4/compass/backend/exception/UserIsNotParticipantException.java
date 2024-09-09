package ch.zhaw.pm4.compass.backend.exception;

/**
 * Custom exception thrown when an operation requires a user to have participant status, but the user does not meet this criterion.
 * This exception is typically used in contexts where certain actions are restricted to users who are specifically
 * designated as participants within the application.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class UserIsNotParticipantException extends Exception {
	private static final long serialVersionUID = 7138122560782561252L;

	/**
	 * Constructs a new UserIsNotParticipantException with a predefined message indicating that the user does not have participant status.
	 */
	public UserIsNotParticipantException() {
		super("User is not a Participant");
	}
}
