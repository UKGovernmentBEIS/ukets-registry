package gov.uk.ets.registry.api.compliance.messaging.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

import gov.uk.ets.registry.api.account.messaging.CompliantEntityInitializationEvent;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ComplianceOutgoingEventBase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;

class ComplianceOutboxServiceIntegrationTest extends BaseIntegrationTest {

    private static final Long TEST_COMPLIANT_ENTITY_ID_1 = 1234567L;
    private static final Long TEST_COMPLIANT_ENTITY_ID_2 = 1234568L;
    private static final String TEST_URID = "UK123456789";
    private static final int FIRST_YEAR_OF_VERIFIED_EMISSION = 2021;
    private static final String COMPLIANCE_EVENTS_IN_TOPIC = "compliance.events.in.topic";

    @SpyBean
    ComplianceEventProducer producer;

    @Autowired
    private ComplianceOutboxService cut;

    @Autowired
    private ComplianceOutboxRepository repository;

    Consumer<String, ComplianceOutgoingEventBase> consumer;

    @BeforeAll
    public void init() {
        Map<String, Object> config =
                new HashMap<>(KafkaTestUtils
                    .consumerProps("compliance_outbox_messages", "false", embeddedKafkaBroker));
        // make sure that we read transactionally committed messages
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        // make sure that we do not read old messages in case of re-balancing
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<>(
            config, new StringDeserializer(),
            new JsonDeserializer<>(ComplianceOutgoingEventBase.class, false)).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, COMPLIANCE_EVENTS_IN_TOPIC);
        
    }
    
    @BeforeEach
    public void setUp() throws ExecutionException, InterruptedException {
        repository.deleteAll();
        repository.flush();
    }

    @AfterEach
    protected void cleanUp() {
        //Looks like KafkaTestUtils.getRecords has changed
        //behaviour and does not commit records any more
        //since spring-boot 2.7.10 , so we need to commit
        //manually
        consumer.commitSync();
    }  
    
    @AfterAll
    protected void tearDown() {
        consumer.close();
        super.tearDown();
    }
    
    @Test
    void shouldCreateEvent() {

        LocalDateTime dateTriggered = LocalDateTime.now();
        
        CompliantEntityInitializationEvent event = getAccountCreationEvent(dateTriggered, TEST_COMPLIANT_ENTITY_ID_1);

        cut.create(event);

        List<ComplianceOutbox> outboxEntries = repository.findAll();
        assertThat(outboxEntries).hasSize(1);

        ComplianceOutbox entry = outboxEntries.get(0);
        assertThat(entry.getStatus()).isEqualTo(ComplianceOutboxStatus.PENDING);
        assertThat(entry.getGeneratedOn()).isEqualToIgnoringNanos(dateTriggered);

    }

    private CompliantEntityInitializationEvent getAccountCreationEvent(LocalDateTime dateTriggered, Long compliantEntityId) {
        return CompliantEntityInitializationEvent.builder()
            .compliantEntityId(compliantEntityId)
            .dateTriggered(dateTriggered)
            .actorId(TEST_URID)
            .firstYearOfVerifiedEmissions(FIRST_YEAR_OF_VERIFIED_EMISSION)
            .build();
    }

    @Test
    void shouldSendEventsAndUpdateDatabase() {

        LocalDateTime dateTriggered1 = LocalDateTime.now().withSecond(1).withSecond(2).withNano(0);
        CompliantEntityInitializationEvent event1 = getAccountCreationEvent(dateTriggered1, TEST_COMPLIANT_ENTITY_ID_1);
        LocalDateTime dateTriggered2 = LocalDateTime.now().withSecond(2).withNano(0);
        CompliantEntityInitializationEvent event2 = getAccountCreationEvent(dateTriggered2, TEST_COMPLIANT_ENTITY_ID_1);
        cut.create(event1);
        cut.create(event2);

        cut.processEvents();

        List<ComplianceOutbox> outboxEntries = repository.findAll();
        assertThat(outboxEntries).hasSize(2);
        assertThat(outboxEntries).extracting(ComplianceOutbox::getGeneratedOn)
            .containsExactlyInAnyOrder(dateTriggered1, dateTriggered2);
        assertThat(outboxEntries).extracting(ComplianceOutbox::getStatus)
            .containsExactlyInAnyOrder(ComplianceOutboxStatus.SENT, ComplianceOutboxStatus.SENT);
        assertThat(outboxEntries).extracting(ComplianceOutbox::getCompliantEntityId)
            .containsExactlyInAnyOrder(TEST_COMPLIANT_ENTITY_ID_1, TEST_COMPLIANT_ENTITY_ID_1);

        ConsumerRecords<String, ComplianceOutgoingEventBase> records = KafkaTestUtils.getRecords(consumer, Duration.ofMillis(10000), 2);

        assertThat(records.count()).isEqualTo(2);
        Iterator<ConsumerRecord<String, ComplianceOutgoingEventBase>> iterator = records.iterator();

        ComplianceOutgoingEventBase kafkaMessage1 = iterator.next().value();
        ComplianceOutgoingEventBase kafkaMessage2 = iterator.next().value();

        // ensure that the messages are consumed in order, since they have the same message key
        assertThat(List.of(kafkaMessage1, kafkaMessage2)).extracting(ComplianceOutgoingEventBase::getCompliantEntityId)
            .containsExactly(TEST_COMPLIANT_ENTITY_ID_1, TEST_COMPLIANT_ENTITY_ID_1);
    }

    @Test
    void shouldNotSendMessageNorUpdateDatabaseIfOneMessageFails() {

        LocalDateTime dateTriggered1 = LocalDateTime.now().withSecond(1).withSecond(2).withNano(0);
        CompliantEntityInitializationEvent event1 = getAccountCreationEvent(dateTriggered1, TEST_COMPLIANT_ENTITY_ID_1);
        LocalDateTime dateTriggered2 = LocalDateTime.now().withSecond(2).withNano(0);
        CompliantEntityInitializationEvent event2 = getAccountCreationEvent(dateTriggered2, TEST_COMPLIANT_ENTITY_ID_2);
        cut.create(event1);
        cut.create(event2);

        // try to simulate an error when sending the 2nd message to kafka...
        doThrow(new RuntimeException()).when(producer).send(event2);

        assertThrows(RuntimeException.class, () -> cut.processEvents());

        List<ComplianceOutbox> outboxEntries = repository.findAll();

        assertThat(outboxEntries).extracting(ComplianceOutbox::getStatus)
            .containsExactlyInAnyOrder(ComplianceOutboxStatus.PENDING, ComplianceOutboxStatus.PENDING);

        ConsumerRecords<String, ComplianceOutgoingEventBase> records = KafkaTestUtils.getRecords(consumer, Duration.ofMillis(10000), 2);
      
        assertThat(records.count()).isZero();
    }
}
