package ch.zhaw.pm4.compass.backend.exception;

/**
 * Custom exception thrown when an attempt is made to submit more than the allowed number of ratings for a single category.
 * This exception is used to enforce rules that limit users to one rating per category, ensuring data integrity and adherence to application policies.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class TooManyRatingsPerCategoryException extends Exception {
	private static final long serialVersionUID = 5840233382938998438L;

	/**
	 * Constructs a new TooManyRatingsPerCategoryException with a predefined message that clarifies the restriction.
	 */
	public TooManyRatingsPerCategoryException() {
		super("Only one rating per Category is allowed");
	}
}
