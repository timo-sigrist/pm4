package ch.zhaw.pm4.compass.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing an incident related to a day sheet.
 * This class stores details about incidents such as titles and descriptions.
 * Each incident is linked to a specific day sheet, indicating where the incident occurred or was reported.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Data
@Entity
@NoArgsConstructor
public class Incident {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String description;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private DaySheet daySheet;

	/**
	 * Constructor for creating an incident with only title and description, without an initial day sheet.
	 * This constructor can be used when the day sheet association will be established later.
	 *
	 * @param title The title of the incident.
	 * @param description The detailed description of the incident.
	 */
	public Incident(String title, String description) {
		this.title = title;
		this.description = description;
	}

	/**
	 * Constructor for creating a fully initialized incident, including its association with a day sheet.
	 * This constructor is suitable when all incident details and its linkage to a specific day sheet are known at creation time.
	 *
	 * @param id The unique identifier of the incident.
	 * @param title The title of the incident.
	 * @param description The detailed description of the incident.
	 * @param daySheet The day sheet to which this incident is linked.
	 */
	public Incident(Long id, String title, String description, DaySheet daySheet) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.daySheet = daySheet;
	}
}
