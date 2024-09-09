package ch.zhaw.pm4.compass.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.zhaw.pm4.compass.backend.model.Incident;

/**
 * Spring Data JPA repository for the {@link Incident} entity. This interface provides methods to query
 * Incident stored in the database
 *
 * Extends JpaRepository to provide CRUD operations and the ability to define custom queries
 * for more complex retrieval operations on the Incident entity.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public interface IncidentRepository extends JpaRepository<Incident, Long> {
	/**
	 * Retrieves a list of incidents based on the user ID of the day sheet owner.
	 * This method is particularly useful for filtering incidents that are associated with day sheets owned by a specific user.
	 *
	 * @param userId The user ID of the owner of the day sheets.
	 * @return A list of incidents associated with day sheets owned by the specified user.
	 */
	List<Incident> findAllByDaySheet_Owner_Id(String userId);
}