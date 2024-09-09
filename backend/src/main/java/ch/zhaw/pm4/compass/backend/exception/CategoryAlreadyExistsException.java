package ch.zhaw.pm4.compass.backend.exception;

import ch.zhaw.pm4.compass.backend.model.Category;

/**
 * Custom exception thrown when an attempt is made to create a category that already exists in the system.
 * This exception helps in maintaining unique category names within the application.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class CategoryAlreadyExistsException extends Exception {
	private static final long serialVersionUID = 9071691353469205450L;

	/**
	 * Constructs a new CategoryAlreadyExistsException specific to the attempted category.
	 *
	 * @param category The category that was attempted to be added, which already exists.
	 */
	public CategoryAlreadyExistsException(Category category) {
		super(String.format("Category with Name %s already exists", category.getName()));
	}
}
