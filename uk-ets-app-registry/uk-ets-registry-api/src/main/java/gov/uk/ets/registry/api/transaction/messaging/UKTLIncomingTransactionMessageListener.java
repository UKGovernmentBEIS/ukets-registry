package gov.uk.ets.registry.api.transaction.messaging;

import gov.uk.ets.registry.api.transaction.service.ProcessETSTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(
    topics = "txlog.originating.transaction.answer.topic",
    groupId = "group.transaction-uktl.group",
    containerFactory = "uktlTransactionConsumerFactory"
)
@RequiredArgsConstructor
@Log4j2
public class UKTLIncomingTransactionMessageListener {
    private final ProcessETSTransactionService processETSTransactionService;

    /**
     * Handles the incoming answer from the UK Transaction Log.
     *
     * @param uktlTransactionAnswer The message request.
     */
    @KafkaHandler
    public void handleAnswer(UKTLTransactionAnswer uktlTransactionAnswer) {
        log.info("Received an answer from transaction: {}", uktlTransactionAnswer);
        processETSTransactionService.process(uktlTransactionAnswer);
    }
}
