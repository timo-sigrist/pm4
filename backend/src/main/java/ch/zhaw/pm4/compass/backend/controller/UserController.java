package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.dto.AuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.CreateAuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing user information within the Compass application.
 * Provides endpoints for creating, deleting, updating, and retrieving user details.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Tag(name = "User Controller", description = "User Endpoint")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Creates a new user based on the provided DTO.
     *
     * @param userDto Data transfer object containing the details necessary to create a new Auth0 user.
     * @return ResponseEntity containing the created UserDto or BAD_REQUEST if the operation fails.
     */
    @PostMapping(produces = "application/json")
    public ResponseEntity<UserDto> createUser(@RequestBody CreateAuthZeroUserDto userDto) {
        UserDto queryUser = userService.createUser(userDto);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The unique identifier of the user to be deleted.
     * @return ResponseEntity containing the UserDto of the deleted user or BAD_REQUEST if the operation fails.
     */
    @DeleteMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<UserDto> deleteUser(@PathVariable String id) {
        UserDto queryUser = userService.deleteUser(id);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Updates the details of an existing user by their ID.
     *
     * @param id The unique identifier of the user to update.
     * @param userDto Data transfer object containing the updated details for the user.
     * @return ResponseEntity containing the updated UserDto or BAD_REQUEST if the operation fails.
     */
    @PutMapping(path = "update/{id}", produces = "application/json")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody AuthZeroUserDto userDto) {
        UserDto queryUser = userService.updateUser(id, userDto);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Restores a previously deleted user by their ID.
     *
     * @param id The unique identifier of the user to restore.
     * @return ResponseEntity containing the restored UserDto or BAD_REQUEST if the operation fails.
     */
    @PutMapping(path = "restore/{id}", produces = "application/json")
    public ResponseEntity<UserDto> restoreUser(@PathVariable String id) {
        UserDto queryUser = userService.restoreUser(id);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The unique identifier of the user to retrieve.
     * @return ResponseEntity containing the UserDto or BAD_REQUEST if the user cannot be found.
     */
    @GetMapping(path = "getById/{id}", produces = "application/json")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        UserDto queryUser = userService.getUserById(id);
        if (queryUser != null) {
            return ResponseEntity.ok(queryUser);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Retrieves all users registered in the system.
     *
     * @return ResponseEntity containing a list of all UserDto or BAD_REQUEST if there are no users.
     */
    @GetMapping(path = "getAllUsers", produces = "application/json")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> queryUsers = userService.getAllUsers();
        if (queryUsers != null) {
            return ResponseEntity.ok(queryUsers);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /**
     * Retrieves all users registered as participants in the system.
     *
     * @return ResponseEntity containing a list of all participant UserDto or BAD_REQUEST if there are no participants.
     */
    @GetMapping(path = "getAllParticipants", produces = "application/json")
    public ResponseEntity<List<UserDto>> getAllParticipants() {
        List<UserDto> queryUsers = userService.getAllParticipants();
        if (queryUsers != null) {
            return ResponseEntity.ok(queryUsers);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}