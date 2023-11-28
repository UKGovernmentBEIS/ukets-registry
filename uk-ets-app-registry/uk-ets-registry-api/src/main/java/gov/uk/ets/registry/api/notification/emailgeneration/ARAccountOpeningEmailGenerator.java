package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.AccountOpeningNotificationProperties;
import gov.uk.ets.registry.api.notification.EmailChangeUserStatusNotification;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ARAccountOpeningEmailGenerator extends EmailGenerator {
    private final NotificationProperties notificationProperties;
    private final EmailChangeUserStatusNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "ar-account-opening.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
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
        AccountOpeningNotificationProperties accountOpeningNotificationProperties =
            notificationProperties.getAccountOpening();

        this.subject(new EmailSentence(accountOpeningNotificationProperties.getArSubject()));
        return notification.enrichWithEmailContext(super.generate());
    }
}
