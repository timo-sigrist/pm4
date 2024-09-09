package ch.zhaw.pm4.compass.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ch.zhaw.pm4.compass.backend.exception.*;
import ch.zhaw.pm4.compass.backend.model.dto.CreateRatingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.ExtendedRatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.service.RatingService;
import ch.zhaw.pm4.compass.backend.service.UserService;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for managing ratings within the Compass application.
 * Provides RESTful endpoints for creating and recording ratings related to day sheets and categories.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Tag(name = "Rating Controller", description = "Rating Endpoint")
@RestController
@RequestMapping("/rating")
public class RatingController {
	@Autowired
	private RatingService ratingService;

	/**
	 * Create multiple ratings related to a specific day sheet.
	 * @param daySheetId The day sheet ID where the ratings will be recorded.
	 * @param createRatingDtos List of rating DTOs related to the ratings.
	 * @return ResponseEntity with a list of created RatingDto or appropriate error status.
	 */
	@PostMapping(path = "/createRatingsByDaySheetId/{daySheetId}", produces = "application/json")
	@SchemaProperties()
	public ResponseEntity<List<RatingDto>> createRatingsByDaySheetId(@PathVariable Long daySheetId, @RequestBody List<CreateRatingDto> createRatingDtos, Authentication authentication) {
		try {

			return ResponseEntity.ok(ratingService.createRatingsByDaySheetId(daySheetId, createRatingDtos, authentication.getName()));
		} catch (DaySheetNotFoundException | CategoryNotFoundException | RatingAlreadyExistsException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
