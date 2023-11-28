package gov.uk.ets.registry.api.account.messaging;

import gov.uk.ets.registry.api.account.service.BalanceTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(
    topics = "txlog.originating.account-opening.answer.topic",
    groupId = "group.account-opening-uktl.group",
    containerFactory = "uktlAccountOpeningConsumerFactory"
)
@RequiredArgsConstructor
@Log4j2
public class UKTLIncomingAccountOpeningMessageListener {

    private final BalanceTransferService balanceTransferService;

    /**
     * Handles the incoming answer from the UK Transaction Log.
     *
     * @param accountOpeningAnswer The account opening answer received.
     */
    @KafkaHandler
    public void handleAnswer(UKTLAccountOpeningAnswer accountOpeningAnswer) {
        log.info("Received an account opening request answer from TL: {}", accountOpeningAnswer);
        if(accountOpeningAnswer.getOldIdentifier() != null) {
            balanceTransferService.createBalanceTransferTransaction(accountOpeningAnswer);
        }
    }
}
