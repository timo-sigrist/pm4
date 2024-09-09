package ch.zhaw.pm4.compass.backend.model;

import org.springframework.data.annotation.PersistenceCreator;

import ch.zhaw.pm4.compass.backend.RatingType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * JPA entity representing a rating within the system. This class holds the rating value,
 * its role (type), and links to the category and day sheet it is associated with.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors
 *
 * This entity is crucial for the assessment processes within the system, allowing for evaluations
 * to be recorded.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Data
@Entity
@NoArgsConstructor
public class Rating {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int rating;

	@Enumerated(EnumType.STRING)
	private RatingType ratingRole;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Category category;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private DaySheet daySheet;

	/**
	 * Constructs a new Rating with a specified value and role. This constructor is typically used when
	 * the association to category and day sheet is established outside of the constructor logic,
	 * such as through setter injection or service logic.
	 *
	 * @param rating The numeric value of the rating, typically within a predefined scale.
	 * @param ratingRole The type of the rating, indicating how the rating should be interpreted.
	 */
	@PersistenceCreator
	public Rating(int rating, RatingType ratingRole) {
		this.rating = rating;
		this.ratingRole = ratingRole;
	}
}
