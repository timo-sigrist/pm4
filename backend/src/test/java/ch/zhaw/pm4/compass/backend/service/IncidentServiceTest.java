package ch.zhaw.pm4.compass.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.DaySheetNotFoundException;
import ch.zhaw.pm4.compass.backend.exception.IncidentNotFoundException;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Incident;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.IncidentDto;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.IncidentRepository;

@Service
public class IncidentServiceTest {
  @Mock
  private IncidentRepository incidentRepository;
  @Mock
  private DaySheetService daySheetService;
  @Mock
  private UserService userService;
  @InjectMocks
  private IncidentService incidentService;

  private LocalDate dateNow = LocalDate.now();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    MockitoAnnotations.openMocks(daySheetService);
    MockitoAnnotations.openMocks(incidentRepository);
  }

  private UserDto getUserDto() {
    return new UserDto("auth0|23sdfyl22ffowqpmclblrtkwerwsdff", "Test", "User", "test.user@stadtmuur.ch",
        UserRole.PARTICIPANT, false);
  }

  private DaySheetDto getDaySheetDto() {
    return new DaySheetDto(1l, "Daysheet 1", dateNow, false, new ArrayList<TimestampDto>());
  }

  private DaySheet getDaySheet() {
    return new DaySheet(1l, "", dateNow);
  }

  private IncidentDto getIncidentDto() {
    return new IncidentDto(1l, "Ausfall", "Teilnehmer kam nicht zur Arbeit", dateNow, getUserDto());
  }

  private Incident getIncident() {
    return new Incident(1l, getIncidentDto().getTitle(), getIncidentDto().getDescription(), getDaySheet());
  }

  @Test
  public void TestCreateIncident() throws DaySheetNotFoundException {
    when(daySheetService.getDaySheetByDate(any(LocalDate.class), any(String.class))).thenReturn(getDaySheetDto());
    when(incidentRepository.save(any(Incident.class))).thenReturn(getIncident());

    IncidentDto resultIncidentDto = incidentService.createIncident(getIncidentDto());
    assertEquals(getIncidentDto().getId(), resultIncidentDto.getId());
    assertEquals(getIncidentDto().getDate(), resultIncidentDto.getDate());
    assertEquals(getIncidentDto().getTitle(), resultIncidentDto.getTitle());
    assertEquals(getIncidentDto().getDescription(), resultIncidentDto.getDescription());
  }

  @Test
  public void TestupdateIncident() throws IncidentNotFoundException {
    when(incidentRepository.findById(any(Long.class))).thenReturn(Optional.of(getIncident()));
    when(incidentRepository.save(any(Incident.class))).thenReturn(getIncident());

    IncidentDto resultIncidentDto = incidentService.updateIncident(getIncidentDto());
    assertEquals(getIncidentDto().getTitle(), resultIncidentDto.getTitle());
    assertEquals(getIncidentDto().getDescription(), resultIncidentDto.getDescription());
  }

  @Test
  public void TestDeleteIncident() throws IncidentNotFoundException {
    when(incidentRepository.findById(any(Long.class))).thenReturn(Optional.of(getIncident()));
    incidentService.deleteIncident(getIncident().getId());
    verify(incidentRepository, times(1)).delete(any(Incident.class));
  }

  @Test
  public void TestGetAllAdmin() {
    UserDto user = getUserDto();
    user.setRole(UserRole.ADMIN);
    user.setUser_id("auth0|23sdfyl22ffowqpmclbdfffffffrwsdff");

    TestAll(user);
  }

  @Test
  public void TestGetAllParticipant() {
    UserDto user = getUserDto();
    user.setRole(UserRole.PARTICIPANT);

    TestAll(user);
  }

  private void TestAll(UserDto user) {
    ArrayList<UserDto> userDtoList = new ArrayList<>();
    ArrayList<Incident> incidents = new ArrayList<>();
    incidents.add(getIncident());

    when(userService.getUserById(any(String.class))).thenReturn(user);
    when(userService.getAllUsers()).thenReturn(userDtoList);
    when(incidentRepository.findAll()).thenReturn(incidents);
    when(incidentRepository.findAllByDaySheet_Owner_Id(user.getUser_id())).thenReturn(incidents);

        List<IncidentDto> incidentDtos = incidentService.getAll();
        assertEquals(getIncidentDto().getTitle(), incidentDtos.getFirst().getTitle());
        assertEquals(getIncidentDto().getDescription(), incidentDtos.getFirst().getDescription());
    }

  @Test
  public void TestConvertDtoToEntity() {
    Incident resultIncident = incidentService.convertDtoToEntity(getIncidentDto());
    assertEquals(resultIncident.getTitle(), getIncidentDto().getTitle());
  }

  @Test
  public void TestConvertEntityToDto() {
    IncidentDto resultIncident = incidentService.convertEntityToDto(getIncident(), getUserDto());
    assertEquals(resultIncident.getId(), getIncident().getId());
    assertEquals(resultIncident.getTitle(), getIncident().getTitle());
    assertEquals(resultIncident.getDescription(), getIncident().getDescription());
    assertEquals(resultIncident.getDate(), getIncident().getDaySheet().getDate());
    assertEquals(resultIncident.getUser(), getUserDto());
  }
}
