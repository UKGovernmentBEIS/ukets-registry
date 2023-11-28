package gov.uk.ets.registry.api.itl.message;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;
import gov.uk.ets.registry.api.itl.message.service.ITLMessageManagementService;

public class ITLMessageControllerTest {

	private MockMvc mockMvc;

	private ITLMessageController controller;

	@Mock
	private ITLMessageManagementService messageManagementService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller = new ITLMessageController(messageManagementService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test()
	@DisplayName("Test retrieval of specific message")
	public void getMessage() throws Exception {

		AcceptMessageLog message = new AcceptMessageLog();
		Optional<AcceptMessageLog> messageOptional  = Optional.of(message);
		Mockito.when(messageManagementService.getMessage(any())).thenReturn(messageOptional);
		mockMvc.perform(get("/api-registry/itl.messages.get?messageId=22")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
//	@Test
//	public void searchMessages() throws Exception {
//		AcceptMessageLog message1 = new AcceptMessageLog();
//		message1.setId(88L);
//		message1.setContent("A content.");
//		message1.setMessageDatetime(new Date());
//		List<AcceptMessageLog> results = new ArrayList<>();
//		results.add(message1);
//		Page<AcceptMessageLog> searchResult = Mockito.mock(Page.class);
//		Mockito.when(searchResult.getContent()).thenReturn(results);
//		Mockito.when(searchResult.getTotalElements()).thenReturn(Long.valueOf(results.size()));
//		Mockito.when(messageManagementService.search(any(), any())).thenReturn(searchResult);
//
//		mockMvc.perform(get("/api-registry/itl.messages.list").param("page", "0").param("pageSize", "30")
//				.param("sortDirection", Direction.ASC.toString()).accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.page", is(0)))
//				.andExpect(jsonPath("$.pageSize", is(30)))
//				.andExpect(jsonPath("$.totalResults", is(1)))
//				.andExpect(jsonPath("$.items", hasSize(1)))
//				.andExpect(jsonPath("$.items[0].messageId", is(message1.getId())))
//				.andExpect(jsonPath("$.items[0].messageDate", is(message1.getMessageDatetime())))
//				.andExpect(jsonPath("$.items[0].content", is(message1.getContent())));
//	}
}
