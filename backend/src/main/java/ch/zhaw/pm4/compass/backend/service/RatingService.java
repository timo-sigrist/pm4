package ch.zhaw.pm4.compass.backend.service;

import java.util.*;
import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.*;
import ch.zhaw.pm4.compass.backend.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.RatingType;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Rating;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.RatingRepository;

/**
 * Service class for managing ratings within the system.
 * This service handles the creation, retrieval, and validation of ratings for categories within day sheets,
 * adhering to specific business rules and data integrity requirements.
 *
 * Using {@link RatingRepository} for persistence operations.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Service
public class RatingService {
	@Autowired
	private RatingRepository ratingRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private DaySheetRepository daySheetRepository;
	@Autowired
	private UserService userService;

	/**
	 * Creates multiple ratings for a day sheet. The ratings are created based on the provided DTOs.
	 * @param daySheetId The ID of the day sheet where the ratings will be recorded.
	 * @param ratings The list of rating DTOs to create.
	 * @param userId The ID of the user creating the ratings.
	 * @return A list of created rating DTOs.
	 * @throws DaySheetNotFoundException If the day sheet does not exist.
	 * @throws CategoryNotFoundException If any of the categories specified do not exist.
	 */
	public List<RatingDto> createRatingsByDaySheetId(Long daySheetId, List<CreateRatingDto> ratings, String userId) throws DaySheetNotFoundException, CategoryNotFoundException, RatingAlreadyExistsException {
		UserRole userRole = userService.getUserRole(userId);
		DaySheet daySheet = daySheetRepository.findById(daySheetId).orElseThrow(() -> new DaySheetNotFoundException(daySheetId));

		List<Rating> ratingEntities = new ArrayList<>();
		List<Rating> existingRatings = daySheet.getMoodRatings();

		for (CreateRatingDto ratingDto : ratings) {
			Category category = categoryRepository.findById(ratingDto.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException(ratingDto.getCategoryId()));
			RatingType ratingType = RatingType.PARTICIPANT;

			if (userRole == UserRole.SOCIAL_WORKER || userRole == UserRole.ADMIN) {
				ratingType = RatingType.SOCIAL_WORKER;
			}

			for (Rating existingRating : existingRatings) {
				if (existingRating.getCategory().getId().equals(category.getId()) && existingRating.getRatingRole() == ratingType) {
					throw new RatingAlreadyExistsException(category.getId());
				}
			}

			Rating rating = new Rating(ratingDto.getRating(), ratingType);
			rating.setCategory(category);
			rating.setDaySheet(daySheet);

			ratingEntities.add(rating);
		}

		return ratingRepository.saveAll(ratingEntities).stream().map(this::convertEntityToDto).toList();
	}

	/**
	 * Converts a {@link RatingDto} to a {@link Rating} entity. This method is crucial for persisting
	 * rating information obtained from the API into the database.
	 *
	 * @param dto The {@link RatingDto} object containing the details to be converted into an entity.
	 * @return A {@link Rating} entity with values set from the {@link RatingDto}.
	 */
	Rating convertDtoToEntity(RatingDto dto) {
		return new Rating(dto.getRating(), dto.getRatingRole());
	}

	/**
	 * Converts a {@link Rating} entity back to a {@link RatingDto}. This conversion includes
	 * assembling related data such as the category and day sheet details into the DTO for comprehensive data transfer.
   *
	 * @param entity The {@link Rating} entity to be converted.
	 * @return A {@link RatingDto} containing the details from the entity and its related data.
	 */
	RatingDto convertEntityToDto(Rating entity) {
		Category ratingCategory = entity.getCategory();
		CategoryDto categoryDto = new CategoryDto(ratingCategory.getId(), ratingCategory.getName(),
				ratingCategory.getMinimumValue(), ratingCategory.getMaximumValue());
		DaySheet daySheet = entity.getDaySheet();
		DaySheetDto daySheetDto = new DaySheetDto(daySheet.getId(), daySheet.getDayNotes(), daySheet.getDate(),
				daySheet.getConfirmed());
		RatingDto dto = new RatingDto(categoryDto, daySheetDto, entity.getRating(), entity.getRatingRole());
		return dto;
	}
}
