package ch.zhaw.pm4.compass.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.LocalUser;


/**
 * Repository interface for {@link Category} entities. This interface handles data access functionality
 * and queries specifically related to categories in the database.
 *
 * Extends JpaRepository to provide CRUD operations and the ability to define custom queries
 * for more complex retrieval operations on the Category entity.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
	/**
	 * Finds a category by its ID.
	 *
	 * @param id The unique ID of the category.
	 * @return An Optional containing the category if found, or an empty Optional if no category is found with the given ID.
	 */
	Optional<Category> findById(Long id);

	/**
	 * Finds a category by its name.
	 *
	 * @param name The name of the category to find.
	 * @return An Optional containing the category if found, or an empty Optional if no category is found with the given name.
	 */
	Optional<Category> findByName(String name);

	/**
	 * Custom query to find all categories that do not have any category owners.
	 * These are considered global categories accessible to all users.
	 *
	 * @return An Iterable of categories that are global (i.e., no owners).
	 */
	@Query(value = "SELECT c FROM Category c WHERE c.categoryOwners IS EMPTY")
	Iterable<Category> findGlobalCategories();

	/**
	 * Finds all categories that are owned by a specified user.
	 *
	 * @param categoryOwner The user whose owned categories are to be retrieved.
	 * @return An Iterable of categories owned by the specified user.
	 */
	Iterable<Category> findAllByCategoryOwners(LocalUser categoryOwner);
}
