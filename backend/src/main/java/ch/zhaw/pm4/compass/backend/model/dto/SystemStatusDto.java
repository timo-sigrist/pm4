package ch.zhaw.pm4.compass.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for representing the operational status of various system components.
 * This class provides details about the reachability of the backend, database, and Auth0 authentication service.
 *
 * Lombok annotations are used to simplify the creation of getters and constructors
 *
 * This DTO is used in health check/ monitoring APIs to provide a snapshot of the system's health and operational status.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemStatusDto {
    private String commitId;
    private boolean backendIsReachable;
    private boolean databaseIsReachable;
    private boolean auth0IsReachable;
}
