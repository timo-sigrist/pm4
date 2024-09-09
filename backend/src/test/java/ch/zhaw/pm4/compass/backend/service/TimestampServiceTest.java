package ch.zhaw.pm4.compass.backend.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.zhaw.pm4.compass.backend.UserRole;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.LocalUser;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import ch.zhaw.pm4.compass.backend.repository.TimestampRepository;

class TimestampServiceTest {

	@Mock
	private TimestampRepository timestampRepository;

	@InjectMocks
	private TimestampService timestampService;

	@Mock
	private DaySheetRepository daySheetRepository;

	@Mock
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		MockitoAnnotations.openMocks(daySheetRepository);
		timestamp = getTimestamp();
		timestampDto = getTimestampDto();
		daySheet = getDaySheet();
		timestamp2 = getTimestamp(); // 13:00 -> 14:00
		timestamp2.setId(2l);
		timestamps2 = new ArrayList<>();
		timestamps2.add(timestamp2);
		timestampToCheck = getTimestamp();
		timestamps1 = new ArrayList<>();
		timestamps1.add(timestamp);
	}

	private String reportText = "Testdate";

	private String user_id = "k234öljk43öj4öj";
	private Timestamp timestamp;
	private Timestamp timestamp2;
	private TimestampDto timestampDto;
	private DaySheet daySheet;
	private List<Timestamp> timestamps2;
	private List<Timestamp> timestamps1;

	private Timestamp timestampToCheck;

	LocalUser getLocalUser() {
		return new LocalUser(user_id, UserRole.PARTICIPANT);
	}

	DaySheet getDaySheet() {
		return new DaySheet(1l, getLocalUser(), reportText, LocalDate.now(), false, new ArrayList<>());
	}

	private TimestampDto getTimestampDto() {
		return new TimestampDto(1l, 1l, LocalTime.parse("13:00:00"), LocalTime.parse("14:00:00"));
	}

	private TimestampDto getUpdateTimestamp() {
		return new TimestampDto(1l, 1l, LocalTime.parse("13:00:00"), LocalTime.parse("15:00:00"));
	}

	private Timestamp getTimestamp() {
		return new Timestamp(1l, LocalTime.parse("13:00:00"), LocalTime.parse("14:00:00"), getDaySheet());
	}

	@Test
	public void testCreateTimestamp() {
		when(timestampRepository.save(any(Timestamp.class))).thenReturn(timestamp);
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(new ArrayList<>());
		when(daySheetRepository.findByIdAndOwnerId(any(Long.class), any(String.class)))
				.thenReturn(Optional.of(daySheet));

		TimestampDto resultTimestamp = timestampService.createTimestamp(timestampDto, user_id);
		assertEquals(timestampDto.getDay_sheet_id(), resultTimestamp.getDay_sheet_id());
		assertEquals(timestampDto.getStart_time(), resultTimestamp.getStart_time());
		assertEquals(timestampDto.getEnd_time(), resultTimestamp.getEnd_time());
	}

	@Test
	public void testCreateTimestampOfNotExistingDaySheet() {
		when(timestampRepository.save(any(Timestamp.class))).thenReturn(timestamp);
		when(daySheetRepository.findByIdAndOwnerId(any(Long.class), any(String.class))).thenReturn(Optional.empty());

		TimestampDto resultTimestamp = timestampService.createTimestamp(timestampDto, user_id);
		assertEquals(null, resultTimestamp);
	}

	@Test
	void testGetTimestampById() {
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.of(timestamp));
		TimestampDto resultTimestamp = timestampService.getTimestampById(timestampDto.getId(), user_id);
		assertEquals(timestampDto.getId(), resultTimestamp.getId());
		assertEquals(timestampDto.getDay_sheet_id(), resultTimestamp.getDay_sheet_id());
		assertEquals(timestampDto.getStart_time(), resultTimestamp.getStart_time());
		assertEquals(timestampDto.getEnd_time(), resultTimestamp.getEnd_time());
	}

	@Test
	public void testGetNotExistingTimestampById() {
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.empty());
		TimestampDto resultTimestamp = timestampService.getTimestampById(timestampDto.getId(), user_id);
		assertNull(resultTimestamp);
	}

	@Test
	void testCreateExistingTimestamp() {

		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.of(timestamp));

		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps2);
		TimestampDto result = timestampService.createTimestamp(timestampDto, user_id);
		assertEquals(null, result);
	}

	@Test
	void testCreateOverlappingTimestamp1() {

		TimestampDto timestampUpdateDto = getUpdateTimestamp();
		timestampUpdateDto.setStart_time(LocalTime.parse("12:00:00"));
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.of(timestamp));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps1);
		TimestampDto result = timestampService.createTimestamp(timestampUpdateDto, user_id);
		assertEquals(null, result);
	}

	@Test
	void testCreateOverlappingTimestamp2() {

		TimestampDto timestampUpdateDto = getUpdateTimestamp();
		timestampUpdateDto.setStart_time(LocalTime.parse("13:30:00"));
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.of(timestamp));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps1);
		TimestampDto result = timestampService.createTimestamp(timestampUpdateDto, user_id);
		assertEquals(null, result);
	}

	@Test
	void testCreateOverlappingTimestamp3() {

		TimestampDto timestampUpdateDto = getUpdateTimestamp();
		timestampUpdateDto.setStart_time(LocalTime.parse("12:30:00"));
		timestampUpdateDto.setEnd_time(LocalTime.parse("13:30:00"));
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.of(timestamp));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps1);
		TimestampDto result = timestampService.createTimestamp(timestampUpdateDto, user_id);
		assertEquals(null, result);
	}

	@Test
	void testAllTimestampsByDayId() {
		TimestampDto getTimestampDto0 = getTimestampDto();
		TimestampDto getTimestampDto1 = getUpdateTimestamp();
		getTimestampDto1.setId(2l);
		getTimestampDto1.setStart_time(LocalTime.parse("14:00:00"));
		getTimestampDto1.setEnd_time(LocalTime.parse("15:00:00"));
		daySheet.setOwner(getLocalUser());
		Timestamp timestamp01 = new Timestamp(1l, LocalTime.parse("13:00:00"), LocalTime.parse("14:00:00"), daySheet);
		daySheet.getTimestamps().add(timestamp01);
		Timestamp timestamp02 = new Timestamp(2l, LocalTime.parse("14:00:00"), LocalTime.parse("15:00:00"), daySheet);
		daySheet.getTimestamps().add(timestamp02);
		ArrayList<TimestampDto> timestampsDto = new ArrayList<TimestampDto>();
		timestampsDto.add(getTimestampDto0);
		timestampsDto.add(getTimestampDto1);
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(daySheet.getTimestamps());
		when(daySheetRepository.findById(any(Long.class))).thenReturn(Optional.of(daySheet));
		ArrayList<TimestampDto> res = timestampService.getAllTimestampsByDaySheetId(daySheet.getId(), user_id);
		TimestampDto resTimestampDto0 = res.get(0);
		TimestampDto resTimestampDto1 = res.get(1);

		assertEquals(getTimestampDto0.getId(), resTimestampDto0.getId());
		assertEquals(getTimestampDto0.getDay_sheet_id(), resTimestampDto0.getDay_sheet_id());
		assertEquals(getTimestampDto0.getStart_time().toString(), resTimestampDto0.getStart_time().toString());
		assertEquals(getTimestampDto0.getEnd_time().toString(), resTimestampDto0.getEnd_time().toString());

		assertEquals(getTimestampDto1.getId(), resTimestampDto1.getId());
		assertEquals(getTimestampDto1.getDay_sheet_id(), resTimestampDto1.getDay_sheet_id());
		assertEquals(getTimestampDto1.getStart_time().toString(), resTimestampDto1.getStart_time().toString());
		assertEquals(getTimestampDto1.getEnd_time().toString(), resTimestampDto1.getEnd_time().toString());
		verify(timestampRepository, times(1)).findAllByDaySheetId(any(Long.class));
	}

	@Test
	void testUpdateTimestamp() {
		TimestampDto timestampUpdateDto = getUpdateTimestamp();

		when(timestampRepository.save(any(Timestamp.class))).thenReturn(timestamp);
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.of(timestamp));
		TimestampDto resultTimestamp = timestampService.updateTimestampById(timestampUpdateDto, user_id);
		assertEquals(timestampUpdateDto.getDay_sheet_id(), resultTimestamp.getDay_sheet_id());
		assertEquals(timestampUpdateDto.getStart_time(), resultTimestamp.getStart_time());
		assertEquals(timestampUpdateDto.getEnd_time(), resultTimestamp.getEnd_time());
	}

	@Test
	void testUpdateNotExistingTimestamp() {
		when(timestampRepository.save(any(Timestamp.class))).thenReturn(timestamp);
		when(timestampRepository.findById(any(Long.class))).thenReturn(Optional.empty());
		TimestampDto resultTimestamp = timestampService.updateTimestampById(timestampDto, user_id);
		assertEquals(null, resultTimestamp);
	}

	@Test
	void testCheckNoDoubleEntryStartTimeAfterEndTime() {

		timestampToCheck.setStartTime(LocalTime.parse("15:00:00"));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps2);

		assertFalse(timestampService.checkNoDoubleEntry(timestampToCheck));
	}

	@Test
	void testCheckNoDoubleEntryStartTimeEqualsEndTime() {

		timestampToCheck.setStartTime(LocalTime.parse("13:00:00"));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps2);

		assertFalse(timestampService.checkNoDoubleEntry(timestampToCheck));
	}

	@Test
	void testCheckNoDoubleEntryStartTimeInExistingTimestamp() {

		timestampToCheck.setStartTime(LocalTime.parse("13:30:00"));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps2);

		assertFalse(timestampService.checkNoDoubleEntry(timestampToCheck));
	}

	@Test
	void testCheckNoDoubleEntryEndTimeInExistingTimestamp() {

		timestampToCheck.setStartTime(LocalTime.parse("12:00:00"));
		timestampToCheck.setEndTime(LocalTime.parse("13:30:00"));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps2);

		assertFalse(timestampService.checkNoDoubleEntry(timestampToCheck));
	}

	@Test
	void testCheckNoDoubleEntryStartTimeEqualsExistingTimestampStartTime() {

		timestampToCheck.setStartTime(LocalTime.parse("13:00:00"));
		timestampToCheck.setEndTime(LocalTime.parse("14:30:00"));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps2);

		Boolean result = timestampService.checkNoDoubleEntry(timestampToCheck);
		assertFalse(result);
	}

	@Test
	void testCheckNoDoubleEntryEndTimeEqualsExistingTimestampEndTime() {

		timestampToCheck.setStartTime(LocalTime.parse("12:00:00"));
		timestampToCheck.setEndTime(LocalTime.parse("14:00:00"));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps2);
		Boolean result = timestampService.checkNoDoubleEntry(timestampToCheck);
		assertFalse(result);
	}

	@Test
	void testCheckNoDoubleEntryNewTimestampAroundExistingTimestamp() {

		timestampToCheck.setStartTime(LocalTime.parse("12:00:00"));
		timestampToCheck.setEndTime(LocalTime.parse("15:00:00"));
		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps2);

		assertFalse(timestampService.checkNoDoubleEntry(timestampToCheck));
	}

	@Test
	void testCheckNoDoubleEntryExcludesOwnTimestamp() {

		when(timestampRepository.findAllByDaySheetId(any(Long.class))).thenReturn(timestamps2);

		assertTrue(timestampService.checkNoDoubleEntry(timestamp2));
	}
}