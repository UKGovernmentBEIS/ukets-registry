package gov.uk.ets.ui.logs.web;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import gov.uk.ets.ui.logs.error.UkEtsUiLogsException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class JsonSanitizerService {

    private final ObjectMapper strictObjectMapper;
    private final boolean isStrictJsonValidationEnabled;

    public List<String> createAndSanitizeLogs(final String json) {
        return ofNullable(json)
            .filter(StringUtils::isNotBlank)
            .map(this::doSanitization)
            .map(this::toList)
            .orElseThrow(() -> new UkEtsUiLogsException("Empty JSON body provided for sanitization."));
    }

    public String assertIsValidJson(final String json) {
        try {
            strictObjectMapper.readTree(json);
            return json;
        } catch (JsonProcessingException e) {
            throw new UkEtsUiLogsException("Non Sanitizable JSON body provided.", e);
        }
    }

    private String doSanitization(final String json) {
        var result = JsonSanitizer.sanitize(json);
        return isStrictJsonValidationEnabled ? this.assertIsValidJson(result) : result;
    }

    @SneakyThrows
    private List<String> toList(final String json) {
        return StreamSupport.stream(strictObjectMapper.readTree(json).spliterator(), false)
            .map(JsonNode::toString)
            .collect(Collectors.toList());
    }

}
