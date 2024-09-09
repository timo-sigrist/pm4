package ch.zhaw.pm4.compass.backend.model.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for managing the details of a day sheet in the Compass application.
 * This DTO contains comprehensive details for each day, including notes, confirmation status, timestamps,
 * mood ratings, incidents, and ownership information.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Getter
@Setter
@NoArgsConstructor
public class DaySheetDto {
	private Long id;
	private LocalDate date;
	private String day_notes;

	private Boolean confirmed = false;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@JsonIgnoreProperties({ "day_sheet_id" })
	private List<TimestampDto> timestamps;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@JsonIgnoreProperties({ "daysheet", "category.categoryOwners", "category.moodRatings" })
	private List<RatingDto> moodRatings;

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@JsonIgnoreProperties({ "date", "user" })
	private List<IncidentDto> incidents;

	private long timeSum;

	private UserDto owner;

	/**
	 * Constructor with parameters to set up a comprehensive day sheet.
	 *
	 * @param id Unique identifier of the day sheet.
	 * @param day_notes Notes associated with the day.
	 * @param date Date of the day sheet.
	 * @param confirmed Confirmation status.
	 * @param timestamps List of timestamps during the day.
	 * @param moodRatings Mood ratings of the day.
	 * @param incidents Incidents recorded during the day.
	 * @param owner Owner of the day sheet.
	 */
	public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed, List<TimestampDto> timestamps,
			List<RatingDto> moodRatings, List<IncidentDto> incidents, UserDto owner) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.moodRatings = moodRatings;
		this.incidents = incidents;
		this.owner = owner;
		setTimeSum();
	}

	/**
	 * Constructor for creating a fully detailed DaySheetDto.
	 * Includes all associated data such as timestamps, mood ratings, and incidents.
	 *
	 * @param id Unique identifier of the day sheet.
	 * @param day_notes Notes relevant to the day's activities.
	 * @param date The specific date of the day sheet.
	 * @param confirmed Status indicating whether the day sheet has been confirmed.
	 * @param timestamps List of timestamps logged during the day.
	 * @param moodRatings List of mood ratings associated with the day.
	 * @param incidents List of incidents reported on this day.
	 */
	public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed, List<TimestampDto> timestamps,
			List<RatingDto> moodRatings, List<IncidentDto> incidents) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.moodRatings = moodRatings;
		this.incidents = incidents;
		setTimeSum();
	}

	/**
	 * Constructor for DaySheetDto with timestamps only.
	 * Suitable for cases focusing primarily on time tracking.
	 *
	 * @param id Unique identifier of the day sheet.
	 * @param day_notes Notes relevant to the day's activities.
	 * @param date The specific date of the day sheet.
	 * @param confirmed Status indicating whether the day sheet has been confirmed.
	 * @param timestamps List of timestamps logged during the day.
	 */
	public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed, List<TimestampDto> timestamps) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		setTimeSum();
	}

	/**
	 * Constructor for DaySheetDto with mood ratings only.
	 * Used when primary concern is to track emotional feedback without detailed time logs.
	 *
	 * @param id Unique identifier of the day sheet.
	 * @param day_notes Notes relevant to the day's activities.
	 * @param date The specific date of the day sheet.
	 * @param moodRatings List of mood ratings associated with the day.
	 */
	public DaySheetDto(Long id, String day_notes, LocalDate date, List<RatingDto> moodRatings) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.moodRatings = moodRatings;
	}

	/**
	 * Simplified constructor for initial creation of a day sheet, typically used when the sheet is first created and details are minimal.
	 *
	 * @param id Unique identifier of the day sheet.
	 * @param day_notes Notes relevant to the day's activities.
	 * @param date The specific date of the day sheet.
	 * @param confirmed Status indicating whether the day sheet has been confirmed.
	 */
	public DaySheetDto(Long id, String day_notes, LocalDate date, Boolean confirmed) {
		this.id = id;
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
	}

	/**
	 * Constructor for creating a DaySheetDto with minimal required details, often used for creating new entries.
	 *
	 * @param day_notes Notes relevant to the day's activities.
	 * @param date The specific date of the day sheet.
	 * @param confirmed Status indicating whether the day sheet has been confirmed.
	 */
	public DaySheetDto(String day_notes, LocalDate date, Boolean confirmed) {
		this.date = date;
		this.day_notes = day_notes;
		this.confirmed = confirmed;
	}

	/**
	 * Calculates the total time sum of all timestamps within the day sheet.
	 * This is a utility method used internally to aggregate time durations from each timestamp.
	 */
	private void setTimeSum() {
		for (TimestampDto timestamp : this.getTimestamps()) {
			LocalTime startTime = timestamp.getStart_time();
			LocalTime endTime = timestamp.getEnd_time();
			long durationInMilliseconds = Duration.between(startTime, endTime).toMillis();
			timeSum += durationInMilliseconds;
		}
	}
}
