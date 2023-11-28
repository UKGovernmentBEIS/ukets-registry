package gov.uk.ets.registry.api.messaging;

import gov.uk.ets.registry.api.messaging.domain.AccountNotification;
import java.io.Serializable;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UktlAccountNotifyMessageService {
    private final KafkaTemplate<String, Serializable> producerTemplate;

    @Value("${spring.kafka.template.uktl.notification.topic}")
    private String transactionLogNotificationOut;

    public UktlAccountNotifyMessageService(
        KafkaTemplate<String, Serializable> producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    /**
     * Sends an account opening notification.
     *
     * @param accountNotification The account notification.
     */
    public void sendAccountOpeningNotification(AccountNotification accountNotification) {
        log.info("Sending an account opening notification {}", accountNotification);
        try {
            producerTemplate.send(transactionLogNotificationOut, accountNotification).get();
        } catch (Exception e) {
            log.error("AccountNotification message failed to be send: {}", accountNotification);
            throw new RuntimeException(e);
        }
    }
}
