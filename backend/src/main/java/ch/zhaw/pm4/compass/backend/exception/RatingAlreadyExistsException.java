package ch.zhaw.pm4.compass.backend.exception;

/**
 * Custom exception thrown when an attempt is made to create a rating for a category that already has a rating.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 29.05.2024
 */
public class RatingAlreadyExistsException extends Exception {
    /**
     * Constructs a new RatingAlreadyExistsException with the specific ID of the category that already has a rating.
     * @param categoryId The ID of the category that already has a rating.
     */
    public RatingAlreadyExistsException(long categoryId) {
        super(String.format("Rating for category with id %d already exists", categoryId));
    }
}
