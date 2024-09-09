package ch.zhaw.pm4.compass.backend.exception;

/**
 * Custom exception thrown when a user attempts to edit a day sheet for which they are not the designated owner.
 * This exception is used to enforce access control within the application, ensuring that only authorized users can make changes to specific day sheets.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class UserNotOwnerOfDaySheetException extends Exception {
	private static final long serialVersionUID = 8883786356323374443L;

	/**
	 * Constructs a new UserNotOwnerOfDaySheetException with a predefined message indicating that the operation cannot proceed because the user does not own the day sheet.
	 */
	public UserNotOwnerOfDaySheetException() {
		super("User is not owner of Daysheet to be edited");
	}

}
