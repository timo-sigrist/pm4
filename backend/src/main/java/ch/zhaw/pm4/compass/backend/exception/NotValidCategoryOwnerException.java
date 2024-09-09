package ch.zhaw.pm4.compass.backend.exception;

/**
 * Custom exception thrown when an operation attempts to assign a category ownership to a user who does not have the
 * required permissions or role to be a category owner. This exception is used to enforce business rules
 * regarding who can own categories within the system.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class NotValidCategoryOwnerException extends Exception {
	private static final long serialVersionUID = 6583230349633757980L;

	/**
	 * Constructs a new NotValidCategoryOwnerException with a predefined message indicating that the user
	 * cannot be a category owner.
	 */
	public NotValidCategoryOwnerException() {
		super("User found in list that cannot be a CategoryOwner");
	}

}
