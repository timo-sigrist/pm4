package ch.zhaw.pm4.compass.backend.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateDaySheetDayNotesDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for handling day sheet operations within the Compass application.
 * Manages endpoints for creating, retrieving, updating, and confirming day sheets.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Tag(name = "DaySheet Controller", description = "DaySheet Endpoint")
@RestController
@RequestMapping("/daysheet")
public class DaySheetController {
	@Autowired
	private DaySheetService daySheetService;
	@Autowired
	private UserService userService;


	/**
	 * Creates a new day sheet with provided details. Only accessible if the user's role allows.
	 *
	 * @param daySheet Details of the new day sheet from the request body.
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity with the created DaySheetDto or an appropriate error status.
	 */
	@PostMapping(produces = "application/json")
	public ResponseEntity<DaySheetDto> createDaySheet(@RequestBody DaySheetDto daySheet,
			Authentication authentication) {
		if (daySheet.getDate() == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		DaySheetDto response = daySheetService.createDay(daySheet, authentication.getName());
		if (response == null) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

		return ResponseEntity.ok(response);
	}


	/**
	 * Retrieves a day sheet by its ID, ensuring the user has access to it.
	 *
	 * @param id The unique identifier for the day sheet.
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity containing the DaySheetDto or NotFound if it does not exist.
	 */
	@GetMapping(path = "/getById/{id}", produces = "application/json")
	public ResponseEntity<DaySheetDto> getDaySheetById(@PathVariable Long id, Authentication authentication) {
		DaySheetDto response = daySheetService.getDaySheetByIdAndUserId(id, authentication.getName());
		if (response == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Retrieves a day sheet by a specific date.
	 *
	 * @param date The date associated with the day sheet.
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity with the DaySheetDto or NotFound if it does not exist.
	 */
	@GetMapping(path = "/getByDate/{date}", produces = "application/json")
	public ResponseEntity<DaySheetDto> getDaySheetDate(@PathVariable String date, Authentication authentication) {
		DaySheetDto response = daySheetService.getDaySheetByDate(LocalDate.parse(date), authentication.getName());
		if (response == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Retrieves all day sheets that have not been confirmed.
	 *
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity with a list of unconfirmed DaySheetDto.
	 */
	@GetMapping(path = "/getAllNotConfirmed", produces = "application/json")
	public ResponseEntity<List<DaySheetDto>> getAllDaySheetNotConfirmed(Authentication authentication) {
		if(!isSocialWorkerOrAdmin(authentication)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return ResponseEntity.ok(daySheetService.getAllDaySheetNotConfirmed(authentication.getName()));
	}

	/**
	 * Retrieves all day sheets for a given month.
	 *
	 * @param month The month and year for which day sheets are requested.
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity with a list of DaySheetDto.
	 */
	@GetMapping(path = "/getAllByMonth/{month}", produces = "application/json")
	public ResponseEntity<List<DaySheetDto>> getAllDaySheetByMonth(@PathVariable YearMonth month, Authentication authentication) {
		if(!isSocialWorkerOrAdmin(authentication)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return ResponseEntity.ok(daySheetService.getAllDaySheetByMonth(month, authentication.getName()));
	}

	/**
	 * Retrieves all day sheets for a particular user and month.
	 *
	 * @param userId The ID of the user whose day sheets are requested.
	 * @param month The month and year of interest.
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity with a list of DaySheetDto.
	 */
	@GetMapping(path = "/getAllByParticipantAndMonth/{userId}/{month}", produces = "application/json")
	public ResponseEntity<List<DaySheetDto>> getAllDaySheetByParticipantAndMonth(@PathVariable String userId,
			@PathVariable YearMonth month, Authentication authentication) {
		if(!isSocialWorkerOrAdmin(authentication)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		return ResponseEntity.ok(daySheetService.getAllDaySheetByUserAndMonth(userId, month, authentication.getName()));
	}

	/**
	 * Retrieves a day sheet by its ID and user ID.
	 * @param userId The ID of the user.
	 * @param date The date of the day sheet.
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity with the DaySheetDto or NotFound if it does not exist.
	 */
	@GetMapping(path = "/getByParticipantAndDate/{userId}/{date}", produces = "application/json")
	public ResponseEntity<DaySheetDto> getDaySheetByParticipantAndDate(@PathVariable String userId, @PathVariable String date, Authentication authentication) {
		LocalDate localDate = LocalDate.parse(date);
		DaySheetDto response = daySheetService.getDaySheetByUserAndDate(userId, localDate, authentication.getName());
		if (response == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Updates the notes of a specific day sheet.
	 *
	 * @param updateDay DaySheet update details.
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity with the updated DaySheetDto or NotFound if the original does not exist.
	 */
	@PutMapping(path = "/updateDayNotes", produces = "application/json")
	public ResponseEntity<DaySheetDto> updateDayNotes(@RequestBody UpdateDaySheetDayNotesDto updateDay,
			Authentication authentication) {
		if(!isSocialWorkerOrAdmin(authentication)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		DaySheetDto response = daySheetService.updateDayNotes(updateDay, authentication.getName());
		if (response == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Confirms a day sheet by its ID.
	 *
	 * @param id The unique identifier for the day sheet to confirm.
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity with the confirmed DaySheetDto or NotFound if it does not exist.
	 */
	@PutMapping(path = "/confirm/{id}", produces = "application/json")
	public ResponseEntity<DaySheetDto> confirm(@PathVariable Long id, Authentication authentication) {
		if(!isSocialWorkerOrAdmin(authentication)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		DaySheetDto response = daySheetService.updateConfirmed(id, true, authentication.getName());
		if (response == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Revokes confirmation of a day sheet by its ID.
	 *
	 * @param id The unique identifier for the day sheet to revoke.
	 * @param authentication Current user's authentication details.
	 * @return ResponseEntity with the unconfirmed DaySheetDto or NotFound if it does not exist.
	 */
	@PutMapping(path = "/revoke/{id}", produces = "application/json")
	public ResponseEntity<DaySheetDto> revoke(@PathVariable Long id, Authentication authentication) {
		DaySheetDto response = daySheetService.updateConfirmed(id, false, authentication.getName());
		if(!isSocialWorkerOrAdmin(authentication)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		if (response == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * Checks if user is allowed to access the ressource (admin or Social worker)
	 *
	 * @param authentication Authentication object containing the user's security credentials.
	 * @return boolean if authentication is successfull or not.
	 */
	private boolean isSocialWorkerOrAdmin(Authentication authentication) {
		String callerId = authentication.getName();
		UserRole callingRole = userService.getUserRole(callerId);
		if (callingRole == UserRole.ADMIN || callingRole == UserRole.SOCIAL_WORKER) {
			return true;
		}

		return false;
	}
}