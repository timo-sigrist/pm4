package ch.zhaw.pm4.compass.backend.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExtendedRatingDto {
	@NonNull
	private LocalDate date;
	@NonNull
	private String participantName;
	@NonNull
	private RatingDto rating;
}
