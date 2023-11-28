package gov.uk.ets.registry.api.itl.notices;

import gov.uk.ets.registry.api.itl.notice.ITLNoticeController;
import gov.uk.ets.registry.api.itl.notice.service.ITLNoticeManagementService;
import gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticeDetailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ITLNoticeControllerTest {

    private MockMvc mockMvc;

    private ITLNoticeController controller;

    @Mock
    private ITLNoticeManagementService noticeManagementService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new ITLNoticeController(noticeManagementService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test()
    @DisplayName("Test retrieval of specific ITL notice")
    public void getITLNotice() throws Exception {

        List<ITLNoticeDetailResult> noticeDetailList = new ArrayList<>();
        Mockito.when(noticeManagementService.getITLDetails(any())).thenReturn(noticeDetailList);
        mockMvc.perform(get("/api-registry/itl.notices.get?notificationIdentity=12")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /*@Test
    public void searchMessages() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendarCreated = Calendar.getInstance();
        calendarCreated.set(2020, 11, 3);

        Calendar calendarUpdated = Calendar.getInstance();
        calendarCreated.set(2020, 11, 3);

        Calendar calendarFrom = Calendar.getInstance();
        calendarCreated.set(2020, 11, 1);

        Calendar calendarTo = Calendar.getInstance();
        calendarCreated.set(2020, 11, 4);

		ITLNoticeResult noticeResult = new ITLNoticeResult(11L, NoticeType.COMMITMENT_PERIOD_RESERVE,
                calendarCreated.getTime(), calendarUpdated.getTime(), NoticeStatus.COMPLETED);
		List<ITLNoticeResult> results = new ArrayList<>();
		results.add(noticeResult);
		Page<ITLNoticeResult> searchResult = Mockito.mock(Page.class);
		Mockito.when(searchResult.getContent()).thenReturn(results);
		Mockito.when(searchResult.getTotalElements()).thenReturn(Long.valueOf(results.size()));
		Mockito.when(noticeManagementService.search(any(), any())).thenReturn(searchResult);

		mockMvc.perform(get(String.format("/api-registry/itl.notices.list?pageSize=%d&messageDateFrom=%s&messageDateTo=%s&sortDirection=%s",2,
                sdf.format(calendarFrom.getTime()), sdf.format(calendarTo.getTime()), "DESC"))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.page", Matchers.is(0)))
				.andExpect(jsonPath("$.pageSize", Matchers.is(30)))
				.andExpect(jsonPath("$.totalResults", Matchers.is(1)))
				.andExpect(jsonPath("$.items", Matchers.hasSize(1)))
				.andExpect(jsonPath("$.items[0].notificationIdentifier", Matchers.is(noticeResult.getNotificationIdentifier())))
				.andExpect(jsonPath("$.items[0].receivedOn", Matchers.is(noticeResult.getReceivedOn())))
				.andExpect(jsonPath("$.items[0].createdDate", Matchers.is(noticeResult.getLastUpdateOn())))
				.andExpect(jsonPath("$.items[0].noticeStatus", Matchers.is(noticeResult.getStatus())))
				.andExpect(jsonPath("$.items[0].noticeType", Matchers.is(noticeResult.getType())));
    }*/
}
