package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.Getter;
import lombok.Setter;


/**
 * Data Transfer Object (DTO) used for updating the notes associated with a specific day sheet.
 * This class encapsulates the necessary information (day sheet ID and notes) required to perform updates on the day sheet's notes.
 *
 * Lombok annotations (@Getter, @Setter) are used to automatically generate getters and setters for each field
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Getter
@Setter
public class UpdateDaySheetDayNotesDto {

    private Long id;

    private String day_notes;

    /**
     * Override default constructor for creating an instance without initializing fields.
     */
    public UpdateDaySheetDayNotesDto() {

    }

    /**
     * Constructs a new UpdateDaySheetDayNotesDto with specified day sheet ID and notes.
     * This constructor is primarily used for creating a DTO with all required information to update day notes.
     *
     * @param dayId The unique identifier of the day sheet whose notes are to be updated.
     * @param day_notes The new notes content to be set for the day sheet.
     */
    public UpdateDaySheetDayNotesDto(Long dayId, String day_notes) {
        this.id = dayId;
        this.day_notes = day_notes;
    }
}
