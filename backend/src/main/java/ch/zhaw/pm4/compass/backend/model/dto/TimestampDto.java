package ch.zhaw.pm4.compass.backend.model.dto;

import java.time.LocalTime;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for managing timestamps related to day sheets.
 * This class encapsulates data for timestamps including identifiers and start/end times.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimestampDto {
	@ApiModelProperty(accessMode = AccessMode.READ_ONLY)
	private Long id;

	private Long day_sheet_id;
	@Schema(type = "string", example = "10:00:00")
	private LocalTime start_time;
	@Schema(type = "string", example = "10:00:00")
	private LocalTime end_time;

	/**
	 * Verifies if the end time is after the start time to ensure the timestamp's validity.
	 *
	 * @return true if the end time is after the start time, otherwise false.
	 */
	public boolean verifyTimeStamp() {
		return end_time.isAfter(start_time);
	}
}
