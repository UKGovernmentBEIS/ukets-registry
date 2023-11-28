package gov.uk.ets.ui.logs;

import static gov.uk.ets.ui.logs.UiLogsUtils.UI_LOGS_URL;
import static gov.uk.ets.ui.logs.UiLogsUtils.readBadlyFormedSanitizableJson;
import static gov.uk.ets.ui.logs.UiLogsUtils.readNonSanitizableJson;
import static gov.uk.ets.ui.logs.UiLogsUtils.readWellFormedJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.uk.ets.ui.logs.web.LogsController;
import javax.servlet.annotation.WebFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

// excludes our custom filters (like CustomLoggingFilter etc.)
@WebMvcTest(controllers = LogsController.class, excludeFilters = @ComponentScan.Filter(WebFilter.class))
// disables all spring security (and probably other filters too) filters
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = { "features.strict-json-validation.enabled=true"})
class LogsControllerWithJsonValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_accepted_well_formed_json() throws Exception {
        var json = readWellFormedJson();
        createMockRequest(json).andExpect(status().isAccepted());
    }

    @Test
    void should_return_accepted_sanitizable_json() throws Exception {
        var json = readBadlyFormedSanitizableJson();
        createMockRequest(json).andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("Should return BAD_REQUEST for non sanitizable JSON")
    void test_non_sanitizable_json() throws Exception {
        var json = readNonSanitizableJson();
        createMockRequest(json).andExpect(status().isBadRequest());
    }

    protected ResultActions createMockRequest(String payload) throws Exception {
        return this.mockMvc.perform(
            post(UI_LOGS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        );
    }

}
