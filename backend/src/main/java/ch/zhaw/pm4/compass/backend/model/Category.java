package ch.zhaw.pm4.compass.backend.model;

import java.util.List;

import org.springframework.data.annotation.PersistenceCreator;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a category within the system.
 * Each category has unique identifiers, a name, minimum and maximum values for associated ratings,
 * and lists of owners and mood ratings.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors
 *
 * The class includes methods to validate category owners and ratings to ensure they meet business rules.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Data
@Entity
@NoArgsConstructor
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String name;

	private Integer minimumValue;
	private Integer maximumValue;

	@ManyToMany
	private List<LocalUser> categoryOwners;

	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private List<Rating> moodRatings;

	/**
	 * Constructor to create a category with validation of category owners.
	 * Throws an exception if any of the provided category owners are invalid.
	 *
	 * @param name The unique name of the category.
	 * @param minimumValue Minimum valid rating value.
	 * @param maximumValue Maximum valid rating value.
	 * @param categoryOwners List of users who will own the category.
	 * @throws NotValidCategoryOwnerException If any of the provided users are not valid owners.
	 */
	@PersistenceCreator
	public Category(String name, int minimumValue, int maximumValue, List<LocalUser> categoryOwners)
			throws NotValidCategoryOwnerException {
		this.name = name;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;

		if (categoryOwners.isEmpty() || categoryOwners.stream().allMatch(i -> isValidCategoryOwner(i))) {
			this.categoryOwners = categoryOwners;
		} else {
			throw new NotValidCategoryOwnerException();
		}
	}

	/**
	 * Validates if a given rating is within the defined valid range for this category.
	 *
	 * @param rating The rating to validate.
	 * @return true if the rating is within the valid range, false otherwise.
	 */
	public boolean isValidRating(Rating rating) {
		return rating.getRating() >= this.minimumValue && rating.getRating() <= this.maximumValue;
	}

	/**
	 * Checks if a given user is a valid owner for the category based on their role.
	 *
	 * @param user The user to validate.
	 * @return true if the user is a PARTICIPANT, false otherwise.
	 */
	public boolean isValidCategoryOwner(LocalUser user) {
		return user.getRole() == UserRole.PARTICIPANT;
	}
}
