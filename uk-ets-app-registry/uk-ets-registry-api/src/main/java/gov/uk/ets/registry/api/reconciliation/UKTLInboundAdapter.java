package gov.uk.ets.registry.api.reconciliation;

import gov.uk.ets.registry.api.reconciliation.service.ProcessReconciliationService;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for accepting UKTL messages during reconciliation process.
 */
@Component
@KafkaListener(
    topics = "${kafka.reconciliation-uktl.answer.topic:txlog.originating.reconciliation.answer.topic}",
    containerFactory = "uktlReconciliationConsumerFactory"
)
@RequiredArgsConstructor
@Log4j2
public class UKTLInboundAdapter {
    private final ProcessReconciliationService processReconciliationService;

    /**
     * Handles the incoming answer from the UK Reconciliation Log.
     *
     * @param message The message request.
     */
    @KafkaHandler
    public void handleAnswer(ReconciliationSummary message) {
        log.info("Received an answer for reconciliation: {}", message);
        processReconciliationService.completeReconciliation(message);
    }
}
