package uk.gov.ets.user.feedback.api.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import javax.servlet.annotation.WebFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.ets.user.feedback.api.service.MailService;

/**
 * @author Chris Georgoulis on 18/01/2022.
 */
@WebMvcTest(controllers = UserFeedbackController.class, excludeFilters = @ComponentScan.Filter(WebFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MailService mailService;

    private final ObjectMapper mapper = new ObjectMapper();

    private final static String SURVEY_URL = "/api-user-feedback/survey";

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
