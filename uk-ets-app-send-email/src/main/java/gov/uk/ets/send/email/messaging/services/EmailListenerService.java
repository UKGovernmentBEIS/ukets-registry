package gov.uk.ets.send.email.messaging.services;

import gov.uk.ets.send.email.messaging.config.EmailException;
import gov.uk.ets.send.email.messaging.domain.GroupNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailListenerService {

    @Value("${bcc.mailbox.address:null}")
    private String bccAddress;

    private final MailService mailService;

    @KafkaListener(topics = "send.email.topic", containerFactory = "kafkaListenerContainerFactory")
    public void sendEmail(GroupNotification message) throws EmailException {
        try {
            mailService.sendMail(
                message.getRecipients().toArray(new String[] {}),
                message.getSubject(),
                message.getBodyHtml(),
                message.getBodyPlain(),
                message.isIncludeBcc() ? bccAddress : null // the sender service decides if the email should be Bcc'ed
            );
        } catch (RuntimeException e) {
            log.error("Error while trying to send the email: {}", e.getMessage());
            throw new EmailException(e.getMessage());
        }
    }
}
