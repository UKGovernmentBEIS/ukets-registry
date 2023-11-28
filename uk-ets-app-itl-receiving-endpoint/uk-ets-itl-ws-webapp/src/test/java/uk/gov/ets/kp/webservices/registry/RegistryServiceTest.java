package uk.gov.ets.kp.webservices.registry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.ets.kp.webservices.shared.enums.ResultIdentifier;
import uk.gov.ets.kp.webservices.shared.types.ITLNoticeRequest;
import uk.gov.ets.kp.webservices.shared.types.ITLNoticeResponse;
import uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationRequest;
import uk.gov.ets.kp.webservices.shared.types.InitiateReconciliationResponse;
import uk.gov.ets.kp.webservices.shared.types.MessageRequest;
import uk.gov.ets.kp.webservices.shared.types.MessageResponse;
import uk.gov.ets.kp.webservices.shared.types.NotificationRequest;
import uk.gov.ets.kp.webservices.shared.types.NotificationResponse;
import uk.gov.ets.kp.webservices.shared.types.ProposalRequest;
import uk.gov.ets.kp.webservices.shared.types.ProposalResponse;
import uk.gov.ets.kp.webservices.shared.types.ProposalTransaction;
import uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideTimeRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideTimeResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideTotalsRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse;
import uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksRequest;
import uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse;
import uk.gov.ets.kp.webservices.shared.types.ReconciliationResultRequest;
import uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse;

@ExtendWith(MockitoExtension.class)
class RegistryServiceTest {
    String noticesTopic = "notices-topic";
    String reconciliationTopic = "reconciliationTopic";
    @Mock
    KafkaTemplate kafkaTemplate;
    @Mock
    KafkaTopics kafkaTopics;

    RegistryService registryService;

    @BeforeEach
    void init() {
        registryService = new RegistryService(kafkaTemplate, kafkaTopics);
    }

    @Test
    void provideTime() {
        // given
        ProvideTimeRequest request = mock(ProvideTimeRequest.class);
        // when
        ProvideTimeResponse response = registryService.provideTime(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    @Test
    void acceptMessage() {
        // given
        MessageRequest request = mock(MessageRequest.class);
        // when
        MessageResponse response = registryService.acceptMessage(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    @Test
    void acceptProposal() {
        // given
        ProposalRequest request = mock(ProposalRequest.class,Mockito.RETURNS_DEEP_STUBS);
        when(request.getProposedTransaction().getTransactionIdentifier()).thenReturn("GB919809");
        // when
        ProposalResponse response = registryService.acceptProposal(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    @Test
    void acceptNotification() {
        // given
        NotificationRequest request = mock(NotificationRequest.class);
        // when
        NotificationResponse response = registryService.acceptNotification(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    @Test
    void acceptITLNotice() {
        // given
        given(kafkaTopics.getNoticesKafkaTopic()).willReturn(noticesTopic);
        ITLNoticeRequest request = mock(ITLNoticeRequest.class);
        // when
        ITLNoticeResponse response = registryService.acceptITLNotice(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    @Test
    void initiateReconciliation() {
        // given
        given(kafkaTopics.getReconciliationInTopic()).willReturn(reconciliationTopic);
        InitiateReconciliationRequest request = mock(InitiateReconciliationRequest.class);
        // when
        InitiateReconciliationResponse response = registryService.initiateReconciliation(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    @Test
    void receiveReconciliationResult() {
        // given
        given(kafkaTopics.getReconciliationInTopic()).willReturn(reconciliationTopic);
        ReconciliationResultRequest request = mock(ReconciliationResultRequest.class);
        // when
        ReconciliationResultResponse response = registryService.receiveReconciliationResult(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    @Test
    void provideAuditTrail() {
        // given
        given(kafkaTopics.getReconciliationInTopic()).willReturn(reconciliationTopic);
        ProvideAuditTrailRequest request = mock(ProvideAuditTrailRequest.class);
        // when
        ProvideAuditTrailResponse response = registryService.provideAuditTrail(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    @Test
    void provideTotals() {
        // given
        given(kafkaTopics.getReconciliationInTopic()).willReturn(reconciliationTopic);
        ProvideTotalsRequest request = mock(ProvideTotalsRequest.class);
        // when
        ProvideTotalsResponse response = registryService.provideTotals(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    @Test
    void provideUnitBlocks() {
        // given
        given(kafkaTopics.getReconciliationInTopic()).willReturn(reconciliationTopic);
        ProvideUnitBlocksRequest request = mock(ProvideUnitBlocksRequest.class);
        // when
        ProvideUnitBlocksResponse response = registryService.provideUnitBlocks(request);
        // then
        verifyResultIdentifier(response.getResultIdentifier());
    }

    private void verifyResultIdentifier(int resultIdentifier) {
        assertEquals(ResultIdentifier.SOAP_MESSAGE_ACCEPTED.getCode(), resultIdentifier);
    }
}