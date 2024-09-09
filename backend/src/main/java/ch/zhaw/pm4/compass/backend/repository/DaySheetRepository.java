package ch.zhaw.pm4.compass.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ch.zhaw.pm4.compass.backend.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import ch.zhaw.pm4.compass.backend.model.DaySheet;

/**
 * Spring Data JPA repository for the {@link DaySheet} entity. This interface provides methods to query
 * day sheets stored in the database based on various attributes such as date, owner, and confirmation status.
 *
 * Extends JpaRepository to provide CRUD operations and the ability to define custom queries
 * for more complex retrieval operations on the DaySheet entity.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */

public interface DaySheetRepository extends JpaRepository<DaySheet, Long> {
	/**
	 * Finds a day sheet by its ID.
	 *
	 * @param id The unique identifier of the day sheet.
	 * @return An Optional containing the day sheet if found, or an empty Optional if no day sheet exists with the given ID.
	 */
	Optional<DaySheet> findById(Long id);

	/**
	 * Finds a day sheet by its ID and owner's user ID.
	 *
	 * @param id The unique identifier of the day sheet.
	 * @param userId The user ID of the day sheet owner.
	 * @return An Optional containing the day sheet if found, or an empty Optional if no matching day sheet exists.
	 */
	Optional<DaySheet> findByIdAndOwnerId(Long id, String userId);

	/**
	 * Finds a day sheet by its date and owner's user ID.
	 *
	 * @param date The date of the day sheet.
	 * @param userId The user ID of the day sheet owner.
	 * @return An Optional containing the day sheet if found, or an empty Optional if no matching day sheet exists for the specified date and owner.
	 */
	Optional<DaySheet> findByDateAndOwnerId(LocalDate date, String userId);

	/**
	 * Finds all day sheets within a specific date range.
	 *
	 * @param firstMonthDay The start date of the range.
	 * @param lastMonthDay The end date of the range.
	 * @return A list of day sheets that fall between the specified dates.
	 */
	List<DaySheet> findAllByDateBetween(LocalDate firstMonthDay, LocalDate lastMonthDay);

	/**
	 * Finds all day sheets for a specific owner within a date range.
	 *
	 * @param userId The user ID of the day sheet owner.
	 * @param firstMonthDay The start date of the range.
	 * @param lastMonthDay The end date of the range.
	 * @return A list of day sheets owned by the specified user that fall between the specified dates.
	 */
	List<DaySheet> findAllByOwnerIdAndDateBetween(String userId, LocalDate firstMonthDay, LocalDate lastMonthDay);

  /**
   * Finds all day sheets for a specific date regardless of the owner.
   *
   * @param date The date of the day sheets.
   * @return A list of day sheets that fall on the specified date.
   */
	List<DaySheet> findAllByDate(LocalDate date);

	/**
	 * Finds all unconfirmed day sheets where the owner has a specific role.
	 *
	 * @param role The role of the day sheet owner (e.g., ADMIN, USER).
	 * @return A list of unconfirmed day sheets owned by users of the specified role.
	 */
	List<DaySheet> findAllByConfirmedIsFalseAndOwner_Role(UserRole role);
}
