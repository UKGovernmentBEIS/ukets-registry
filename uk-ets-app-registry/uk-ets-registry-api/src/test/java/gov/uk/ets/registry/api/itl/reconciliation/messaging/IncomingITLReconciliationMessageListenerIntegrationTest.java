package gov.uk.ets.registry.api.itl.reconciliation.messaging;


import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.END_BLOCK_1;
import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.END_BLOCK_2;
import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.END_BLOCK_3;
import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.ORIGINATING_COUNTRY_CODE;
import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.START_BLOCK_1;
import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.START_BLOCK_2;
import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.START_BLOCK_3;
import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.TEST_ACCOUNT_ID_1;
import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.TEST_ACCOUNT_ID_2;
import static gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper.TEST_RECONCILIATION_ID;
import static gov.uk.ets.registry.api.itl.reconciliation.messaging.ITLReconciliationMessagingConfiguration.DEFAULT_ITL_RECONCILIATION_OUT_TOPIC;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.common.test.itl.IntegrationTestBase;
import gov.uk.ets.registry.api.helper.persistence.ITLReconciliationHelper;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLSnapshotBlockRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLSnapshotLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.service.ITLReconciliationProvideTotalsService;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationPhase;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.lib.commons.kyoto.types.ProvideTotalsRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProvideUnitBlocksRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveTotalsRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveUnitBlocksRequest;
import uk.gov.ets.lib.commons.kyoto.types.Total;
import uk.gov.ets.lib.commons.kyoto.types.UnitBlock;
import uk.gov.ets.lib.commons.kyoto.types.UnitBlockRequest;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Disabled(value = "Tests are often failing in Jenkins. Never fail locally so there is no easy way to reproduce and fix.")
class IncomingITLReconciliationMessageListenerIntegrationTest extends IntegrationTestBase {

    @Value("${kafka.itl.reconciliation.consumer.json.trusted.package:*}")
    private String trustedPackages;

    private static final long DEFAULT_CONSUMER_TIMEOUT = 60000;
    private static final String TEST_REGISTRY_ID = "UK-REGISTRY-ID";

    @Autowired
    @Qualifier("itlReconciliationIncomingKafkaTemplate")
    KafkaTemplate<String, Serializable> itlReconciliationIncomingKafkaTemplate;

    @Autowired
    ITLReconciliationLogRepository reconciliationLogRepository;

    @Autowired
    ITLSnapshotLogRepository snapshotLogRepository;

    @Autowired
    ITLSnapshotBlockRepository snapshotBlockRepository;

    @SpyBean
    ITLReconciliationProvideTotalsService provideTotalsService;

    @Autowired
    ITLReconciliationHelper reconciliationHelper;

    private static Consumer<String, Serializable> consumer;


    @BeforeEach
    void setUp() {
        setupTestListener();
        reconciliationHelper.setupTestData();
    }

    @AfterEach
    public void tearDown() {
        tearDownTestListener();
    }

    @Test
    // The test will not work if no transaction is in process
    @Transactional
    public void shouldCreateTotals() {

        sendProvideTotalsRequest(createProvideTotalsRequest(TEST_RECONCILIATION_ID));

        ConsumerRecord<String, Serializable> singleRecord =
            KafkaTestUtils.getSingleRecord(consumer, DEFAULT_ITL_RECONCILIATION_OUT_TOPIC, DEFAULT_CONSUMER_TIMEOUT);

        assertThat(singleRecord).isNotNull();
        ReceiveTotalsRequest receiveTotalsRequest = (ReceiveTotalsRequest) singleRecord.value();
        assertThat(receiveTotalsRequest.getTotals()).hasSize(2);
        assertThat(receiveTotalsRequest.getReconciliationIdentifier()).isEqualTo(TEST_RECONCILIATION_ID);
        assertThat(receiveTotalsRequest.getFrom()).isEqualTo(Constants.KYOTO_REGISTRY_CODE);
        assertThat(receiveTotalsRequest.getTo()).isEqualTo(Constants.ITL_TO);

        assertThat(receiveTotalsRequest.getTotals()).extracting(Total::getUnitCount).containsOnly(20L, 25L);
        assertThat(receiveTotalsRequest.getTotals()).extracting(Total::getAccountIdentifier)
            .containsOnly(null, null);

        Optional<ITLReconciliationLog> optionalLog = reconciliationLogRepository.findById(TEST_RECONCILIATION_ID);
        assertThat(optionalLog).isPresent();
        ITLReconciliationLog reconciliationLog = optionalLog.get();
        assertThat(reconciliationLog.getReconPhaseCode()).isEqualTo(ITLReconciliationPhase.TOTALS);

        assertThat(reconciliationLog.getReconciliationStatusHistories()).hasSize(1);
        ITLReconciliationStatusHistory historyEntry =
            reconciliationLog.getReconciliationStatusHistories().get(0);
        assertThat(historyEntry.getReconStatus()).isEqualTo(ITLReconciliationStatus.INITIATED);
    }


    @Test
    // The test will not work if no transaction is in process
    @Transactional
    public void shouldFailWhenNonExistingReconciliationIdIsProvided() {

        sendProvideTotalsRequest(createProvideTotalsRequest("non existing id"));

        Assert.assertThrows(Exception.class, () -> {
            KafkaTestUtils.getSingleRecord(consumer, DEFAULT_ITL_RECONCILIATION_OUT_TOPIC, DEFAULT_CONSUMER_TIMEOUT);
        });
        Optional<ITLReconciliationLog> reconciliationLog = reconciliationLogRepository.findById(TEST_RECONCILIATION_ID);
        assertThat(reconciliationLog).isPresent();
        assertThat(reconciliationLog.get().getReconciliationStatusHistories()).hasSize(0);
    }

    @Test
    // The test will not work if no transaction is in process
    @Transactional
    public void shouldRetryIfSnapshotIsNotCreated() {

        reconciliationHelper.deleteSnapshotData();

        ProvideTotalsRequest provideTotalsRequest = createProvideTotalsRequest(TEST_RECONCILIATION_ID);
        sendProvideTotalsRequest(provideTotalsRequest);

        Assert.assertThrows(IllegalStateException.class, () -> {
            ConsumerRecord<String, Serializable> singleRecord = KafkaTestUtils
                .getSingleRecord(consumer, DEFAULT_ITL_RECONCILIATION_OUT_TOPIC, DEFAULT_CONSUMER_TIMEOUT);
            log.info("*****************SINGLE RECORD*************************");
            log.info(singleRecord);
        });

        // 9 is checked here because of Dead Letter Topic retry policy which at the moment is 3.
        // so for every message that an exception is thrown, it will be retried 3 times and for each time
        // there is a method level retry for three times
        verify(provideTotalsService, times(9)).provideTotals(provideTotalsRequest);

    }

    @Test
    // The test will not work if no transaction is in process
    @Transactional
    @DisplayName("should provide all unit blocks after an initial calculate totals request")
    public void shouldProvideAllUnitBlocksAfterTotalsRequest() {
        // first send a provide totals request
        sendProvideTotalsRequest(createProvideTotalsRequest(TEST_RECONCILIATION_ID));
        KafkaTestUtils.getSingleRecord(consumer, DEFAULT_ITL_RECONCILIATION_OUT_TOPIC, DEFAULT_CONSUMER_TIMEOUT);

        sendProvideUnitBlocksRequest(createProvideUnitBlocksRequest(TEST_RECONCILIATION_ID));
        ConsumerRecord<String, Serializable> singleRecord =
            KafkaTestUtils.getSingleRecord(consumer, DEFAULT_ITL_RECONCILIATION_OUT_TOPIC, DEFAULT_CONSUMER_TIMEOUT);

        assertThat(singleRecord).isNotNull();
        ReceiveUnitBlocksRequest receiveUnitBlocksRequest = (ReceiveUnitBlocksRequest) singleRecord.value();

        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).hasSize(3);
        assertThat(receiveUnitBlocksRequest.getReconciliationIdentifier()).isEqualTo(TEST_RECONCILIATION_ID);
        assertThat(receiveUnitBlocksRequest.getFrom()).isEqualTo(Constants.KYOTO_REGISTRY_CODE);
        assertThat(receiveUnitBlocksRequest.getTo()).isEqualTo(Constants.ITL_TO);

        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).extracting(UnitBlock::getUnitSerialBlockStart)
            .containsOnly(START_BLOCK_1, START_BLOCK_2, START_BLOCK_3);
        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).extracting(UnitBlock::getUnitSerialBlockEnd)
            .containsOnly(END_BLOCK_1, END_BLOCK_2, END_BLOCK_3);
        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).extracting(UnitBlock::getAccountIdentifier)
            .containsOnly(TEST_ACCOUNT_ID_1, TEST_ACCOUNT_ID_2);
        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).extracting(UnitBlock::getOriginatingRegistryCode)
            .containsOnly(ORIGINATING_COUNTRY_CODE);

        ITLReconciliationLog itlReconciliationLog = reconciliationLogRepository.findById(TEST_RECONCILIATION_ID).get();
        assertThat(itlReconciliationLog.getReconPhaseCode()).isEqualTo(ITLReconciliationPhase.UNIT_BLOCK_DETAIL);

        ITLReconciliationStatusHistory latestHistoryEntry = itlReconciliationLog.getLatestHistoryEntry();
        assertThat(latestHistoryEntry.getReconStatus()).isEqualTo(ITLReconciliationStatus.ITL_TOTAL_INCON);
    }

    @Test
    // The test will not work if no transaction is in process
    @Transactional
    @DisplayName("should provide unit blocks for specified account/ unit types and period when this is an initial request")
    public void shouldProvideUnitBlocksForSpecificAccountTypeUnitTypeAndPeriodWithoutTotalsRequest() {

        ProvideUnitBlocksRequest provideUnitBlocksRequest = createProvideUnitBlocksRequest(TEST_RECONCILIATION_ID);
        UnitBlockRequest unitBlockRequest = new UnitBlockRequest();
        unitBlockRequest.setUnitType(UnitType.RMU.getPrimaryCode());
        unitBlockRequest.setAccountType(KyotoAccountType.PERSON_HOLDING_ACCOUNT.getCode());
        unitBlockRequest.setAccountCommitPeriod(CommitmentPeriod.CP1.getCode());
        provideUnitBlocksRequest.setUnitBlockRequests(new UnitBlockRequest[] {unitBlockRequest});

        sendProvideUnitBlocksRequest(provideUnitBlocksRequest);

        ConsumerRecord<String, Serializable> singleRecord =
            KafkaTestUtils.getSingleRecord(consumer, DEFAULT_ITL_RECONCILIATION_OUT_TOPIC, DEFAULT_CONSUMER_TIMEOUT);

        assertThat(singleRecord).isNotNull();
        ReceiveUnitBlocksRequest receiveUnitBlocksRequest = (ReceiveUnitBlocksRequest) singleRecord.value();

        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).hasSize(1);
        assertThat(receiveUnitBlocksRequest.getReconciliationIdentifier()).isEqualTo(TEST_RECONCILIATION_ID);
        assertThat(receiveUnitBlocksRequest.getFrom()).isEqualTo(Constants.KYOTO_REGISTRY_CODE);
        assertThat(receiveUnitBlocksRequest.getTo()).isEqualTo(Constants.ITL_TO);

        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).extracting(UnitBlock::getUnitSerialBlockStart)
            .containsOnly(START_BLOCK_2);
        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).extracting(UnitBlock::getUnitSerialBlockEnd)
            .containsOnly(END_BLOCK_2);
        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).extracting(UnitBlock::getAccountIdentifier)
            .containsOnly(TEST_ACCOUNT_ID_2);
        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).extracting(UnitBlock::getOriginatingRegistryCode)
            .containsOnly(ORIGINATING_COUNTRY_CODE);

        ITLReconciliationLog itlReconciliationLog = reconciliationLogRepository.findById(TEST_RECONCILIATION_ID).get();
        assertThat(itlReconciliationLog.getReconPhaseCode()).isEqualTo(ITLReconciliationPhase.UNIT_BLOCK_DETAIL);

        ITLReconciliationStatusHistory latestHistoryEntry = itlReconciliationLog.getLatestHistoryEntry();
        assertThat(latestHistoryEntry.getReconStatus()).isEqualTo(ITLReconciliationStatus.INITIATED);
    }


    private ProvideTotalsRequest createProvideTotalsRequest(String testReconciliationId) {
        ProvideTotalsRequest provideTotalsRequest = new ProvideTotalsRequest();
        provideTotalsRequest.setReconciliationIdentifier(testReconciliationId);
        provideTotalsRequest
            .setByAccountFlag(ITLReconciliationProvideTotalsService.PROVIDE_TOTALS_WITHOUT_ACCOUNT_IDENTIFIER);
        provideTotalsRequest.setTo(TEST_REGISTRY_ID);
        return provideTotalsRequest;
    }

    private void sendProvideTotalsRequest(
        ProvideTotalsRequest provideTotalsRequest) {
        // because of spring proxy issue, we need to get default topic from template
        itlReconciliationIncomingKafkaTemplate.send(
            itlReconciliationIncomingKafkaTemplate.getDefaultTopic(), provideTotalsRequest)
            .addCallback(this::callback, ex -> log.error("failed to send message", ex));
    }


    private void sendProvideUnitBlocksRequest(ProvideUnitBlocksRequest provideUnitBlocksRequest) {
        itlReconciliationIncomingKafkaTemplate.send(
            itlReconciliationIncomingKafkaTemplate.getDefaultTopic(), provideUnitBlocksRequest)
            .addCallback(this::callback, ex -> log.error("failed to send message", ex));
    }

    private ProvideUnitBlocksRequest createProvideUnitBlocksRequest(String testReconciliationId) {
        ProvideUnitBlocksRequest provideUnitBlocksRequest = new ProvideUnitBlocksRequest();
        provideUnitBlocksRequest.setReconciliationIdentifier(testReconciliationId);
        provideUnitBlocksRequest.setTo(TEST_REGISTRY_ID);
        return provideUnitBlocksRequest;
    }

    private void setupTestListener() {
        Map<String, Object> consumerProperties = getConsumerProperties();
        JsonDeserializer<Serializable> jsonDeserializer =
            new JsonDeserializer<>(Serializable.class);
        jsonDeserializer.addTrustedPackages(trustedPackages);
        DefaultKafkaConsumerFactory<String, Serializable> cf =
            new DefaultKafkaConsumerFactory<>(consumerProperties, new StringDeserializer(),
                jsonDeserializer);

        consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, DEFAULT_ITL_RECONCILIATION_OUT_TOPIC);
    }

    private void tearDownTestListener() {
        consumer.close();
    }
}
