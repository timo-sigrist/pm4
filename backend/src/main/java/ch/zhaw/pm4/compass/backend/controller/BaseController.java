package ch.zhaw.pm4.compass.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles the base route of the Compass application.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@RestController
public class BaseController {

    /**
     * Handles the base GET request.
     * @return A simple string message indicating the backend of the Compass application.
     */
    @GetMapping("/")
    public String index() {
        return "Backend of Compass application!";
    }

}
