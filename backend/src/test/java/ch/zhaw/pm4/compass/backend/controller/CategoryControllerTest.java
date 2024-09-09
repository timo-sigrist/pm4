package ch.zhaw.pm4.compass.backend.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import ch.zhaw.pm4.compass.backend.model.dto.*;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;

import ch.zhaw.pm4.compass.backend.GsonExclusionStrategy;
import ch.zhaw.pm4.compass.backend.LocalDateDeserializer;
import ch.zhaw.pm4.compass.backend.LocalDateSerializer;
import ch.zhaw.pm4.compass.backend.LocalTimeDeserializer;
import ch.zhaw.pm4.compass.backend.LocalTimeSerializer;
import ch.zhaw.pm4.compass.backend.RatingType;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.GlobalCategoryException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.exception.UserIsNotParticipantException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.service.CategoryService;
import ch.zhaw.pm4.compass.backend.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration
public class CategoryControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	private WebApplicationContext controller;

	@MockBean
	private UserService userService;

	@MockBean
	private CategoryService categoryService;

	private Gson gson = new GsonBuilder().registerTypeAdapter(LocalTime.class, new LocalTimeDeserializer())
			.registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();

	private long categoryId = 1l;
	private String categoryName = "Stress";
	private int minValue = 1;
	private int maxValue = 10;
	private UserDto userDto1 = new UserDto("id1", "Max", "Mustermann", "m.m@musti.ch", UserRole.PARTICIPANT, false);
	private UserDto userDto2 = new UserDto("id1", "Max", "Mustermann", "m.m@musti.ch", UserRole.PARTICIPANT, false);
	private List<UserDto> ownersDtoFull = List.of(userDto1, userDto2);

	private CategoryDto getGlobalCategoryDto() {
		return new CategoryDto(categoryId, categoryName, minValue, maxValue);
	}

	private CategoryDto getPersonalCategoryDto() {
		return new CategoryDto(categoryId + 1, categoryName + "2", minValue, maxValue, ownersDtoFull);
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingCreateEndpointWithGlobalCategory_expectCategory() throws Exception {
		CategoryDto category = getGlobalCategoryDto();
		when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(category);
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		mockMvc.perform(post("/category").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getGlobalCategoryDto(), CategoryDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(category.getId()))
				.andExpect(jsonPath("$.minimumValue").value(category.getMinimumValue()))
				.andExpect(jsonPath("$.maximumValue").value(category.getMaximumValue()))
				.andExpect(jsonPath("$.name").value(category.getName()))
				.andExpect(jsonPath("$.categoryOwners").value(category.getCategoryOwners()));

		verify(categoryService, times(1)).createCategory(any(CategoryDto.class));
		verify(userService, times(1)).getUserRole(any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingAdminProtectedEndpointsAsNotAdmin_expectForbidden() throws Exception {
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.SOCIAL_WORKER);

		mockMvc.perform(post("/category").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getGlobalCategoryDto(), CategoryDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		mockMvc.perform(post("/category/linkUsersToExistingCategory").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getPersonalCategoryDto(), CategoryDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isForbidden());

		verify(userService, times(2)).getUserRole(any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingCreateEndpointWithBadRequest_expectBadRequest() throws Exception {
		Category category = new Category(categoryName, minValue, maxValue, List.of());

		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);
		when(categoryService.createCategory(any(CategoryDto.class)))
				.thenThrow(new CategoryAlreadyExistsException(category));

		mockMvc.perform(post("/category").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getGlobalCategoryDto(), CategoryDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		when(categoryService.createCategory(any(CategoryDto.class))).thenThrow(new NotValidCategoryOwnerException());
		mockMvc.perform(post("/category").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getGlobalCategoryDto(), CategoryDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		verify(userService, times(2)).getUserRole(any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingGetEndpoint_expectList() throws Exception {
		List<CategoryDto> categoryDtoList = List.of(getGlobalCategoryDto());

		when(categoryService.getAllCategories()).thenReturn(categoryDtoList);

		String res = mockMvc.perform(get("/category").with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		System.out.print(res);
		assertEquals(categoryDtoList, gson.fromJson(res, new TypeToken<List<CategoryDto>>() {
		}.getType()));

		verify(categoryService, times(1)).getAllCategories();
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingLinkEndpointWithBadRequest_expectBadRequest() throws Exception {
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		when(categoryService.linkUsersToExistingCategory(any(CategoryDto.class)))
				.thenThrow(new NotValidCategoryOwnerException());
		mockMvc.perform(post("/category/linkUsersToExistingCategory").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getPersonalCategoryDto(), CategoryDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		when(categoryService.linkUsersToExistingCategory(any(CategoryDto.class)))
				.thenThrow(new NoSuchElementException());
		mockMvc.perform(post("/category/linkUsersToExistingCategory").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getPersonalCategoryDto(), CategoryDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		when(categoryService.linkUsersToExistingCategory(any(CategoryDto.class)))
				.thenThrow(new GlobalCategoryException());
		mockMvc.perform(post("/category/linkUsersToExistingCategory").contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(getGlobalCategoryDto(), CategoryDto.class))
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		verify(userService, times(3)).getUserRole(any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingLinkEndpoint_expectCategory() throws Exception {
		CategoryDto category = getPersonalCategoryDto();
		String owners = gson.toJson(category.getCategoryOwners(), new TypeToken<List<ParticipantDto>>() {
		}.getType());
		when(categoryService.linkUsersToExistingCategory(any(CategoryDto.class))).thenReturn(category);
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.ADMIN);

		MvcResult result = mockMvc
				.perform(post("/category/linkUsersToExistingCategory").contentType(MediaType.APPLICATION_JSON)
						.content(this.gson.toJson(getPersonalCategoryDto(), CategoryDto.class))
						.with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(category.getId()))
				.andExpect(jsonPath("$.minimumValue").value(category.getMinimumValue()))
				.andExpect(jsonPath("$.maximumValue").value(category.getMaximumValue()))
				.andExpect(jsonPath("$.name").value(category.getName())).andReturn();
		String response = result.getResponse().getContentAsString();
		assertEquals(JsonPath.parse(response).read("$.categoryOwners").toString(), owners);

		verify(categoryService, times(1)).linkUsersToExistingCategory(any(CategoryDto.class));
		verify(userService, times(1)).getUserRole(any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingGetListByUserIdEndpointWithBadRequest_expectBadRequest() throws Exception {
		when(categoryService.getCategoryListByUserId(any(String.class))).thenThrow(new NoSuchElementException());
		mockMvc.perform(get("/category/getCategoryListByUserId/tester").contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		when(categoryService.getCategoryListByUserId(any(String.class))).thenThrow(new UserIsNotParticipantException());
		mockMvc.perform(get("/category/getCategoryListByUserId/Mctester").contentType(MediaType.APPLICATION_JSON)
				.with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

		verify(categoryService, times(2)).getCategoryListByUserId(any(String.class));
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingGetListByUserIdEndpointWithGoodRequest_expectOk() throws Exception {
		List<CategoryDto> categoryDtoList = new ArrayList<>();
		categoryDtoList.add(getGlobalCategoryDto());
		categoryDtoList.add(getPersonalCategoryDto());

		when(categoryService.getCategoryListByUserId(any(String.class))).thenReturn(categoryDtoList);

		String res = mockMvc
				.perform(get("/category/getCategoryListByUserId/McTesto")
						.with(SecurityMockMvcRequestPostProcessors.csrf()))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		System.out.print(res);
		assertEquals(categoryDtoList.toString(), gson.fromJson(res, new TypeToken<List<CategoryDto>>() {
		}.getType()).toString());

		verify(categoryService, times(1)).getCategoryListByUserId(any(String.class));
	}
}
