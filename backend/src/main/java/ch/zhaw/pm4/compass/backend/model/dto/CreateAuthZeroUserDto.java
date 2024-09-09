package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.UserRole;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new Auth0 user. This class extends AuthZeroUserDto to include
 * additional information required for user registration, such as user ID and password.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Getter
@Setter
public class CreateAuthZeroUserDto extends AuthZeroUserDto {
	private String user_id;
	private String password;
	private String connection;

	/**
	 * Constructs a new CreateAuthZeroUserDto with all necessary details for registering a new user in Auth0.
	 *
	 * @param user_id The unique identifier of the user in the Auth0 system.
	 * @param email The email address of the user.
	 * @param given_name The first name of the user.
	 * @param family_name The last name of the user.
	 * @param role The role assigned to the user within the application.
	 * @param password The password for the user account.
	 */
	public CreateAuthZeroUserDto(String user_id, String email, String given_name, String family_name, UserRole role,
			String password) {
		super(email, given_name, family_name, role);
		this.user_id = user_id;
		this.password = password;
		this.connection = "Username-Password-Authentication";
	}
}
