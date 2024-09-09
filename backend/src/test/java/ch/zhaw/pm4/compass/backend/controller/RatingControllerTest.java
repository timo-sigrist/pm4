package ch.zhaw.pm4.compass.backend.controller;

import ch.zhaw.pm4.compass.backend.*;
import ch.zhaw.pm4.compass.backend.exception.*;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.CreateRatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.service.RatingService;
import ch.zhaw.pm4.compass.backend.service.UserService;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RatingControllerTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	private WebApplicationContext controller;

	@MockBean
	private RatingService ratingService;

	private Gson gson = new GsonBuilder().registerTypeAdapter(LocalTime.class, new LocalTimeDeserializer())
			.registerTypeAdapter(LocalTime.class, new LocalTimeSerializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
			.registerTypeAdapter(LocalDate.class, new LocalDateSerializer()).create();

	private DaySheet daySheet;
	private DaySheetDto daySheetDto;

	private Category categoryGlobal;
	private Category categoryPersonal;
	private CategoryDto categoryGlobalDto;
	private CategoryDto categoryPersonalDto;

	private CreateRatingDto createRatingDtoOne;
	private RatingDto ratingDto;
	private String userId = "dasfdwssdio";
	private LocalUser participant = new LocalUser(userId, UserRole.PARTICIPANT);

	@BeforeEach
	public void setUpDtos() throws NotValidCategoryOwnerException {
		List<LocalUser> categoryOwners = Arrays.asList(this.participant);

		LocalDate now = LocalDate.now();
		daySheet = new DaySheet(1l, "", now, false);
		daySheet.setOwner(participant);
		daySheetDto = new DaySheetDto(1l, "", now, false);

		categoryGlobal = new Category("Unit Test", 0, 10, List.of());
		categoryGlobal.setId(1l);
		categoryGlobalDto = new CategoryDto(1l, "Unit Test", 0, 10);

		categoryPersonal = new Category("Integration Test", 0, 2, categoryOwners);
		categoryPersonal.setId(2l);
		categoryPersonalDto = new CategoryDto(2l, "Integration Test", 0, 2, null);

		createRatingDtoOne = new CreateRatingDto(categoryGlobal.getId(),4);
		ratingDto = new RatingDto(categoryGlobalDto,daySheetDto,4,RatingType.PARTICIPANT);
	}

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(controller).apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingCreateRatingsByDaySheetId_expectResult() throws Exception {
		List<CreateRatingDto> createRatingList = new ArrayList<>();
		createRatingList.add(createRatingDtoOne);
		when(ratingService.createRatingsByDaySheetId(any(Long.class), any(List.class), any(String.class))).thenReturn(List.of(ratingDto));

		String result = mockMvc.perform(post("/rating/createRatingsByDaySheetId/" + 1).contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(createRatingList)).with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		assertEquals("[{\"category\":{\"id\":1,\"name\":\"Unit Test\",\"minimumValue\":0,\"maximumValue\":10,\"categoryOwners\":[]},\"rating\":4,\"ratingRole\":\"PARTICIPANT\"}]",result);
	}
	@Test
	@WithMockUser(username = "testuser", roles = {})
	public void whenCallingCreateRatingsByDaySheetId_expectBadrequest() throws Exception {
		List<CreateRatingDto> createRatingList = new ArrayList<>();
		createRatingList.add(createRatingDtoOne);
		when(ratingService.createRatingsByDaySheetId(any(Long.class), any(List.class), any(String.class))).thenThrow(new RatingAlreadyExistsException(1L));

		mockMvc.perform(post("/rating/createRatingsByDaySheetId/" + 1).contentType(MediaType.APPLICATION_JSON)
				.content(this.gson.toJson(createRatingList)).with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(status().isBadRequest());

	}
}
