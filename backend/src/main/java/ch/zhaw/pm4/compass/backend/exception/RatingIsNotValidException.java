package ch.zhaw.pm4.compass.backend.exception;

import ch.zhaw.pm4.compass.backend.model.Category;

/**
 * Custom exception thrown when a rating value does not comply with the predefined limits set for a category.
 * This exception ensures that rating values are validated against category-specific constraints,
 * helping maintain data integrity and adherence to business rules.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class RatingIsNotValidException extends Exception {
	private static final long serialVersionUID = -2064461522110054964L;

	/**
	 * Constructs a new RatingIsNotValidException with detailed information about the failed validation.
	 *
	 * @param rating The rating object that failed validation.
	 * @param category The category against which the rating was validated.
	 */
	public RatingIsNotValidException(Category category) {
		super(String.format("Rating is not within range 0f %s: Min(%d) - Max:(%d)", category.getName(),
				category.getMinimumValue(), category.getMaximumValue()));
	}
}
