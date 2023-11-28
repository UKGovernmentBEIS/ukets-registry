package gov.uk.ets.registry.api.itl.reconciliation.messaging;

import java.io.Serializable;
import lombok.extern.log4j.Log4j2;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveAuditTrailRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveTotalsRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveUnitBlocksRequest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Log4j2
public class OutgoingItlReconciliationMessageProducer {

    private final KafkaTemplate<String, Serializable> itlReconciliationOutgoingKafkaTemplate;

    public OutgoingItlReconciliationMessageProducer(
        @Qualifier("itlReconciliationOutgoingKafkaTemplate")
            KafkaTemplate<String, Serializable> itlReconciliationOutgoingKafkaTemplate) {
        this.itlReconciliationOutgoingKafkaTemplate = itlReconciliationOutgoingKafkaTemplate;
    }

    @Transactional
    public void sendTotalsRequest(ReceiveTotalsRequest request) {
        try {
            itlReconciliationOutgoingKafkaTemplate
                .send(itlReconciliationOutgoingKafkaTemplate.getDefaultTopic(), request).get();
        } catch (Exception e) {
            log.error("ReceiveTotalsRequest message failed to be send: {}", request);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void sendUnitBlocksRequest(ReceiveUnitBlocksRequest request) {
        try {
            itlReconciliationOutgoingKafkaTemplate
                .send(itlReconciliationOutgoingKafkaTemplate.getDefaultTopic(), request).get();
        } catch (Exception e) {
            log.error("ReceiveUnitBlocksRequest message failed to be send: {}", request);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void sendAuditTrailRequest(ReceiveAuditTrailRequest request) {
        try {
            itlReconciliationOutgoingKafkaTemplate
                .send(itlReconciliationOutgoingKafkaTemplate.getDefaultTopic(), request).get();
        } catch (Exception e) {
            log.error("ReceiveAuditTrailRequest message failed to be send: {}", request);
            throw new RuntimeException(e);
        }
    }
}
