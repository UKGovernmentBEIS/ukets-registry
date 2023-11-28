package gov.uk.ets.registry.api.common.test;

import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

@RegistryIntegrationTest
public abstract class BaseIntegrationTest {
    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;

    @AfterAll
    protected void tearDown() {
        embeddedKafkaBroker.destroy();
    }

}
