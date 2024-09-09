package ch.zhaw.pm4.compass.backend.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.zhaw.pm4.compass.backend.RatingType;
import ch.zhaw.pm4.compass.backend.exception.NotValidCategoryOwnerException;

public class CategoryModelTest {
	Rating validRating;
	Rating inValidRatingOver;
	Rating inValidRatingUnder;

	Category category;

	List<LocalUser> emptyUserList;

	@BeforeEach
	void setUp() {
		this.validRating = new Rating(3, RatingType.PARTICIPANT);
		this.inValidRatingOver = new Rating(100, RatingType.PARTICIPANT);
		this.inValidRatingUnder = new Rating(0, RatingType.PARTICIPANT);

		this.emptyUserList = new ArrayList<>();

		try {
			this.category = new Category("Test Cat", 1, 10, emptyUserList);
		} catch (NotValidCategoryOwnerException e) {
			return;
		}
	}

	@Test
	public void whenGivenARatingOutsideOfMinMaxBorderForValidation_expectFalse() {
		assertTrue(category.isValidRating(validRating));
		assertFalse(category.isValidRating(inValidRatingOver));
		assertFalse(category.isValidRating(inValidRatingUnder));
	}
}
