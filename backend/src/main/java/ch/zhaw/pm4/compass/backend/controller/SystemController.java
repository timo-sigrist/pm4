package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.model.dto.SystemStatusDto;
import ch.zhaw.pm4.compass.backend.service.SystemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing system-wide operations within the Compass application.
 * Provides an endpoint to retrieve the system status, including availability checks for backend components.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Tag(name = "System Controller", description = "System Endpoint")
@RestController
@RequestMapping("/system")
public class SystemController {
    @Autowired
    private SystemService systemService;

    @Value("${git.commit.id:local}")
    private String commitId;

    /**
     * Retrieves the current system status, including version control information and the health status of key components.
     *
     * @return ResponseEntity containing the SystemStatusDto which includes the commit ID and the reachability status of backend, database, and Auth0.
     */
    @GetMapping("/status")
    public ResponseEntity<SystemStatusDto> getStatus() {
        boolean backendIsReachable = systemService.isBackendReachable();
        boolean databaseIsReachable = systemService.isDatabaseReachable();
        boolean auth0IsReachable = systemService.isAuth0Reachable();

        return ResponseEntity.ok(new SystemStatusDto(commitId, backendIsReachable, databaseIsReachable, auth0IsReachable));
    }
}
