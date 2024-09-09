package ch.zhaw.pm4.compass.backend.model.dto;

import ch.zhaw.pm4.compass.backend.RatingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRatingDto {
    private Long categoryId;
    private Integer rating;
}
