package ch.zhaw.pm4.compass.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;

import ch.zhaw.pm4.compass.backend.GsonExclusionStrategy;
import ch.zhaw.pm4.compass.backend.LocalDateDeserializer;
import ch.zhaw.pm4.compass.backend.LocalDateSerializer;
import ch.zhaw.pm4.compass.backend.LocalTimeDeserializer;
import ch.zhaw.pm4.compass.backend.LocalTimeSerializer;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.dto.IncidentDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.service.IncidentService;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration
public class IncidentControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	private WebApplicationContext controller;

	@MockBean
	private IncidentService incidentService;
	@MockBean
	private UserService userService;

	private Gson gson = new GsonBuilder().registerTypeAdapter(LocalTime.class, new LocalTimeDeserializer())
			.registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();


	private UserDto getUserDto() {
		return new UserDto("auth0|23sdfyl22ffowqpmclblrtkwerwsdff", "Test", "User", "test.user@stadtmuur.ch",
				UserRole.PARTICIPANT, false);
	}

	private IncidentDto getIncidentDto() {
		return new IncidentDto(1l, "Ausfall", "Teilnehmer kam nicht zur Arbeit", null, getUserDto());
	}

	@Before()
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testCreateIncident() throws Exception {
		// Arrange
		when(incidentService.createIncident(any(IncidentDto.class))).thenReturn(getIncidentDto());
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		// Act and Assert//
		mockMvc.perform(post("/incident").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getIncidentDto(), IncidentDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(getIncidentDto().getId()))
				.andExpect(jsonPath("$.title").value(getIncidentDto().getTitle()))
				.andExpect(jsonPath("$.description").value(getIncidentDto().getDescription()))
				.andExpect(jsonPath("$.user").value(getIncidentDto().getUser()));

		verify(incidentService, times(1)).createIncident(any(IncidentDto.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testUpdateIncident() throws Exception {
		// Arrange
		when(incidentService.updateIncident(any(IncidentDto.class))).thenReturn(getIncidentDto());
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		// Act and Assert//
		mockMvc.perform(put("/incident").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getIncidentDto(), IncidentDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(getIncidentDto().getId()))
				.andExpect(jsonPath("$.title").value(getIncidentDto().getTitle()))
				.andExpect(jsonPath("$.description").value(getIncidentDto().getDescription()))
				.andExpect(jsonPath("$.user").value(getIncidentDto().getUser()));

		verify(incidentService, times(1)).updateIncident(any(IncidentDto.class));

	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void testDeleteIncident() throws Exception {
		// Arrange
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);
		doNothing().when(incidentService).deleteIncident(any(Long.class));

		// Act and Assert//
		mockMvc.perform(delete("/incident/1").with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk());

		verify(incidentService, times(1)).deleteIncident(any(Long.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void getGetAll() throws Exception {
		// Arrange
		List<IncidentDto> incidentDtoList = new ArrayList<>();
		incidentDtoList.add(getIncidentDto());

		when(incidentService.getAll()).thenReturn(incidentDtoList);
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		// Act and Assert/
		String res = mockMvc.perform(get("/incident/getAll").with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		List<IncidentDto> resultIncidentDtoList = gson.fromJson(res, new TypeToken<List<IncidentDto>>() {
		}.getType());

		assertEquals(incidentDtoList.getFirst().getId(), resultIncidentDtoList.getFirst().getId());
		assertEquals(incidentDtoList.getFirst().getTitle(), resultIncidentDtoList.getFirst().getTitle());
		assertEquals(incidentDtoList.getFirst().getDescription(), resultIncidentDtoList.getFirst().getDescription());
		assertEquals(incidentDtoList.getFirst().getUser(), resultIncidentDtoList.getFirst().getUser());

		verify(incidentService, times(1)).getAll();
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingAdminOrSocialWorkerProtectedEndpointsAsParticipant_expectForbidden() throws Exception {
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.PARTICIPANT);

		mockMvc.perform(post("/incident").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getIncidentDto(), IncidentDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		mockMvc.perform(put("/incident").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getIncidentDto(), IncidentDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		mockMvc.perform(delete("/incident/1").contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		mockMvc.perform(get("/incident/getAll").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getIncidentDto(), IncidentDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		verify(userService, times(4)).getUserRole(any(String.class));
	}
}
