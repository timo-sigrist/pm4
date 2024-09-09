package ch.zhaw.pm4.compass.backend.exception;

/**
 * Custom exception thrown when an attempt is made to access a category that does not exist in the system.
 * This exception is typically used in situations where a category is expected to exist in the database but is not found.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class CategoryNotFoundException extends Exception {
	private static final long serialVersionUID = 5766981433329948942L;

	/**
	 * Constructs a new CategoryNotFoundException with the specific ID of the category that was not found.
	 *
	 * @param id The ID of the category that could not be located.
	 */
	public CategoryNotFoundException(long id) {
		super(String.format("Category with id %d not found", id));
	}
}
