package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Service class for managing timestamps associated with day sheets.
 * Handles the creation, updating, deletion, and retrieval of timestamps,
 * ensuring proper access control and data integrity.
 *
 * Using {@link TimestampRepository} for persistence operations.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Service
public class TimestampService {
	@Autowired
	private TimestampRepository timestampRepository;
	@Autowired
	private DaySheetRepository daySheetRepository;
	@Autowired
	private UserService userService;

	/**
	 * Creates a timestamp if it passes validation checks and the user is authorized.
	 * Ensures that no overlapping timestamps exist within the same day sheet.
	 *
	 * @param createTimestamp DTO containing the timestamp data to create.
	 * @param user_id The user ID attempting to create the timestamp.
	 * @return The created timestamp DTO or null if validation fails or if unauthorized.
	 */
	public TimestampDto createTimestamp(TimestampDto createTimestamp, String user_id) {
		Timestamp timestamp = convertTimestampDtoToTimestamp(createTimestamp, user_id);
		if (timestamp != null && checkNoDoubleEntry(timestamp)) {
			return convertTimestampToTimestampDto(timestampRepository.save(timestamp));
		} else {
			return null;
		}
	}

	/**
	 * Retrieves a timestamp by its ID if the user is authorized to view it.
	 *
	 * @param id The ID of the timestamp to retrieve.
	 * @param userId The ID of the user attempting to retrieve the timestamp.
	 * @return The retrieved timestamp DTO or null if not found or unauthorized.
	 */
	public TimestampDto getTimestampById(Long id, String userId) {
		if (!authCheckTimestamp(id, userId)) {
			return null;
		}

		Optional<Timestamp> response = timestampRepository.findById(id);
		if (response.isEmpty())
			return null;

		return convertTimestampToTimestampDto(response.get());
	}

	/**
	 * Retrieves all timestamps for a specific day sheet if the user is authorized.
	 *
	 * @param id The ID of the day sheet.
	 * @param userId The ID of the user attempting to retrieve the timestamps.
	 * @return A list of timestamp DTOs or an empty list if unauthorized.
	 */
	public ArrayList<TimestampDto> getAllTimestampsByDaySheetId(Long id, String userId) {
		if (!authCheckDaySheet(id, userId)) {
			return new ArrayList<>();
		}

		ArrayList<TimestampDto> resultList = new ArrayList<>();
		Iterable<Timestamp> list = timestampRepository.findAllByDaySheetId(id);
		for (Timestamp timestamp : list)
			resultList.add(convertTimestampToTimestampDto(timestamp));

		return resultList;
	}

	/**
	 * Updates a timestamp by its ID if the user is authorized and if the new timestamp does not overlap with others.
	 *
	 * @param updateTimestampDto DTO containing the updated timestamp data.
	 * @param userId The user ID attempting to update the timestamp.
	 * @return The updated timestamp DTO or null if validation fails or unauthorized.
	 */
	public TimestampDto updateTimestampById(TimestampDto updateTimestampDto, String userId) {
		if (!authCheckTimestamp(updateTimestampDto.getId(), userId)) {
			return null;
		}

		Optional<Timestamp> response = timestampRepository.findById(updateTimestampDto.getId());
		if (response.isPresent()) {
			Timestamp timestamp = response.get();
			Timestamp newTimestamp = new Timestamp(timestamp.getId(), updateTimestampDto.getStart_time(),
					updateTimestampDto.getEnd_time(), timestamp.getDaySheet());
			if (checkNoDoubleEntry(newTimestamp)) {
				timestamp.setStartTime(updateTimestampDto.getStart_time());
				timestamp.setEndTime(updateTimestampDto.getEnd_time());
				return convertTimestampToTimestampDto(timestampRepository.save(timestamp));
			}
		}

		return null;

	}

	/**
	 * Checks if the user is authorized to access or modify a specific timestamp.
	 * The user must either be the owner of the day sheet associated with the timestamp,
	 * or have a role of SOCIAL_WORKER or ADMIN.
	 *
	 * @param timestampId The ID of the timestamp to check.
	 * @param userId The ID of the user attempting to access or modify the timestamp.
	 * @return true if the user is authorized, false otherwise.
	 */
	private boolean authCheckTimestamp(Long timestampId, String userId) {
		Optional<Timestamp> optionalTimestamp = timestampRepository.findById(timestampId);
		UserRole userRole = userService.getUserRole(userId);
		if (userRole == UserRole.SOCIAL_WORKER || userRole == UserRole.ADMIN || (optionalTimestamp.isPresent()
				&& optionalTimestamp.get().getDaySheet().getOwner().getId().equals(userId))) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the user is authorized to access or modify a specific day sheet.
	 * The user must either be the owner of the day sheet, or have a role of SOCIAL_WORKER or ADMIN.
	 *
	 * @param daySheetId The ID of the day sheet to check.
	 * @param userId The ID of the user attempting to access or modify the day sheet.
	 * @return true if the user is authorized, false otherwise.
	 */
	private boolean authCheckDaySheet(Long daySheetId, String userId) {
		Optional<DaySheet> optionalDaySheet = daySheetRepository.findById(daySheetId);
		if (userService.getUserRole(userId) == UserRole.SOCIAL_WORKER || userService.getUserRole(userId) == UserRole.ADMIN
				|| (optionalDaySheet.isPresent() && optionalDaySheet.get().getOwner().getId().equals(userId))) {
			return true;
		}
		return false;
	}

	/**
	 * Deletes a timestamp by its ID if it exists.
	 *
	 * @param id The ID of the timestamp to delete.
	 */
	public void deleteTimestamp(Long id) {
		Optional<Timestamp> timestamp = timestampRepository.findById(id);
        if (timestamp.isPresent())
			timestampRepository.deleteTimestamp(timestamp.get().getId());
	}

	/**
	 * Converts a {@link TimestampDto} to a {@link Timestamp} entity.
	 * This method verifies the user's role and ownership before converting the DTO to an entity,
	 * ensuring that the user is authorized to create or update the timestamp.
	 *
	 * @param timestampDto The {@link TimestampDto} containing the data to be converted.
	 * @param user_id The ID of the user attempting to create or update the timestamp.
	 * @return A {@link Timestamp} entity if the user is authorized and the day sheet exists, null otherwise.
	 */
	private Timestamp convertTimestampDtoToTimestamp(TimestampDto timestampDto, String user_id) {
		Optional<DaySheet> daySheet;
		UserRole userRole = userService.getUserRole(user_id);

		if (userRole == UserRole.SOCIAL_WORKER || userRole == UserRole.ADMIN) {
			daySheet = daySheetRepository.findById(timestampDto.getDay_sheet_id());
		} else {
			daySheet = daySheetRepository.findByIdAndOwnerId(timestampDto.getDay_sheet_id(), user_id);
		}

		if (daySheet.isPresent()) {
			return new Timestamp(timestampDto.getId(), timestampDto.getStart_time(), timestampDto.getEnd_time(), daySheet.get());
		}
		return null;
	}

	/**
	 * Converts a {@link Timestamp} entity to a {@link TimestampDto}.
	 * This method is used to transform entity data into a format suitable for transfer over the network or use in the presentation layer.
	 *
	 * @param timestamp The {@link Timestamp} entity to convert.
	 * @return A {@link TimestampDto} containing the relevant data from the {@link Timestamp} entity.
	 */
	public TimestampDto convertTimestampToTimestampDto(Timestamp timestamp) {
		return new TimestampDto(timestamp.getId(), timestamp.getDaySheet().getId(), timestamp.getStartTime(),
				timestamp.getEndTime());
	}

	/**
	 * Checks whether a given timestamp does not overlap with any existing timestamps in the same day sheet.
	 * This method ensures that no two timestamps within the same day sheet have overlapping time periods.
	 *
	 * @param timestampToCheck The {@link Timestamp} entity to check for overlaps.
	 * @return true if there are no overlapping timestamps, false otherwise.
	 */
	public boolean checkNoDoubleEntry(Timestamp timestampToCheck) {
		Iterable<Timestamp> timestamps = timestampRepository
				.findAllByDaySheetId(timestampToCheck.getDaySheet().getId());

		boolean noDoubleEntry = true;
		if (timestampToCheck.getStartTime().isAfter(timestampToCheck.getEndTime())
				|| timestampToCheck.getStartTime().equals(timestampToCheck.getEndTime())) {
			noDoubleEntry = false;
			return noDoubleEntry;
		}
		for (Timestamp timestamp : timestamps) {
			if (timestampToCheck.getId() == timestamp.getId()) {
				continue;
			}
			if (timestampToCheck.getStartTime().isBefore(timestamp.getEndTime())
					&& timestampToCheck.getStartTime().isAfter(timestamp.getStartTime())) {
				noDoubleEntry = false;
				break;
			}

			if (timestampToCheck.getEndTime().isBefore(timestamp.getEndTime())
					&& timestampToCheck.getEndTime().isAfter(timestamp.getStartTime())) {
				noDoubleEntry = false;
				break;
			}

			if (timestampToCheck.getStartTime().equals(timestamp.getStartTime())
					|| timestampToCheck.getEndTime().equals(timestamp.getEndTime())) {
				noDoubleEntry = false;
				break;
			}
			if (timestampToCheck.getStartTime().isBefore(timestamp.getStartTime())
					&& timestampToCheck.getEndTime().isAfter(timestamp.getEndTime())) {
				noDoubleEntry = false;
				break;
			}
		}
		return noDoubleEntry;
	}
}
