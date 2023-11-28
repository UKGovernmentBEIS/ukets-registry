package gov.uk.ets.ui.logs;

import static gov.uk.ets.ui.logs.UiLogsUtils.readNonSanitizableJson;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "features.strict-json-validation.enabled=false"})
class LogsControllerWithoutJsonValidationTest extends LogsControllerWithJsonValidationTest {

    @Test
    @DisplayName("Should return ACCEPTED for non sanitizable JSON")
    void test_non_sanitizable_json() throws Exception {
        var json = readNonSanitizableJson();
        createMockRequest(json).andExpect(status().isAccepted());
    }
}
