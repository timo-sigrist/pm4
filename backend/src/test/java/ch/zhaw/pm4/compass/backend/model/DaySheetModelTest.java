package ch.zhaw.pm4.compass.backend.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class DaySheetModelTest {
	@Test
	public void testConstructors() {
		LocalDate now = LocalDate.now();

		DaySheet sheet1 = new DaySheet(now);
		assertNotNull(sheet1.getTimestamps());
		assertNotNull(sheet1.getIncidents());
		assertNotNull(sheet1.getMoodRatings());

		ArrayList<Timestamp> timestamps = new ArrayList<>();
		ArrayList<Rating> moodRatings = new ArrayList<>();
		DaySheet sheet2 = new DaySheet(1l, new LocalUser(), "dayNotes", now, false, timestamps, moodRatings);
		assertEquals(1l, sheet2.getId());
		assertNotNull(sheet2.getOwner());
		assertNotNull(sheet2.getTimestamps());
		assertNotNull(sheet2.getIncidents());
		assertNotNull(sheet2.getMoodRatings());

		DaySheet sheet3 = new DaySheet(2l, "dayNotes", now, false, timestamps, moodRatings);
		assertEquals(2l, sheet3.getId());
		assertNotNull(sheet3.getTimestamps());
		assertNotNull(sheet3.getIncidents());
		assertNotNull(sheet3.getMoodRatings());

		DaySheet sheet4 = new DaySheet(3l, "dayNotes", now, true, timestamps);
		assertEquals(3l, sheet4.getId());
		assertNotNull(sheet4.getTimestamps());
		assertNotNull(sheet4.getIncidents());
		assertNotNull(sheet4.getMoodRatings());

		DaySheet sheet5 = new DaySheet("dayNotes", now);
		assertNotNull(sheet5.getTimestamps());
		assertNotNull(sheet5.getIncidents());
		assertNotNull(sheet5.getMoodRatings());
	}
}
