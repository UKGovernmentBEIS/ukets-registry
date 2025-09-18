package gov.uk.ets.registry.api.integration.config;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaLoggingEntry<K> {

    private Type type;
    private String correlationId;
    private String correlationParentId;
    private String clientId;
    private K recordKey;
    @Builder.Default
    private Map<String, Object> recordValue = new HashMap<>();
    private String topic;
    private int partition;
    private Long offset;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public enum Type {
        PRODUCING,
        CONSUMING
    }

}
