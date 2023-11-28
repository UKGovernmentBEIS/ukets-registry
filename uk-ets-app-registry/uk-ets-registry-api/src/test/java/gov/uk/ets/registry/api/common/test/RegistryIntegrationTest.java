package gov.uk.ets.registry.api.common.test;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ActiveProfiles("integrationTest")
@EmbeddedKafka(topics = {
    "registry.originating.notification.topic",
    "proposal.notification.in",
    "domain.event.topic",
    "group.notification.topic",
    "registry.originating.reconciliation.question.topic",
    "registry.originating.transaction.question.topic",
    "itl.originating.reconciliation.in.topic",
    "itl.originating.reconciliation.out.topic",
    "compliance.events.in.topic"},
    brokerPropertiesLocation = "classpath:integration-test-application.properties",
    brokerProperties = "auto.create.topics.enable=true",
    count = 3,
    ports = {0, 0, 0}
)
@TestPropertySource(locations = "/integration-test-application.properties", properties = {
    "mail.fromAddress=test@test.gr",
    "spring.application.name=registry-api",
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}",
    "logging.level.root=INFO",
    "kafka.authentication.enabled=false",
    "spring.datasource.hikari.maximum-pool-size=5"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(
    initializers = PostgresSQLContainerInitializer.class)
@TestInstance(Lifecycle.PER_CLASS)
public @interface RegistryIntegrationTest {

}
