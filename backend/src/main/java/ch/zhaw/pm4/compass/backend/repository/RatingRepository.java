package ch.zhaw.pm4.compass.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.zhaw.pm4.compass.backend.model.Rating;

/**
 * Spring Data JPA repository for the {@link Rating} entity.
 *
 * Extends JpaRepository to provide CRUD operations and the ability to define custom queries
 * for more complex retrieval operations on the Rating entity.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public interface RatingRepository extends JpaRepository<Rating, Long> {
	/**
	 * Retrieves all ratings associated with a specific day sheet.
	 * This method is useful for analyzing or displaying all ratings collected for a particular day sheet,
	 * such as in reports.
	 *
	 * @param daySheetId The identifier of the day sheet for which ratings are to be retrieved.
	 * @return An iterable collection of ratings linked to the specified day sheet.
	 */
	Iterable<Rating> findAllByDaySheetId(Long daySheetId);

	/**
	 * Retrieves all ratings associated with a specific category.
	 * This method supports functionality where ratings need to be segregated or analyzed based on their category,
	 * useful for categorial analysis or when adjusting category-specific metrics.
	 *
	 * @param categoryId The identifier of the category for which ratings are to be retrieved.
	 * @return An iterable collection of ratings linked to the specified category.
	 */
	Iterable<Rating> findAllByCategoryId(Long categoryId);
}
