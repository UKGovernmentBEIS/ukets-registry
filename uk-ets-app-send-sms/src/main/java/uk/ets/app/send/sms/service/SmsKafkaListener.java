package uk.ets.app.send.sms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import uk.ets.app.send.sms.domain.SmsNotification;
import uk.gov.service.notify.NotificationClientException;

@Service
@RequiredArgsConstructor
@Log4j2
public class SmsKafkaListener {

    private final SmsService service;

    @KafkaListener(topics = "send.sms.topic", containerFactory = "kafkaListenerContainerFactory")
    public void sendSms(SmsNotification message) {
        try {
            log.info("Sending sms: {}", message);
            service.sendSms(message);
        } catch (NotificationClientException e) {
            log.warn("Failed to send sms...");
            throw new RuntimeException(e);
        }
    }
}
