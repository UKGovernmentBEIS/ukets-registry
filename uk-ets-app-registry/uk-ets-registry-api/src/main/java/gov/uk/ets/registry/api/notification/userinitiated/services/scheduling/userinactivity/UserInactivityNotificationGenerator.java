package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.userinactivity;

import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.services.StringEmailTemplateProcessor;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationGenerator;
import gov.uk.ets.registry.api.notification.userinitiated.util.HtmlToPlainTextConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * Generates actual notification with recipients, subject, body (with parameters).
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UserInactivityNotificationGenerator implements NotificationGenerator {

    private final StringEmailTemplateProcessor templateProcessor;

    public IdentifiableEmailNotification generate(NotificationParameterHolder recipient, String emailSubject, String emailBody) {
        log.info("generating notification for: {}", recipient);
        // subject is of the form: "(<notification_instance_id>) <subject>'"
        String subject = String.format("(%s) %s", recipient.getNotificationInstanceId(), emailSubject);

        BaseNotificationParameters baseParams = recipient.getBaseNotificationParameters();

        // we cannot set null value to the map, so instead we set an empty string
        Map<String, Object> params = Map.of(
            "user", baseParams
        );

        // replace the ${user.Id} key
        String formattedLongText = emailBody.contains("${user.Id}") ?
            emailBody.replace("${user.Id}", "${user.urid}") :
            emailBody;
        String body = templateProcessor.processTemplate(formattedLongText, params);

        return IdentifiableEmailNotification.builder()
            // this is needed here to correlate with the channel when sending the notification
            .notificationId(recipient.getNotificationId())
            // this is needed for logging purposes
            .notificationInstanceId(recipient.getNotificationInstanceId())
            .recipients(Set.of(recipient.getEmail()))
            .subject(subject)
            .bodyHtml(body)
            .bodyPlain(HtmlToPlainTextConverter.convertHtmlToPlainText(body))
            .includeBcc(true)
            .build();
    }
}

