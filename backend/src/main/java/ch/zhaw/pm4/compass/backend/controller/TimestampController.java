package ch.zhaw.pm4.compass.backend.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import ch.zhaw.pm4.compass.backend.service.TimestampService;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for managing timestamps related to day sheets in the Compass application.
 * Provides endpoints for creating, retrieving, updating, and deleting timestamps.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Tag(name = "Timestamp Controller", description = "Timestamp Endpoint")
@RestController
@RequestMapping("/timestamp")
public class TimestampController {
	@Autowired
	private TimestampService timestampService;
	@Autowired
	private DaySheetService daySheetService;

	/**
	 * Creates a new timestamp based on the provided DTO.
	 *
	 * @param timestamp Timestamp data transfer object containing details of the timestamp.
	 * @param authentication Authentication object containing the user's security credentials.
	 * @return ResponseEntity with the created TimestampDto or appropriate error status.
	 */
	@PostMapping(produces = "application/json")
	public ResponseEntity<TimestampDto> createTimestamp(@RequestBody TimestampDto timestamp,
			Authentication authentication) {
		if (!timestamp.verifyTimeStamp()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		DaySheetDto daySheet = daySheetService.getDaySheetByIdAndUserId(timestamp.getDay_sheet_id(),
				authentication.getName());
		if (daySheet == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		if (daySheet.getConfirmed())
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		TimestampDto response = timestampService.createTimestamp(timestamp, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return ResponseEntity.ok(response);
	}

	/**
	 * Retrieves a timestamp by its ID.
	 *
	 * @param id The unique identifier of the timestamp.
	 * @param authentication Authentication object containing the user's security credentials.
	 * @return ResponseEntity with the TimestampDto or NOT_FOUND if it does not exist.
	 */
	@GetMapping(path = "/getById/{id}", produces = "application/json")
	public ResponseEntity<TimestampDto> getTimestampById(@PathVariable Long id, Authentication authentication) {
		TimestampDto response = timestampService.getTimestampById(id, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return ResponseEntity.ok(response);
	}

	/**
	 * Retrieves all timestamps associated with a specific day sheet ID.
	 *
	 * @param id The day sheet ID.
	 * @param authentication Authentication object containing the user's security credentials.
	 * @return ResponseEntity containing a list of TimestampDto or NOT_FOUND if no timestamps are found.
	 */
	@GetMapping(path = "/allbydaysheetid/{id}", produces = "application/json")
	public ResponseEntity<ArrayList<TimestampDto>> getAllTimestampByDaySheetId(@PathVariable Long id,
			Authentication authentication) {
		ArrayList<TimestampDto> list = timestampService.getAllTimestampsByDaySheetId(id, authentication.getName());
		if (list == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return ResponseEntity.ok(list);
	}

	/**
	 * Updates a timestamp based on the provided DTO.
	 *
	 * @param timestamp Timestamp data transfer object containing updated details of the timestamp.
	 * @param authentication Authentication object containing the user's security credentials.
	 * @return ResponseEntity with the updated TimestampDto or appropriate error status.
	 */
	@PutMapping(produces = "application/json")
	public ResponseEntity<TimestampDto> putTimestamp(@RequestBody TimestampDto timestamp,
			Authentication authentication) {
		DaySheetDto daySheet = daySheetService.getDaySheetByIdAndUserId(timestamp.getDay_sheet_id(),
				authentication.getName());
		if (daySheet == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		if (daySheet.getConfirmed())
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		TimestampDto response = timestampService.updateTimestampById(timestamp, authentication.getName());
		if (response == null)
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		return ResponseEntity.ok(response);
	}

	/**
	 * Deletes a timestamp by its ID.
	 *
	 * @param id The unique identifier of the timestamp to delete.
	 * @param authentication Authentication object containing the user's security credentials.
	 * @return ResponseEntity indicating the result of the operation or appropriate error status.
	 */
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Object> deleteTimestamp(@PathVariable Long id, Authentication authentication) {
		TimestampDto timestamp = timestampService.getTimestampById(id, authentication.getName());
		if (timestamp == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		DaySheetDto daySheet = daySheetService.getDaySheetByIdAndUserId(timestamp.getDay_sheet_id(),
				authentication.getName());
		if (daySheet == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		if (daySheet.getConfirmed())
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		timestampService.deleteTimestamp(id);
		return ResponseEntity.ok().build();
	}
}