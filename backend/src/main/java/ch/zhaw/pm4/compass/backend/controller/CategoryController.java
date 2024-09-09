package ch.zhaw.pm4.compass.backend.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.GlobalCategoryException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.exception.UserIsNotParticipantException;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.service.CategoryService;
import ch.zhaw.pm4.compass.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for handling operations related to categories.
 * Provides endpoints for managing category details and associated operations.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Tag(name = "Category Controller", description = "Category Endpoint")
@RestController
@RequestMapping("/category")
public class CategoryController {
	@Autowired
	private UserService userService;
	@Autowired
	private CategoryService categoryService;

	/**
	 * Creates a new category with the provided details.
	 *
	 * @param category The category data transfer object containing the category details.
	 * @param authentication Authentication object containing user identity.
	 * @return ResponseEntity with the created CategoryDto or an appropriate error status.
	 * @throws CategoryAlreadyExistsException if the category already exists.
	 * @throws NotValidCategoryOwnerException if the user is not authorized to own the category.
	 */
	@PostMapping(produces = "application/json")
	public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto category,
			Authentication authentication) {
		String userId = authentication.getName();
		UserRole userRole = userService.getUserRole(userId);
		if (userRole != UserRole.ADMIN) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		try {
			return ResponseEntity.ok(categoryService.createCategory(category));
		} catch (CategoryAlreadyExistsException | NotValidCategoryOwnerException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Retrieves all categories.
	 *
	 * @return ResponseEntity with a list of all CategoryDto.
	 */
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<CategoryDto>> getAllCategories() {
		return ResponseEntity.ok(categoryService.getAllCategories());
	}

	/**
	 * Retrieves a list of categories associated with a specific user ID.
	 *
	 * @param userId The user ID whose category list is being requested.
	 * @return ResponseEntity with a list of CategoryDto or an appropriate error status.
	 */
	@GetMapping(path = "/getCategoryListByUserId/{userId}", produces = "application/json")
	public ResponseEntity<List<CategoryDto>> getCategoryListByUserId(@PathVariable String userId) {
		try {
			return ResponseEntity.ok(categoryService.getCategoryListByUserId(userId));
		} catch (UserIsNotParticipantException | NoSuchElementException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Links users to an existing category based on the provided category details.
	 *
	 * @param category The category data transfer object with user links.
	 * @param authentication Authentication object containing user identity.
	 * @return ResponseEntity with the updated CategoryDto or an appropriate error status.
	 */
	@PostMapping(path = "/linkUsersToExistingCategory", produces = "application/json")
	public ResponseEntity<CategoryDto> linkUsersToExistingCategory(@RequestBody CategoryDto category,
																   Authentication authentication) {
		String callerId = authentication.getName();
		UserRole callingRole = userService.getUserRole(callerId);
		if (callingRole != UserRole.ADMIN) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		try {
			return ResponseEntity.ok(categoryService.linkUsersToExistingCategory(category));
		} catch (NotValidCategoryOwnerException | NoSuchElementException | GlobalCategoryException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
