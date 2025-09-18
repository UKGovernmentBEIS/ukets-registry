package gov.uk.ets.registry.api.integration.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.apache.kafka.common.header.Headers;

@UtilityClass
public class MessageExtractor {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public String resolveHeader(String key, Headers headers) {
        return Optional.ofNullable(headers.lastHeader(key))
            .map(header -> new String(header.value(), StandardCharsets.UTF_8)).orElse(null);
    }

    public <V> Map<String, Object> resolveRecordValueAsMap(V recordValue) {
        Object value;

        if (recordValue instanceof byte[]) {
            String payload = new String((byte[]) recordValue);
            try {
                value = objectMapper.readValue(payload, Map.class);
            } catch (Exception e) {
                value = payload;
            }
        } else {
            try {
                value = objectMapper.convertValue(recordValue, new TypeReference<>() {
                });
            } catch (Exception e) {
                value = recordValue.toString();
            }
        }

        final Map<String, Object> recordValueAsMap = new HashMap<>();
        recordValueAsMap.put("payload", value);
        return recordValueAsMap;
    }

    public Map convertToMap(Object obj) {
        Map<String, Object> map = resolveRecordValueAsMap(obj);
        if (map.get("payload") instanceof Map<?,?> payload) {
            return payload;
        }
        return map;
    }
}
