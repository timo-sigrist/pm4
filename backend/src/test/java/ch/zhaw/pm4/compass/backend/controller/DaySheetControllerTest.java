package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.IncidentDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateDaySheetDayNotesDto;
import ch.zhaw.pm4.compass.backend.service.DaySheetService;
import ch.zhaw.pm4.compass.backend.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class DaySheetControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	private WebApplicationContext controller;

	@MockBean
	private DaySheetService daySheetService;
	@MockBean
	private UserService userService;
	@MockBean
	@SuppressWarnings("unused")
	private JwtDecoder jwtDecoder;

	private LocalDate dateNow = LocalDate.now();
	private String reportText = "Testdate";

	private DaySheetDto getDaySheetDto() {
		return new DaySheetDto(1l, reportText, dateNow, false);
	}

	private DaySheetDto getUpdateDaySheet() {
		return new DaySheetDto(1l, reportText + "1", dateNow.plusDays(1), false);
	}

	private DaySheet getDaySheet() {
		LocalUser user = new LocalUser("auth0|2svwqwqwvp2qadcjl3409wdsu340fds3eu", UserRole.PARTICIPANT);
		return new DaySheet(1l, user, reportText, dateNow, false, new ArrayList<Timestamp>());
	}

	@Before()
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testCreateDay() throws Exception {
		// Arrange

		// CreateDaySheetDto day = getCreateDaySheet();
		DaySheetDto getDay = getDaySheetDto();

		when(daySheetService.createDay(any(DaySheetDto.class), any(String.class))).thenReturn(getDay);

		// Act and Assert//
		mockMvc.perform(post("/daysheet").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_notes\": \"" + reportText + "\", \"date\": \"" + dateNow.toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1l)).andExpect(jsonPath("$.day_notes").value(reportText))
				.andExpect(jsonPath("$.date").value(dateNow.toString()));

		verify(daySheetService, times(1)).createDay(any(DaySheetDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testCreateDaySheetWithEmptyBody() throws Exception {
		// Arrange
		DaySheetDto getDay = getDaySheetDto();

		when(daySheetService.createDay(any(DaySheetDto.class), any(String.class))).thenReturn(getDay);
		mockMvc.perform(post("/daysheet").contentType(MediaType.APPLICATION_JSON).content("{}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		verify(daySheetService, times(0)).createDay(any(DaySheetDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void testDayAlreadyExists() throws Exception {
		// Arrange
		when(daySheetService.createDay(any(DaySheetDto.class), any(String.class))).thenReturn(null);

		mockMvc.perform(post("/daysheet").contentType(MediaType.APPLICATION_JSON)
				.content("{\"day_notes\": \"" + reportText + "\", \"date\": \"" + dateNow.toString() + "\"}")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isConflict())
				.andExpect(jsonPath("$").doesNotExist());

		verify(daySheetService, times(1)).createDay(any(DaySheetDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testUpdateDayNotes() throws Exception {
		// Arrange
		DaySheetDto updateDay = getUpdateDaySheet();
		updateDay.setConfirmed(false);
		when(daySheetService.updateDayNotes(any(UpdateDaySheetDayNotesDto.class), any(String.class)))
				.thenReturn(updateDay);
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		// Act
		mockMvc.perform(put("/daysheet/updateDayNotes").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\": 1,\"day_notes\": \"" + reportText + "1" + "\", \"date\": \"" + dateNow.toString()
						+ "\", \"confirmed\": \"true\" }")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1l))
				.andExpect(jsonPath("$.day_notes").value(updateDay.getDay_notes()))
				.andExpect(jsonPath("$.date").value(updateDay.getDate().toString()))
				.andExpect(jsonPath("$.confirmed").value(updateDay.getConfirmed().toString()));

		verify(daySheetService, times(1)).updateDayNotes(any(UpdateDaySheetDayNotesDto.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testConfirm() throws Exception {
		// Arrange

		DaySheetDto updateDay = getUpdateDaySheet();
		updateDay.setConfirmed(true);
		when(daySheetService.updateConfirmed(any(Long.class), any(Boolean.class), any(String.class))).thenReturn(updateDay);
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		// Act
		mockMvc.perform(put("/daysheet/confirm/1").contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1l))
				.andExpect(jsonPath("$.confirmed").value(updateDay.getConfirmed().toString()));

		verify(daySheetService, times(1)).updateConfirmed(any(Long.class), any(Boolean.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testRevoke() throws Exception {
		// Arrange

		DaySheetDto updateDay = getUpdateDaySheet();
		updateDay.setConfirmed(false);
		when(daySheetService.updateConfirmed(any(Long.class), any(Boolean.class), any(String.class))).thenReturn(updateDay);
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		// Act
		mockMvc.perform(put("/daysheet/revoke/1").contentType(MediaType.APPLICATION_JSON)
						.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1l))
				.andExpect(jsonPath("$.confirmed").value(updateDay.getConfirmed().toString()));

		verify(daySheetService, times(1)).updateConfirmed(any(Long.class), any(Boolean.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGetDayByDate() throws Exception {
		// Arrange
		DaySheetDto getDay = getDaySheetDto();
		when(daySheetService.getDaySheetByDate(any(LocalDate.class), any(String.class))).thenReturn(getDay);

		mockMvc.perform(get("/daysheet/getByDate/" + getDay.getDate().toString())
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1l)).andExpect(jsonPath("$.day_notes").value(getDay.getDay_notes()))
				.andExpect(jsonPath("$.date").value(getDay.getDate().toString()));

		verify(daySheetService, times(1)).getDaySheetByDate(any(LocalDate.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGetDayById() throws Exception {
		// Arrange
		DaySheetDto getDay = getDaySheetDto();
		when(daySheetService.getDaySheetByIdAndUserId(any(Long.class), any(String.class))).thenReturn(getDay);

		mockMvc.perform(get("/daysheet/getById/" + getDay.getId()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1l))
				.andExpect(jsonPath("$.day_notes").value(getDay.getDay_notes()))
				.andExpect(jsonPath("$.date").value(getDay.getDate().toString()));

		verify(daySheetService, times(1)).getDaySheetByIdAndUserId(any(Long.class), any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGetAllByParticipantByMonth() throws Exception {
		List<DaySheetDto> daySheets = new ArrayList<>();
		DaySheetDto day1 = getDaySheetDto();

		DaySheetDto day2 = getDaySheetDto();
		day2.setId(2l);
		day2.setDate(dateNow.plusDays(1));
		daySheets.add(day1);
		daySheets.add(day2);
		when(daySheetService.getAllDaySheetByUserAndMonth(any(String.class), any(YearMonth.class), any(String.class)))
				.thenReturn(daySheets);
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		String res = mockMvc.perform(
				get("/daysheet/getAllByParticipantAndMonth/" + getDaySheet().getOwner().getId() + "/" + YearMonth.now())
						.contentType(MediaType.APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals("[{\"id\":1,\"date\":\"" + dateNow.toString()
				+ "\",\"day_notes\":\"Testdate\",\"confirmed\":false,\"timestamps\":null,\"moodRatings\":null,\"incidents\":null,\"timeSum\":0,\"owner\":null},{\"id\":2,\"date\":\""
				+ dateNow.plusDays(1).toString()
				+ "\",\"day_notes\":\"Testdate\",\"confirmed\":false,\"timestamps\":null,\"moodRatings\":null,\"incidents\":null,\"timeSum\":0,\"owner\":null}]",
				res);
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGtDaySheetByParticipantAndDate() throws Exception {
		DaySheetDto getDay = getDaySheetDto();
		when(daySheetService.getDaySheetByUserAndDate(any(String.class), any(LocalDate.class),any(String.class)))
				.thenReturn(getDay);
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		String res = mockMvc.perform(
						get("/daysheet/getByParticipantAndDate/" + getDaySheet().getOwner().getId() + "/" + getDaySheet().getDate().toString())
								.contentType(MediaType.APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals("{\"id\":1,\"date\":\"" + dateNow.toString()
						+ "\",\"day_notes\":\"Testdate\",\"confirmed\":false,\"timestamps\":null,\"moodRatings\":null,\"incidents\":null,\"timeSum\":0,\"owner\":null}",
				res);
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGtDaySheetByParticipantAndDateNotExisting() throws Exception {
		DaySheetDto getDay = getDaySheetDto();
		when(daySheetService.getDaySheetByUserAndDate(any(String.class), any(LocalDate.class),any(String.class)))
				.thenReturn(null);
		mockMvc.perform(
						get("/daysheet/getByParticipantAndDate/" + getDaySheet().getOwner().getId() + "/" + getDaySheet().getDate().toString())
								.contentType(MediaType.APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingAdminOrSocialWorkerProtectedEndpointsAsParticipant_expectForbidden() throws Exception {
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.PARTICIPANT);

		mockMvc.perform(get("/daysheet/getAllNotConfirmed").contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		mockMvc.perform(get("/daysheet/getAllByMonth/" + YearMonth.now()).contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		mockMvc.perform(get("/daysheet/getAllByParticipantAndMonth/" + getDaySheet().getOwner().getId() + "/" + YearMonth.now()).contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		mockMvc.perform(put("/daysheet/updateDayNotes").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\": 1,\"day_notes\": \"" + reportText + "1" + "\", \"date\": \"" + dateNow.toString()
						+ "\", \"confirmed\": \"true\" }")
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		mockMvc.perform(put("/daysheet/confirm/1").contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		mockMvc.perform(put("/daysheet/revoke/1").contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		verify(userService, times(6)).getUserRole(any(String.class));
	}
}
