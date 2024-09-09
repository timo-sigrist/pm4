package ch.zhaw.pm4.compass.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for categories used within the Compass application.
 * This DTO includes details about category identifiers, names, value ranges, owners, and associated ratings.
 * Utilizes Lombok for simplifying the codebase by automatically generating getters, setters, equals, hashCode, and toString methods.
 *
 * @version 26.05.2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @NonNull
    private Long id;
    private String name;
    private Integer minimumValue;
    private Integer maximumValue;
    private List<UserDto> categoryOwners;

    /**
     * Constructs a CategoryDto using JSON properties, initializing the list of category owners.
     * This constructor is used for JSON deserialization.
     *
     * @param id The unique identifier of the category.
     * @param name The name of the category.
     * @param minimumValue The minimum value for ratings within this category.
     * @param maximumValue The maximum value for ratings within this category.
     */
    public CategoryDto(@JsonProperty("id") Long id, @JsonProperty("name") String name,
                       @JsonProperty("minimumValue") Integer minimumValue, @JsonProperty("maximumValue") Integer maximumValue) {
        this.id = id;
        this.name = name;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.categoryOwners = new ArrayList<>();
    }
}
