package ch.zhaw.pm4.compass.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.exception.CategoryAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.GlobalCategoryException;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;
import ch.zhaw.pm4.compass.backend.exception.UserIsNotParticipantException;
import ch.zhaw.pm4.compass.backend.model.Category;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.dto.CategoryDto;
import ch.zhaw.pm4.compass.backend.model.dto.ParticipantDto;
import ch.zhaw.pm4.compass.backend.model.dto.RatingDto;
import ch.zhaw.pm4.compass.backend.model.dto.UserDto;
import ch.zhaw.pm4.compass.backend.repository.CategoryRepository;

/**
 * Service class for handling operations related to {@link Category} entities.
 * This class includes methods to manage categories, including creation, retrieval, and user linking
 *
 * Using {@link CategoryRepository} for persistence operations.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@Service
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private DaySheetService daySheetService;
	@Autowired
	private UserService userService;

	/**
	 * Creates a new category based on provided {@link CategoryDto}.
	 * Ensures that the category does not already exist by name before saving.
	 *
	 * @param createCategory DTO containing category details to be created.
	 * @return The created category converted back to DTO format.
	 * @throws CategoryAlreadyExistsException if a category with the same name already exists.
	 * @throws NotValidCategoryOwnerException if any of the category owners are not valid.
	 */
	public CategoryDto createCategory(CategoryDto createCategory) throws CategoryAlreadyExistsException, NotValidCategoryOwnerException {
		Category category = convertDtoToEntity(createCategory);

		if (categoryRepository.findByName(category.getName()).isPresent()) {
			throw new CategoryAlreadyExistsException(category);
		}

		List<UserDto> userDtos = userService.getAllUsers();
		return convertEntityToDto(categoryRepository.save(category), userDtos);
	}

	/**
	 * Retrieves a category by its name, optionally including associated ratings.
	 *
	 * @param name The name of the category to retrieve.
	 * @return The retrieved category as a DTO.
	 * @throws NoSuchElementException if the category does not exist.
	 */
	public CategoryDto getCategoryByName(String name) throws NoSuchElementException {
		List<UserDto> userDtos = userService.getAllUsers();
		return convertEntityToDto(categoryRepository.findByName(name).orElseThrow(), userDtos);
	}

	/**
	 * Retrieves all categories stored in the repository.
	 *
	 * @return A list of all categories as DTOs.
	 */
	public List<CategoryDto> getAllCategories() {
		List<UserDto> userDtos = userService.getAllUsers();
		return categoryRepository.findAll().stream().map(i -> convertEntityToDto(i, userDtos)).toList();
	}

	/**
	 * Links users to an existing category, ensuring that it is not a global category.
	 *
	 * @param linkCategory DTO containing the category and users to link.
	 * @return The updated category as a DTO.
	 * @throws NotValidCategoryOwnerException if any of the users are not valid category owners.
	 * @throws GlobalCategoryException if the category is considered global and cannot have specific owners.
	 */
	public CategoryDto linkUsersToExistingCategory(CategoryDto linkCategory) throws NotValidCategoryOwnerException, GlobalCategoryException {
		Category newCategoryConfig = convertDtoToEntity(linkCategory);
		Category savedCategory = categoryRepository.findById(linkCategory.getId()).orElseThrow();
		List<UserDto> userDtos = userService.getAllUsers();

		if (savedCategory.getCategoryOwners().isEmpty()) {
			throw new GlobalCategoryException();
		}

		for (LocalUser i : newCategoryConfig.getCategoryOwners()) {
			if (!savedCategory.getCategoryOwners().contains(i)) {
				savedCategory.getCategoryOwners().add(i);
			}
		}

		return convertEntityToDto(categoryRepository.save(savedCategory), userDtos);
	}

	/**
	 * Retrieves a list of categories by a user's ID, ensuring the user is a participant.
	 *
	 * @param userId The ID of the user whose categories are to be retrieved.
	 * @return A list of categories associated with the user, as DTOs.
	 * @throws UserIsNotParticipantException if the user is not a participant.
	 */
	public List<CategoryDto> getCategoryListByUserId(String userId) throws UserIsNotParticipantException {
		LocalUser user = userService.getLocalUser(userId);
		if (user.getRole() != UserRole.PARTICIPANT) {
			throw new UserIsNotParticipantException();
		}

		Iterable<Category> globalCategories = categoryRepository.findGlobalCategories();
		Iterable<Category> userCategories = categoryRepository.findAllByCategoryOwners(user);
		List<UserDto> userDtos = userService.getAllUsers();

		return Stream.concat(StreamSupport.stream(globalCategories.spliterator(), false),
				StreamSupport.stream(userCategories.spliterator(), false)).map(i -> {
					CategoryDto categoryDto = convertEntityToDto(i, userDtos);
					categoryDto.setCategoryOwners(List.of());
					return categoryDto;
				}).toList();
	}

	/**
	 * Converts a {@link CategoryDto} to a {@link Category} entity.
	 * This method also handles the conversion of participant DTOs to {@link LocalUser} entities, ensuring that
	 * all category owners are properly instantiated based on the provided DTO details.
	 *
	 * @param dto The category DTO to convert.
	 * @return The corresponding {@link Category} entity with owners populated from the user service.
	 * @throws NotValidCategoryOwnerException if any specified owner does not meet the validation criteria
	 *         necessary to be a category owner.
	 */
	public Category convertDtoToEntity(CategoryDto dto) throws NotValidCategoryOwnerException {
		List<LocalUser> categoryOwners = dto.getCategoryOwners().stream().map(userDto -> new LocalUser(userDto.getUser_id(),userDto.getRole())).toList();
		return new Category(dto.getName(), dto.getMinimumValue(), dto.getMaximumValue(), categoryOwners);
	}

	/**
	 * Converts a {@link Category} entity to a {@link CategoryDto}.
	 * This method also decides whether to include associated mood ratings based on the {@code withRatings} parameter.
	 * Ratings are converted using a helper method from the day sheet service, which adds additional details
	 * necessary for complete data representation in the DTO.
	 *
	 * @param entity The category entity to convert.
	 * @return The corresponding {@link CategoryDto} with or without mood ratings based on the provided flag.
	 */
  public CategoryDto convertEntityToDto(Category entity, List<UserDto> userDtos) {
	  List<UserDto> categoryOwnerDtos = new ArrayList<>();
	  for (LocalUser i : entity.getCategoryOwners()) {
		  UserDto userDto = userDtos.stream().filter(userFilter -> userFilter.getUser_id().equals(i.getId())).findFirst().orElse(null);
		  categoryOwnerDtos.add(userDto);
	  }

	  return new CategoryDto(entity.getId(), entity.getName(), entity.getMinimumValue(), entity.getMaximumValue(), categoryOwnerDtos);
  }
}
