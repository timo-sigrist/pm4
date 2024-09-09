package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the {@link Timestamp} entity. This interface provides standard CRUD operations
 * via JpaRepository and includes a method to retrieve timestamps based on a day sheet's identifier.
 *
 * The functionalities provided by this repository are integral to managing and querying timestamps,
 * which are critical for tracking the start and end times of activities recorded in day sheets.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public interface TimestampRepository extends JpaRepository<Timestamp, Long> {
    /**
     * Retrieves all timestamps associated with a specific day sheet.
     * This method is particularly useful for applications that need to process or display time-related
     * data for activities captured on a particular day.
     *
     * @param daySheetId The identifier of the day sheet for which timestamps are to be retrieved.
     * @return An iterable collection of timestamps linked to the specified day sheet.
     */
    Iterable<Timestamp> findAllByDaySheetId(Long daySheetId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM timestamp b WHERE b.id=:id",
            nativeQuery = true)
    void deleteTimestamp(@Param("id") Long id);
}
