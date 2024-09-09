package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.RatingType;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.*;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.model.dto.*;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RatingServiceTest {
	@Mock
	private RatingRepository ratingRepository;
	@Mock
	private DaySheetRepository daySheetRepository;
	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private UserService userService;

	@Spy
	@InjectMocks
	private RatingService ratingService;

	private DaySheet daySheet;
	private DaySheetDto daySheetDto;

	private Category categoryGlobal;
	private Category categoryPersonal;
	private CategoryDto categoryGlobalDto;
	private CategoryDto categoryPersonalDto;

	private RatingDto ratingOneCategoryGlobalDto;
	private RatingDto ratingTwoCategoryGlobalDto;
	private RatingDto ratingOneCategoryPersonalDto;

	private ExtendedRatingDto extendedRatingOneCaregoryGlobalDto;
	private ExtendedRatingDto extendedRatingTwoCaregoryGlobalDto;
	private ExtendedRatingDto extendedRatingOneCaregoryPersonalDto;

	private CreateRatingDto createRatingOneCategoryGlobalDto;
	private CreateRatingDto createRatingTwoCategoryGlobalDto;
	private String userId;
	private LocalUser participant;

	@BeforeEach
	public void setup() throws NotValidCategoryOwnerException {
		MockitoAnnotations.openMocks(this);

		userId = "dasfdwssdio";
		participant = new LocalUser(userId, UserRole.PARTICIPANT);
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
		categoryPersonalDto = new CategoryDto(2l, "Integration Test", 0, 2);

		ratingOneCategoryGlobalDto = new RatingDto();
		ratingTwoCategoryGlobalDto = new RatingDto();
		ratingOneCategoryPersonalDto = new RatingDto();

		ratingOneCategoryGlobalDto.setCategory(categoryGlobalDto);
		ratingOneCategoryGlobalDto.setDaySheet(daySheetDto);
		ratingOneCategoryGlobalDto.setRating(3);
		ratingOneCategoryGlobalDto.setRatingRole(RatingType.PARTICIPANT);

		ratingTwoCategoryGlobalDto.setCategory(categoryGlobalDto);
		ratingTwoCategoryGlobalDto.setDaySheet(daySheetDto);
		ratingTwoCategoryGlobalDto.setRating(6);
		ratingTwoCategoryGlobalDto.setRatingRole(RatingType.PARTICIPANT);

		ratingOneCategoryPersonalDto.setCategory(categoryPersonalDto);
		ratingOneCategoryPersonalDto.setDaySheet(daySheetDto);
		ratingOneCategoryPersonalDto.setRating(2);
		ratingOneCategoryPersonalDto.setRatingRole(RatingType.PARTICIPANT);

		extendedRatingOneCaregoryGlobalDto = new ExtendedRatingDto();
		extendedRatingTwoCaregoryGlobalDto = new ExtendedRatingDto();
		extendedRatingOneCaregoryPersonalDto = new ExtendedRatingDto();

		extendedRatingOneCaregoryGlobalDto.setRating(ratingOneCategoryGlobalDto);
		extendedRatingOneCaregoryGlobalDto.setDate(now);
		extendedRatingOneCaregoryGlobalDto.setParticipantName("Chester");

		extendedRatingTwoCaregoryGlobalDto.setRating(ratingTwoCategoryGlobalDto);
		extendedRatingTwoCaregoryGlobalDto.setDate(now);
		extendedRatingTwoCaregoryGlobalDto.setParticipantName("Tester McTester");

		extendedRatingOneCaregoryPersonalDto.setRating(ratingOneCategoryPersonalDto);
		extendedRatingOneCaregoryPersonalDto.setDate(now);
		extendedRatingOneCaregoryPersonalDto.setParticipantName("Chester");

		createRatingOneCategoryGlobalDto = new CreateRatingDto();
		createRatingTwoCategoryGlobalDto = new CreateRatingDto();

		createRatingOneCategoryGlobalDto.setCategoryId(categoryGlobalDto.getId());
		createRatingOneCategoryGlobalDto.setRating(3);

		createRatingTwoCategoryGlobalDto.setCategoryId(categoryGlobalDto.getId());
		createRatingTwoCategoryGlobalDto.setRating(6);
	}

	@Test
	void whenCreateRatingsByDaySheetId_expectCorrectDaySheet() throws CategoryNotFoundException, RatingAlreadyExistsException, DaySheetNotFoundException {
		List<RatingDto> ratingList = new ArrayList<>();
		ratingList.add(ratingOneCategoryGlobalDto);
		ratingList.add(ratingTwoCategoryGlobalDto);
		List<CreateRatingDto> createRatingList = new ArrayList<>();
		createRatingList.add(createRatingOneCategoryGlobalDto);
		createRatingList.add(createRatingTwoCategoryGlobalDto);
		Rating ratingOne = ratingService.convertDtoToEntity(ratingOneCategoryGlobalDto);
		ratingOne.setCategory(categoryGlobal);
		ratingOne.setDaySheet(daySheet);
		Rating ratingTwo = ratingService.convertDtoToEntity(ratingTwoCategoryGlobalDto);
		ratingTwo.setCategory(categoryGlobal);
		ratingTwo.setDaySheet(daySheet);
		List<Rating> existingRatings = new ArrayList<Rating>();
		existingRatings.add(ratingOne);
		existingRatings.add(ratingTwo);

		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.PARTICIPANT);
		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));
		when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(categoryPersonal));
		when(ratingRepository.saveAll(any(List.class))).thenReturn(existingRatings);
		//when(ratingRepository.saveAll(existingRatings).stream().map(any()).toList()).thenReturn( ratingList);

		List<RatingDto> resultRatings = ratingService.createRatingsByDaySheetId(daySheet.getId(), createRatingList, userId);

		assertEquals(createRatingList.get(0).getRating(),resultRatings.get(0).getRating());
	}
	@Test
	void whenCreateRatingsByDaySheetId_expectRatingAlreadyExistsException() throws CategoryNotFoundException, RatingAlreadyExistsException, DaySheetNotFoundException {
		List<RatingDto> ratingList = new ArrayList<>();
		ratingList.add(ratingOneCategoryGlobalDto);
		ratingList.add(ratingTwoCategoryGlobalDto);
		List<CreateRatingDto> createRatingList = new ArrayList<>();
		createRatingList.add(createRatingOneCategoryGlobalDto);
		createRatingList.add(createRatingTwoCategoryGlobalDto);
		Rating ratingOne = ratingService.convertDtoToEntity(ratingOneCategoryGlobalDto);
		ratingOne.setCategory(categoryGlobal);
		List<Rating> existingRatings = new ArrayList<Rating>();
		existingRatings.add(ratingOne);

		daySheet.setMoodRatings(existingRatings);
		when(userService.getUserRole(any(String.class))).thenReturn(UserRole.PARTICIPANT);
		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));
		when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(categoryGlobal));
		//when(ratingRepository.saveAll(any(List.class))).thenReturn(ratingList);


		assertThrows(RatingAlreadyExistsException.class,() ->ratingService.createRatingsByDaySheetId(daySheet.getId(), createRatingList, userId));


	}
}
