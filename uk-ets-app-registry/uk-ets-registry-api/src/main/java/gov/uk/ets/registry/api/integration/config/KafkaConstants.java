package gov.uk.ets.registry.api.integration.config;

public class KafkaConstants {

    private KafkaConstants() { }

    public static final String CORRELATION_ID_HEADER = "Correlation-Id";
    public static final String CORRELATION_PARENT_ID_HEADER = "Correlation-Parent-Id";
    public static final String PRODUCER_CLIENT_ID_HEADER = "Producer-Client-Id";

}