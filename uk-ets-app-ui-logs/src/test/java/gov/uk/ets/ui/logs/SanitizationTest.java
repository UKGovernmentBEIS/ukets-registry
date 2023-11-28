package gov.uk.ets.ui.logs;

import static gov.uk.ets.ui.logs.UiLogsUtils.readBadlyFormedSanitizableJson;
import static gov.uk.ets.ui.logs.UiLogsUtils.readNonSanitizableJson;
import static gov.uk.ets.ui.logs.UiLogsUtils.readWellFormedJson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.ui.logs.error.UkEtsUiLogsException;
import gov.uk.ets.ui.logs.web.JsonSanitizerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SanitizationTest {

    private final ObjectMapper mapper = new ObjectMapper()
        .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
        .enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);

    private final JsonSanitizerService sanitizerService =
        new JsonSanitizerService(mapper, true);

    @Test
    void should_not_change_well_formed_json() throws Exception {
        final var json = readWellFormedJson();
        final var sanitizedJsonList = sanitizerService.createAndSanitizeLogs(json);
        sanitizedJsonList.forEach(j ->
            Assertions.assertDoesNotThrow(() -> {
                sanitizerService.assertIsValidJson(j);
            })
        );

    }

    @Test
    void should_sanitize_reasonably_json_like_payload() throws Exception {
        final var json = readBadlyFormedSanitizableJson();
        final var sanitizedJsonList = sanitizerService.createAndSanitizeLogs(json);
        sanitizedJsonList.forEach(el ->
            Assertions.assertDoesNotThrow(() -> sanitizerService.assertIsValidJson(el))
        );
    }

    @Test
    void should_throw_exception_on_non_sanitizable_json_like_payload() throws Exception {
        final var json = readNonSanitizableJson();
        Assertions.assertThrows(UkEtsUiLogsException.class, () -> sanitizerService.createAndSanitizeLogs(json));
    }

}
