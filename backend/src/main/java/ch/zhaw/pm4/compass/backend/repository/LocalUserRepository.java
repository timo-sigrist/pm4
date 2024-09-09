package ch.zhaw.pm4.compass.backend.repository;

import ch.zhaw.pm4.compass.backend.model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for the {@link LocalUser} entity.
 * This interface is used for data access operations on LocalUser entities within the database,
 *
 * Extends JpaRepository to provide CRUD operations and the ability to define custom queries
 * for more complex retrieval operations on the LocalUser entity.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
public interface LocalUserRepository extends JpaRepository<LocalUser, String> {
}
