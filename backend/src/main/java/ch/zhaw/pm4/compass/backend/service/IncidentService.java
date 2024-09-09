package ch.zhaw.pm4.compass.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.exception.DaySheetNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.IncidentNotFoundException;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Incident;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.IncidentDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.IncidentRepository;

/**
 * Service class for managing incidents linked to day sheets. 
 * This includes creating, updating, deleting incidents, and retrieving incident details.
 *
 * Using {@link IncidentRepository} for persistence operations.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Service
public class IncidentService {
   @Autowired
   private IncidentRepository incidentRepository;
   @Autowired
   private DaySheetService daySheetService;
   @Autowired
   private UserService userService;

  /**
   * Creates an incident and assigns it to an existing day sheet based on the provided date, 
   * or creates a new day sheet if one does not exist.
   *
   * @param createIncident The incident DTO to create.
   * @return The created incident DTO.
   * @throws DaySheetNotFoundException If the day sheet could not be found or created.
   */
  public IncidentDto createIncident(IncidentDto createIncident) throws DaySheetNotFoundException {
    DaySheetDto daySheetDto = daySheetService.getDaySheetByDate(createIncident.getDate(),
        createIncident.getUser().getUser_id());
    if (daySheetDto == null) {
      DaySheetDto createDaySheetDto = new DaySheetDto("", createIncident.getDate(), false);
      daySheetDto = daySheetService.createDay(createDaySheetDto, createIncident.getUser().getUser_id());
    }

    DaySheet daySheet = daySheetService.convertDaySheetDtoToDaySheet(daySheetDto);

    Incident incident = convertDtoToEntity(createIncident);
    incident.setDaySheet(daySheet);

    return convertEntityToDto(incidentRepository.save(incident), null);
  }

  /**
   * Updates the details of an existing incident.
   *
   * @param updateIncident The incident DTO with updated details.
   * @return The updated incident DTO.
   * @throws IncidentNotFoundException If the incident to update is not found.
   */
  public IncidentDto updateIncident(IncidentDto updateIncident) throws IncidentNotFoundException {
    Incident incident = incidentRepository.findById(updateIncident.getId())
        .orElseThrow(() -> new IncidentNotFoundException(updateIncident.getId()));
    incident.setTitle(updateIncident.getTitle());
    incident.setDescription(updateIncident.getDescription());
    return convertEntityToDto(incidentRepository.save(incident), null);
  }

  /**
   * Deletes an incident by its ID.
   *
   * @param id The ID of the incident to delete.
   * @throws IncidentNotFoundException If the incident to delete is not found.
   */
  public void deleteIncident(Long id) throws IncidentNotFoundException {
    Incident incident = incidentRepository.findById(id)
            .orElseThrow(() -> new IncidentNotFoundException(id));
    incidentRepository.delete(incident);
  }

  /**
   * Retrieves all incidents, converting them to DTOs.
   *
   * @return A list of all incident DTOs.
   */
  public List<IncidentDto> getAll() {
    List<UserDto> userDtos = userService.getAllUsers();
    return incidentRepository.findAll()
            .stream().map(incident -> {
                UserDto user = userDtos.stream()
                        .filter(userFilter -> userFilter.getUser_id().equals(incident.getDaySheet().getOwner().getId()))
                        .findFirst().orElse(null);
                return convertEntityToDto(incident, user);
            }).toList();
  }

  /**
   * Converts an IncidentDto to an Incident entity.
   *
   * @param dto The incident DTO to convert.
   * @return The incident entity.
   */
  public Incident convertDtoToEntity(IncidentDto dto) {
    return new Incident(dto.getTitle(), dto.getDescription());
  }

  /**
   * Converts an Incident entity to an IncidentDto.
   *
   * @param entity  The incident entity to convert.
   * @param userDto The user DTO associated with the incident.
   * @return The incident DTO.
   */
  public IncidentDto convertEntityToDto(Incident entity, UserDto userDto) {
    return IncidentDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .date(entity.getDaySheet().getDate())
            .user(userDto)
            .build();
  }
}
