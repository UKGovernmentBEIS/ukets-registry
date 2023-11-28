package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.DocumentRequestReminderGroupNotification;
import gov.uk.ets.registry.api.notification.DocumentRequestReminderNotificationProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class DocumentRequestReminderEmailGenerator extends EmailGenerator {

    private final DocumentRequestReminderNotificationProperties properties;
    private final DocumentRequestReminderGroupNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "document-request-reminder.ftl";

    // tell cpd to start ignoring code - CPD-OFF
    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("requestId", notification.getRequestId());
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        return params;
    }

    @Override
    String htmlTemplate() {
        return mailConfiguration.getHtmlTemplatesFolder() + TEMPLATE;
    }

    @Override
    String textTemplate() {
        return mailConfiguration.getTextTemplatesFolder() + TEMPLATE;
    }

    @Override
    Configuration freemarkerConfiguration() {
        return freemarkerConfiguration;
    }

    @Override
    public GroupNotification generate() {
        this.subject(new EmailSentence(properties.getSubject()));
        return notification.enrichWithEmailContext(super.generate());
    }
}
