package gov.uk.ets.registry.api.integration.service;

import static gov.uk.ets.registry.api.integration.config.KafkaConstants.CORRELATION_ID_HEADER;

import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import java.util.Map;
import java.util.Optional;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

@Component
public class IntegrationHeadersUtil {

    public String getSourceTopic(Map<String, Object> headers) {
        return headers.get(KafkaHeaders.RECEIVED_TOPIC).toString();
    }

    public OperatorType getOperatorType(Map<String, Object> headers) {
        String sourceTopic = getSourceTopic(headers);
        if (sourceTopic.startsWith("installation")) {
            return OperatorType.INSTALLATION;
        }
        if (sourceTopic.startsWith("aviation")) {
            return OperatorType.AIRCRAFT_OPERATOR;
        }
        if (sourceTopic.startsWith("maritime")) {
            return OperatorType.MARITIME_OPERATOR;
        }
        throw new IllegalArgumentException("Unknown source");
    }

    public SourceSystem getSourceSystem(Map<String, Object> headers) {
        String sourceTopic = getSourceTopic(headers);
        if (sourceTopic.startsWith("installation") || sourceTopic.startsWith("aviation")) {
            return SourceSystem.METSIA;
        }
        if (sourceTopic.startsWith("maritime")) {
            return SourceSystem.MARITIME;
        }
        throw new IllegalArgumentException("Unknown source");
    }

    public String getCorrelationId(Map<String, Object> headers) {
        return Optional.ofNullable(headers.get(CORRELATION_ID_HEADER))
            .map(bytes -> new String((byte[]) bytes))
            .orElse("N/A");
    }

}
