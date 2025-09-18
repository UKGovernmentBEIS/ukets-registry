package gov.uk.ets.registry.api.reconciliation.messaging;

import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * The UTTL outbound adapter which is responsible to send messages to UKTL.
 */
@Component
@Log4j2
public class UKTLOutboundAdapter {

    private final UKTLOutboundAdapterProperties properties;
    private final KafkaTemplate<String, ReconciliationSummary> reconciliationProducerTemplate;

    public UKTLOutboundAdapter(UKTLOutboundAdapterProperties properties,
                               @Qualifier("uktlReconciliationProducerTemplate")
                                   KafkaTemplate<String, ReconciliationSummary> reconciliationProducerTemplate) {
        this.properties = properties;
        this.reconciliationProducerTemplate = reconciliationProducerTemplate;
    }

    /**
     * Sends the {@link ReconciliationSummary} message to UKTL. The message is sent synchronously by waiting the kafka
     * answer in order to rollback the transaction in case of a kafka error.
     *
     * @param reconciliationSummary The message.
     */
    @Transactional
    public void sendMessage(ReconciliationSummary reconciliationSummary) {
        try {
            reconciliationProducerTemplate.send(properties.getReconciliationQuestionTopic(), reconciliationSummary)
                .get();
        } catch (InterruptedException exception) {
            log.error("Reconciliation summary got interrupted.");
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            log.error("Reconciliation summary failed to be send to the transaction log during initiation.");
            throw new RuntimeException(exception);
        }
    }
}
