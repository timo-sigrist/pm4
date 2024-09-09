package ch.zhaw.pm4.compass.backend;

import io.swagger.annotations.ApiModel;


/**
 * Enum representing the types of ratings available in the Compass application.
 * This enum is used to differentiate between ratings given by social workers and participants.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil
 * @version 26.05.2024
 */
@ApiModel
public enum RatingType {
	SOCIAL_WORKER, PARTICIPANT
}
