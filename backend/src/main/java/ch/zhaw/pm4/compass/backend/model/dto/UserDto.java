package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.UserRole;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;


/**
 * Data Transfer Object (DTO) for user data within the system.
 * This class encapsulates comprehensive user information including identifiers, names, role,
 * day sheet associations, and deletion status.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors
 *
 * This DTO is essential for user management operations such as registration, updates, and displaying user profiles.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NonNull
    private String email;
    @NonNull
    private String given_name;
    @NonNull
    private String family_name;
    @NonNull
    private String user_id;
    private List<DaySheetDto> daySheets;

    private UserRole role;
    private Boolean deleted;

    /**
     * Constructs a UserDto with a comprehensive set of user information and a list of day sheets.
     * Assumes the user is not deleted by default.
     *
     * @param user_id Unique identifier of the user.
     * @param email Email address of the user.
     * @param given_name First name of the user.
     * @param family_name Last name of the user.
     * @param daySheets List of DaySheetDto instances associated with the user.
     * @param role Role of the user within the system.
     */
    public UserDto(String user_id, String email, String given_name, String family_name, List<DaySheetDto> daySheets, UserRole role) {
        this.user_id = user_id;
        this.email = email;
        this.given_name = given_name;
        this.family_name = family_name;
        this.daySheets = daySheets;
        this.role = role;
        this.deleted = false;
    }

    /**
     * Constructs a UserDto with essential user information, initializing the list of day sheets to an empty list.
     * Allows explicit setting of the deleted status.
     *
     * @param user_id Unique identifier of the user.
     * @param given_name First name of the user.
     * @param family_name Last name of the user.
     * @param email Email address of the user.
     * @param role Role of the user within the system.
     * @param deleted Boolean flag indicating if the user is considered deleted.
     */
    public UserDto(String user_id, String given_name, String family_name, String email, UserRole role, Boolean deleted) {
        this.user_id = user_id;
        this.given_name = given_name;
        this.family_name = family_name;
        this.email = email;
        this.daySheets = new ArrayList<>();
        this.role = role;
        this.deleted = !isNull(deleted) && deleted;
    }
}
