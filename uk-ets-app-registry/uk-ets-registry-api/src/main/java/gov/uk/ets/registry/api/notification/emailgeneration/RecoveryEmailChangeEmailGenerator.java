package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.RecoveryEmailChangeNotification;
import gov.uk.ets.registry.api.notification.RecoveryEmailChangeNotificationProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RecoveryEmailChangeEmailGenerator extends EmailGenerator {

    private final NotificationProperties notificationProperties;
    private final RecoveryEmailChangeNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "recovery-email-change.ftl";
    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("securityCode", notification.getSecurityCode());
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
        RecoveryEmailChangeNotificationProperties properties = notificationProperties.getRecoveryEmailChange();
        this.subject(new EmailSentence(properties.getSubject()));
        return notification.enrichWithEmailContext(super.generate());
    }
}
