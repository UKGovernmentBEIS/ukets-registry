package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhoc.email;

import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.services.StringEmailTemplateProcessor;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationGenerator;
import gov.uk.ets.registry.api.notification.userinitiated.util.HtmlToPlainTextConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Generates actual notification with recipients, subject, body (with parameters).
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AdHocEmailNotificationGenerator implements NotificationGenerator {

    private final StringEmailTemplateProcessor templateProcessor;

    @Override
    public IdentifiableEmailNotification generate(NotificationParameterHolder recipient, String emailSubject, String emailBody) {
        String recipientEmail = (String) recipient.getCsvRowData().entrySet().iterator().next().getValue();
        log.info("generating ad hoc recipients email notification for: {}", recipientEmail);

        // subject is of the form: "(<notification_instance_id>) <subject>'"
        String subject = String.format("(%s) %s", recipient.getNotificationInstanceId(), emailSubject);
        String body = templateProcessor.processTemplate(emailBody, recipient.getCsvRowData());

        return IdentifiableEmailNotification.builder()
            .notificationId(recipient.getNotificationId())
            .notificationInstanceId(recipient.getNotificationInstanceId())
            .recipients(Set.of(recipientEmail))
            .subject(subject)
            .bodyHtml(body)
            .bodyPlain(HtmlToPlainTextConverter.convertHtmlToPlainText(body))
            .includeBcc(true)
            .build();
    }
}

