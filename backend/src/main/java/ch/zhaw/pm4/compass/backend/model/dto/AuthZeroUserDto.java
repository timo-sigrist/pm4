package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.UserRole;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a user in an Auth0 authentication context.
 * This class provides details about the user, including email, names, roles, and blocked status.
 * It utilizes Lombok annotations to reduce boilerplate code for getter and setter methods.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Getter
@Setter
public class AuthZeroUserDto {
    private String email;
    private String given_name;
    private String family_name;
    private UserRole role;
    private Boolean blocked;

    public AuthZeroUserDto() {

    }

    /**
     * Constructs a new AuthZeroUserDto with the provided details and defaults the blocked status to false.
     *
     * @param email       The email address of the user.
     * @param given_name  The first name of the user.
     * @param family_name The last name of the user.
     * @param role        The user's role within the application.
     */
    public AuthZeroUserDto(String email, String given_name, String family_name, UserRole role) {
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.role = role;
        this.blocked = false;
    }

    /**
     * Constructs a new AuthZeroUserDto with detailed user information including blocked status.
     *
     * @param email       The email address of the user.
     * @param given_name  The first name of the user.
     * @param family_name The last name of the user.
     * @param role        The user's role within the application.
     * @param blocked     Indicates whether the user is currently blocked.
     */
    public AuthZeroUserDto(String email, String given_name, String family_name, UserRole role, Boolean blocked) {
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.role = role;
        this.blocked = blocked;
    }

}
