package ch.zhaw.pm4.compass.backend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * JPA entity representing a day sheet, which details activities, incidents, and assessments for a particular day.
 * This class supports multiple constructor configurations to accommodate various initialization scenarios.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
public class DaySheet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.DATE)
	private LocalDate date;

	private String dayNotes;

	private Boolean confirmed = false;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private LocalUser owner;

	@OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
	private List<Timestamp> timestamps;

	@OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
	private List<Rating> moodRatings;

	@OneToMany(mappedBy = "daySheet", cascade = CascadeType.ALL)
	private List<Incident> incidents;

	public DaySheet() {

	}

	/**
	 * Simplified constructor initializing a day sheet with just notes and date.
	 * Useful for basic day sheet creation where detailed relations are not yet established.
	 *
	 * @param dayNotes Descriptive notes for the day.
	 * @param date The specific date of the day sheet.
	 */
	public DaySheet(String dayNotes, LocalDate date) {
		this.date = date;
		this.dayNotes = dayNotes;
		this.timestamps = new ArrayList<>();
		this.moodRatings = new ArrayList<>();
		this.incidents = new ArrayList<>();
	}

	/**
	 * Constructor initializing a day sheet with basic information.
	 *
	 * @param id       The identifier of the day sheet.
	 * @param dayNotes Descriptive notes for the day.
	 * @param date     The specific date of the day sheet.
	 */
	public DaySheet(Long id, String dayNotes, LocalDate date) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.timestamps = new ArrayList<>();
		this.moodRatings = new ArrayList<>();
		this.incidents = new ArrayList<>();
	}

	/**
	 * Constructor initializing a day sheet with confirmation status.
	 *
	 * @param id        The identifier of the day sheet.
	 * @param dayNotes  Descriptive notes for the day.
	 * @param date      The specific date of the day sheet.
	 * @param confirmed Whether the day sheet has been confirmed or not.
	 */
	public DaySheet(Long id, String dayNotes, LocalDate date, Boolean confirmed) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = new ArrayList<>();
		this.moodRatings = new ArrayList<>();
		this.incidents = new ArrayList<>();
	}

	/**
	 * Constructor initializing a day sheet with timestamps and confirmation status.
	 *
	 * @param id         The identifier of the day sheet.
	 * @param dayNotes   Descriptive notes for the day.
	 * @param date       The specific date of the day sheet.
	 * @param confirmed  Whether the day sheet has been confirmed or not.
	 * @param timestamps List of timestamps associated with the day sheet.
	 */
	public DaySheet(Long id, String dayNotes, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.moodRatings = new ArrayList<>();
		this.incidents = new ArrayList<>();
	}

	/**
	 * Constructor initializing a day sheet with an owner, timestamps, and confirmation status.
	 *
	 * @param id         The identifier of the day sheet.
	 * @param owner      The owner of the day sheet.
	 * @param dayNotes   Descriptive notes for the day.
	 * @param date       The specific date of the day sheet.
	 * @param confirmed  Whether the day sheet has been confirmed or not.
	 * @param timestamps List of timestamps associated with the day sheet.
	 */
	public DaySheet(Long id, LocalUser owner, String dayNotes, LocalDate date, Boolean confirmed,
			ArrayList<Timestamp> timestamps) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.owner = owner;
		this.moodRatings = new ArrayList<>();
		this.incidents = new ArrayList<>();
	}

	/**
	 * Constructor initializing a day sheet with timestamps, mood ratings, and confirmation status.
	 *
	 * @param id          The identifier of the day sheet.
	 * @param dayNotes    Descriptive notes for the day.
	 * @param date        The specific date of the day sheet.
	 * @param confirmed   Whether the day sheet has been confirmed or not.
	 * @param timestamps  List of timestamps associated with the day sheet.
	 * @param moodRatings List of mood ratings associated with the day sheet.
	 */
	public DaySheet(Long id, String dayNotes, LocalDate date, Boolean confirmed, ArrayList<Timestamp> timestamps,
			ArrayList<Rating> moodRatings) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.moodRatings = moodRatings;
		this.incidents = new ArrayList<>();
	}

	/**
	 * Fully specified constructor including all relationships.
	 *
	 * @param id          The identifier of the day sheet.
	 * @param owner       The owner of the day sheet.
	 * @param dayNotes    Descriptive notes for the day.
	 * @param date        The specific date of the day sheet.
	 * @param confirmed   Whether the day sheet has been confirmed or not.
	 * @param timestamps  List of timestamps associated with the day sheet.
	 * @param moodRatings List of mood ratings associated with the day sheet.
	 */
	public DaySheet(Long id, LocalUser owner, String dayNotes, LocalDate date, Boolean confirmed,
			ArrayList<Timestamp> timestamps, ArrayList<Rating> moodRatings) {
		this.id = id;
		this.date = date;
		this.dayNotes = dayNotes;
		this.confirmed = confirmed;
		this.timestamps = timestamps;
		this.owner = owner;
		this.moodRatings = moodRatings;
		this.incidents = new ArrayList<>();
	}

	/**
	 * Minimal constructor used for simple day sheet initialization with just the date.
	 *
	 * @param date The specific date of the day sheet.
	 */
	public DaySheet(LocalDate date) {
		this.date = date;
		this.timestamps = new ArrayList<>();
		this.moodRatings = new ArrayList<>();
		this.incidents = new ArrayList<>();
	}
}
