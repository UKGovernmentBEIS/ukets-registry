package gov.uk.ets.registry.api.integration.regulatornotice;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.helper.integration.RegulatorNoticeTestHelper;
import gov.uk.ets.registry.api.helper.integration.TestAccountData;
import gov.uk.ets.registry.api.helper.integration.TestAccountDataFactory;
import gov.uk.ets.registry.api.helper.integration.TestDataHelper;
import gov.uk.ets.registry.api.helper.integration.TestFileUtils;
import gov.uk.ets.registry.api.regulatornotice.domain.RegulatorNotice;
import gov.uk.ets.registry.api.regulatornotice.repository.RegulatorNoticeRepository;
import jakarta.persistence.EntityManager;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestPropertySource(
        locations = "/integration-test-application.properties",
        properties = {
                "kafka.integration.enabled=true",
                "kafka.integration.aviation.regulator.notice.enabled=true",
                "logging.level.root=ERROR",
                "account.audit.enabled=false",
        }
)
@EmbeddedKafka(partitions = 1, topics = {
        "aviation-registry-regulator-notice-request-topic",
        "aviation-registry-regulator-notice-response-topic"})
@Disabled
class AviationRegulatorNoticeEventConsumerIntegrationTest extends BaseIntegrationTest {

    static final String REQUEST_TOPIC =
            "aviation-registry-regulator-notice-request-topic";
    private static final String RESPONSE_TOPIC =
            "aviation-registry-regulator-notice-response-topic";

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TestAccountDataFactory dataFactory;
    @Autowired
    private Configuration freemarkerConfiguration;
    @Autowired
    private RegulatorNoticeTestHelper eventHelper;
    @Autowired
    private RegulatorNoticeRepository regulatorNoticeRepository;
    @Autowired
    private UploadedFilesRepository uploadedFilesRepository;

    private KafkaTemplate<String, RegulatorNoticeEvent> kafkaTemplate;
    private Consumer<String, RegulatorNoticeEventOutcome> responseConsumer;

    //Test data
    private TestAccountData data1;

    @BeforeAll
    void initData(@Autowired TestDataHelper helper) {
        data1 = dataFactory.nextAviationAccount();

        helper.createAircraftOperatorAccount(data1);

        // Producer
        var producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);

        kafkaTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(
                        producerProps,
                        new StringSerializer(),
                        new JsonSerializer<>()
                )
        );

        // Consumer with UNIQUE group id → no old messages
        JsonDeserializer<RegulatorNoticeEventOutcome> deserializer =
                new JsonDeserializer<>(RegulatorNoticeEventOutcome.class);
        deserializer.addTrustedPackages("*");

        responseConsumer = new DefaultKafkaConsumerFactory<>(
                consumerConfigs("regulator-notices-test-" + UUID.randomUUID()),
                new StringDeserializer(),
                deserializer
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(responseConsumer, RESPONSE_TOPIC);

        freemarkerConfiguration.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(),
                "/templates/email"
        );
    }


    private Map<String, Object> consumerConfigs(String groupId) {
        Map<String, Object> cfg =
                new HashMap<>(KafkaTestUtils.consumerProps(groupId, "false", embeddedKafkaBroker));
        cfg.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return cfg;
    }

    @AfterEach
    void cleanDatabase() {
        uploadedFilesRepository.deleteAll();
        regulatorNoticeRepository.deleteAll();
        entityManager.clear();
    }

    @Test
    void shouldConsumeRegulatorNoticeEventAndCreateRegulatorNotice() {

        final String correlationId = "corr-aviation-success";
        final String notificationType = "Installation Transfer";
        final String fileName = "regulatorNotice.pdf";
        byte[] pdfBytes = TestFileUtils.loadFileFromResources(
                "data/files/testing.pdf"
        );

        RegulatorNoticeEvent event = eventHelper.regulatorNoticeEvent(data1.getRegistryId(), notificationType, fileName, pdfBytes);

        ProducerRecord<String, RegulatorNoticeEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "aviation-key",
                        event,
                        List.of(new RecordHeader("Correlation-Id", correlationId.getBytes()))
                );

        kafkaTemplate.send(record);

        // Wait for DB update
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            entityManager.clear();

            final List<RegulatorNotice> regulatorNotices = regulatorNoticeRepository.findAll();

            assertThat(regulatorNotices).hasSize(1);
            final RegulatorNotice regulatorNotice = regulatorNotices.get(0);
            assertThat(regulatorNotice.getProcessType()).isEqualTo(notificationType);
            final List<UploadedFile> uploadedFiles = uploadedFilesRepository.findByTaskRequestId(regulatorNotice.getRequestId());
            assertThat(uploadedFiles).hasSize(1);
            final UploadedFile uploadedFile = uploadedFiles.get(0);
            assertThat(uploadedFile.getFileName()).isEqualTo(fileName);
            assertThat(uploadedFile.getTask().getId()).isEqualTo(regulatorNotice.getId());
        });

        // Read response messages and filter by correlation-id
        var records =
                KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(10));

        List<RegulatorNoticeEventOutcome> matching = new ArrayList<>();

        for (ConsumerRecord<String, RegulatorNoticeEventOutcome> rec : records) {
            Header header = rec.headers().lastHeader("Correlation-Id");
            if (header != null) {
                String corr = new String(header.value(), StandardCharsets.UTF_8);
                if (correlationId.equals(corr)) {
                    matching.add(rec.value());
                }
            }
        }

        assertThat(matching).hasSize(1);

        var response = matching.get(0);
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.SUCCESS);
        assertThat(response.getErrors()).isEmpty();
    }

    @Test
    void shouldConsumeRegulatorNoticesEventWithValidationErrors() {

        final String correlationId = "corr-aviation-success";

        RegulatorNoticeEvent event = eventHelper.regulatorNoticeEvent(data1.getRegistryId(), null, null, new byte[0]);

        ProducerRecord<String, RegulatorNoticeEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "aviation-key",
                        event,
                        List.of(new RecordHeader("Correlation-Id", correlationId.getBytes()))
                );

        kafkaTemplate.send(record);

        // Wait for DB update
        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
            entityManager.clear();

            final List<RegulatorNotice> regulatorNotices = regulatorNoticeRepository.findAll();
            assertThat(regulatorNotices).isEmpty();
            final List<UploadedFile> uploadedFiles = uploadedFilesRepository.findAll();
            assertThat(uploadedFiles).isEmpty();
        });

        // Read response messages and filter by correlation-id
        var records =
                KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(10));

        List<RegulatorNoticeEventOutcome> matching = new ArrayList<>();

        for (ConsumerRecord<String, RegulatorNoticeEventOutcome> rec : records) {
            Header header = rec.headers().lastHeader("Correlation-Id");
            if (header != null) {
                String corr = new String(header.value(), StandardCharsets.UTF_8);
                if (correlationId.equals(corr)) {
                    matching.add(rec.value());
                }
            }
        }

        assertThat(matching).hasSize(1);

        var response = matching.get(0);
        assertThat(response.getOutcome()).isEqualTo(IntegrationEventOutcome.ERROR);
        assertThat(response.getErrors().size()).isEqualTo(3);
    }
}
