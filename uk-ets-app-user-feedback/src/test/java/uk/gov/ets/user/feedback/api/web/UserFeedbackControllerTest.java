package uk.gov.ets.user.feedback.api.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;

import jakarta.servlet.annotation.WebFilter;
import uk.gov.ets.user.feedback.api.service.MailService;
import uk.gov.ets.user.feedback.api.web.model.UserFeedbackRequest;

/**
 * @author Chris Georgoulis on 18/01/2022.
 */
@org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest(controllers = UserFeedbackController.class, excludeFilters = @ComponentScan.Filter(WebFilter.class))
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MailService mailService;
    @MockitoBean
	private ObjectMapper objectMapper;
    @MockitoBean
	private UserFeedbackRequest userFeedbackRequest;
    
    private final static String SURVEY_URL = "/api-user-feedback/survey";

    @BeforeEach
    void init() {
        // given
        Map<String, String> expectedMap = Map.of("key", "value");
        when(objectMapper.convertValue(any(UserFeedbackRequest.class), any(MapType.class)))
                .thenReturn(expectedMap);
        when(objectMapper.convertValue(any(Map.class) , eq(UserFeedbackRequest.class)))
        .thenReturn(userFeedbackRequest);
        when(userFeedbackRequest.getTimestamp())
        .thenReturn(LocalDateTime.now().toString());
    }
    
    @Test
    @DisplayName("Survey form should be successfully submitted")
    void testFormSubmission() throws Exception {
        this.mockMvc.perform(
            post(SURVEY_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("timestamp", LocalDateTime.now().toString())
                .param("satisfactionRate", "testtest")
                .param("userRegistrationRate", "<script>console.log</script>")
                .param("onlineGuidanceRate", "testtest")
                .param("creatingAccountRate", "testtest")
                .param("onBoardingRate", "testtest")
                .param("tasksRate", "testtest")
                .param("improvementSuggestion", "very very very very very long string " +
                    "very very very very very long string very very very very very long string")
        ).andExpect(status().isOk());

    }

    @Test
    @DisplayName("Survey form with null suggestion should not throw")
    void testFormSubmissionWithNullSuggestion() throws Exception {
        this.mockMvc.perform(
            post(SURVEY_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("timestamp", LocalDateTime.now().toString())
                .param("satisfactionRate", "testtest")
                .param("userRegistrationRate", "<script>console.log</script>")
                .param("onlineGuidanceRate", "testtest")
                .param("creatingAccountRate", "testtest")
                .param("onBoardingRate", "testtest")
                .param("tasksRate", "testtest")
        ).andExpect(status().isOk());

    }
}
