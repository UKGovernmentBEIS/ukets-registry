package uk.ets.lib.commons.kafkaconfig;

import lombok.Builder;
import lombok.Getter;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Builder
@Getter
public class UkEtsKafkaConfigProperties {

    /**
     * The transactional id.
     */
    private final String transactionalId;
    /**
     * Comma-delimited list of host:port pairs to use for establishing the initial connections to the Kafka cluster.
     */
    private final String kafkaBootstrapAddress;
    /**
     * Used mainly to inject the authentication properties.
     */
    private final KafkaProducerConfig kafkaProducerConfig;
    /**
     * Optional parameter.
     */
    private final JsonSerializer jsonSerializer;
    /**
     * If set, will be set directly in the producer factory. Used to avoid InvalidPidMappingException.
     */
    private final Long maxAgeInMillis;
}
