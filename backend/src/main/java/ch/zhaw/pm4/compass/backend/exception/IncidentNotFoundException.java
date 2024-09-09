package ch.zhaw.pm4.compass.backend.exception;

/**
 * Custom exception thrown when an attempt is made to access an incident that does not exist in the system.
 * This exception is used primarily in scenarios where an incident is expected to exist in the database but is not found,
 * typically during retrieval, update, or deletion operations by incident ID.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public class IncidentNotFoundException extends Exception {

    /**
     * Constructs a new IncidentNotFoundException with the specific ID of the incident that was not found.
     *
     * @param id The ID of the incident that could not be located.
     */
    public IncidentNotFoundException(long id) {
        super(String.format("DaySheet with id %d not found", id));
    }
}
