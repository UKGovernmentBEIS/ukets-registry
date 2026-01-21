package gov.uk.ets.registry.api.integration.service;

import static gov.uk.ets.registry.api.integration.config.KafkaConstants.CORRELATION_ID_HEADER;

import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import java.util.Map;
import java.util.Optional;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

/**
 * Extended version of IntegrationHeadersUtil that supports new topic naming patterns:
 *  - registry-installation-...
 *  - registry-mrtm-...
 *  - aviation-...
 */
@Component
public class RegistryIntegrationHeadersUtil {

    public OperatorType getOperatorType(Map<String, Object> headers) {
        String sourceTopic = getSourceTopic(headers);

        if (sourceTopic.contains("installation")) {
            return OperatorType.INSTALLATION;
        }
        if (sourceTopic.contains("aviation")) {
            return OperatorType.AIRCRAFT_OPERATOR;
        }
        if (sourceTopic.contains("maritime") || sourceTopic.contains("mrtm")) {
            return OperatorType.MARITIME_OPERATOR;
        }

        throw new IllegalArgumentException("Unknown source topic: " + sourceTopic);
    }

    public SourceSystem getSourceSystem(Map<String, Object> headers) {
        String sourceTopic = getSourceTopic(headers);

        if (sourceTopic.contains("installation")) {
            return SourceSystem.METSIA_INSTALLATION;
        }
        if (sourceTopic.contains("aviation")) {
            return SourceSystem.METSIA_AVIATION;
        }
        if (sourceTopic.contains("maritime") || sourceTopic.contains("mrtm")) {
            return SourceSystem.MARITIME;
        }

        throw new IllegalArgumentException("Unknown source topic: " + sourceTopic);
    }

    public String getSourceTopic(Map<String, Object> headers) {
        Object topicHeader = headers.get(KafkaHeaders.RECEIVED_TOPIC);
        return topicHeader != null ? topicHeader.toString() : "unknown-topic";
    }

    public String getCorrelationId(Map<String, Object> headers) {
        return Optional.ofNullable(headers.get(CORRELATION_ID_HEADER))
            .map(bytes -> new String((byte[]) bytes))
            .orElse("N/A");
    }
}
