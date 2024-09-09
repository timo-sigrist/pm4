package ch.zhaw.pm4.compass.backend.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import ch.zhaw.pm4.compass.backend.model.dto.AuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.CreateAuthZeroUserDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext controller;
	@MockBean
	private UserService userService;

	private Gson gson = new GsonBuilder().registerTypeAdapter(LocalTime.class, new LocalTimeDeserializer())
			.registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();

	private UserDto getUserDto() {
		return new UserDto("auth0|23sdfyl22ffowqpmclblrtkwerwsdff", "Test", "User", "test.user@stadtmuur.ch", null,
				UserRole.PARTICIPANT);
	}

	private CreateAuthZeroUserDto getCreateAuthZeroUserDto() {
		return new CreateAuthZeroUserDto(getUserDto().getUser_id(), getUserDto().getEmail(),
				getUserDto().getGiven_name(), getUserDto().getFamily_name(), getUserDto().getRole(), "Swordfish");
	}

	private AuthZeroUserDto getAuthZeroUserDto() {
		return getCreateAuthZeroUserDto();
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testCreateUser() throws Exception {
		// Arrange
		when(userService.createUser(any(CreateAuthZeroUserDto.class))).thenReturn(getUserDto());

		// Act
		mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON)
				.content((new Gson()).toJson(getCreateAuthZeroUserDto(), CreateAuthZeroUserDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.user_id").value(getCreateAuthZeroUserDto().getUser_id()))
				.andExpect(jsonPath("$.email").value(getCreateAuthZeroUserDto().getEmail()))
				.andExpect(jsonPath("$.given_name").value(getCreateAuthZeroUserDto().getGiven_name()))
				.andExpect(jsonPath("$.family_name").value(getCreateAuthZeroUserDto().getFamily_name()))
				.andExpect(jsonPath("$.role").value(getCreateAuthZeroUserDto().getRole().toString()))
				.andExpect(jsonPath("$.deleted").value(getCreateAuthZeroUserDto().getBlocked()));

		verify(userService, times(1)).createUser(any(CreateAuthZeroUserDto.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testDeleteUser() throws Exception {
		when(userService.deleteUser(anyString())).thenReturn(getUserDto());

		// Act
		mockMvc.perform(
				delete("/user/" + getCreateAuthZeroUserDto().getUser_id()).contentType(MediaType.APPLICATION_JSON)
						.content((new Gson()).toJson(getCreateAuthZeroUserDto(), CreateAuthZeroUserDto.class))
						.with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk());

		verify(userService, times(1)).deleteUser(anyString());
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testUpdateUser() throws Exception {
		// Arrange
		when(userService.updateUser(anyString(), any(AuthZeroUserDto.class))).thenReturn(getUserDto());

		// Act
		mockMvc.perform(
				put("/user/update/" + getCreateAuthZeroUserDto().getUser_id()).contentType(MediaType.APPLICATION_JSON)
						.content((new Gson()).toJson(getAuthZeroUserDto(), AuthZeroUserDto.class))
						.with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.email").value(getAuthZeroUserDto().getEmail()))
				.andExpect(jsonPath("$.given_name").value(getAuthZeroUserDto().getGiven_name()))
				.andExpect(jsonPath("$.family_name").value(getAuthZeroUserDto().getFamily_name()))
				.andExpect(jsonPath("$.role").value(getAuthZeroUserDto().getRole().toString()))
				.andExpect(jsonPath("$.deleted").value(getAuthZeroUserDto().getBlocked()));

		verify(userService, times(1)).updateUser(anyString(), any(AuthZeroUserDto.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testRestoreUser() throws Exception {
		// Arrange
		when(userService.restoreUser(anyString())).thenReturn(getUserDto());

		// Act
		mockMvc.perform(
				put("/user/restore/" + getUserDto().getUser_id()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.deleted").value(getAuthZeroUserDto().getBlocked()));

		verify(userService, times(1)).restoreUser(anyString());
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGetById() throws Exception {
		// Arrange
		when(userService.getUserById(anyString())).thenReturn(getUserDto());

		// Act
		mockMvc.perform(
				get("/user/getById/" + getUserDto().getUser_id()).with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.email").value(getAuthZeroUserDto().getEmail()))
				.andExpect(jsonPath("$.given_name").value(getAuthZeroUserDto().getGiven_name()))
				.andExpect(jsonPath("$.family_name").value(getAuthZeroUserDto().getFamily_name()))
				.andExpect(jsonPath("$.role").value(getAuthZeroUserDto().getRole().toString()))
				.andExpect(jsonPath("$.deleted").value(getAuthZeroUserDto().getBlocked()));

		verify(userService, times(1)).getUserById(anyString());
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testGetAllUsers() throws Exception {
		List<UserDto> userDtoList = new ArrayList<>();
		userDtoList.add(getUserDto());

		when(userService.getAllUsers()).thenReturn(userDtoList);

		String res = mockMvc.perform(get("/user/getAllUsers").with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(userDtoList, gson.fromJson(res, new TypeToken<List<UserDto>>() {
		}.getType()));

		verify(userService, times(1)).getAllUsers();
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	void testAllParticipants() throws Exception {
		List<UserDto> userDtoList = new ArrayList<>();
		userDtoList.add(getUserDto());

		when(userService.getAllParticipants()).thenReturn(userDtoList);

		String res = mockMvc.perform(get("/user/getAllParticipants").with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(userDtoList, gson.fromJson(res, new TypeToken<List<UserDto>>() {
		}.getType()));

		verify(userService, times(1)).getAllParticipants();
	}
}
