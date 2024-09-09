package ch.zhaw.pm4.compass.backend.model;

import java.util.List;

import ch.zhaw.pm4.compass.backend.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity representing a user in the system. This class includes the user's identifier, role,
 * associated day sheets, and categories they own.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors
 *
 *  This entity is linked to the DaySheet and Category entities through relationships.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LocalUser {
	@Id
	private String id;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@OneToMany(fetch = FetchType.LAZY)
	private List<DaySheet> daySheets;

	@ManyToMany(mappedBy = "categoryOwners")
	private List<Category> categories;

	/**
	 * Constructor for creating a LocalUser with an identifier and a role.
	 * This is useful when creating a new user where the full details of associated day sheets and categories are not yet known or needed.
	 *
	 * @param id   The unique identifier of the user.
	 * @param role The role assigned to the user within the system.
	 */
	public LocalUser(String id, UserRole role) {
		this.id = id;
		this.role = role;
	}

	/**
	 * Checks if the user's ID field is empty.
	 * This can be used to validate user entities to ensure they have been properly initialized with an identifier.
	 *
	 * @return true if the ID is empty, indicating the user object is not properly initialized; false otherwise.
	 */
	public boolean isEmpty() {
		return this.id.isEmpty();
	}
}