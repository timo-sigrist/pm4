package ch.zhaw.pm4.compass.backend.exception;

/**
 * Custom exception thrown when there is an attempt to link users to a global category in the system.
 * Global categories are typically shared across all users and should not have individual user associations.
 * This exception helps in enforcing business rules regarding category management.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class GlobalCategoryException extends Exception {
	private static final long serialVersionUID = 5529127806103716409L;

	/**
	 * Constructs a new GlobalCategoryException with a predefined message indicating that users cannot be linked
	 * to a global category.
	 */
	public GlobalCategoryException() {
		super("Cannot link Users to a global Category");
	}
}