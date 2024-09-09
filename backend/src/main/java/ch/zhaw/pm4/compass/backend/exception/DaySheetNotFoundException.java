package ch.zhaw.pm4.compass.backend.exception;

/**
 * Custom exception thrown when an attempt is made to access a day sheet that does not exist in the system.
 * This exception is used in scenarios where a day sheet is expected to exist in the database but is not found,
 * typically during retrieval operations by day sheet ID.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class DaySheetNotFoundException extends Exception {
	private static final long serialVersionUID = -7905280285542310018L;

	/**
	 * Constructs a new DaySheetNotFoundException with the specific ID of the day sheet that was not found.
	 *
	 * @param id The ID of the day sheet that could not be located.
	 */
	public DaySheetNotFoundException(long id) {
		super(String.format("DaySheet with id %d not found", id));
	}
}
