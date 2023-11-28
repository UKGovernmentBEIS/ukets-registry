package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.TrustedAccountDescriptionNotificationProperties;
import gov.uk.ets.registry.api.notification.TrustedAccountUpdateDescriptionGroupNotification;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TrustedAccountUpdateDescriptionEmailGenerator extends EmailGenerator {
    public static final String NO_VALUE = "no value";
    private final NotificationProperties notificationProperties;
    private final TrustedAccountUpdateDescriptionGroupNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "trusted-account-update.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("fullIdentifier", Utils.maskFullIdentifier(notification.getAccountFullIdentifier()));
        params.put("description",
            notification.getDescription() != null && !"".equals(notification.getDescription()) ?
                notification.getDescription() : NO_VALUE);
        params.put("accountIdentifier", notification.getAccountIdentifier());
        params.put("accountFullIdentifier", notification.getAccountFullIdentifier());

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
        TrustedAccountDescriptionNotificationProperties trustedAccountDescriptionNotificationProperties =
            notificationProperties.getTrustedAccountUpdateDescription();
        this.subject(new EmailSentence(
            trustedAccountDescriptionNotificationProperties.getSubject(),
            "submitted"
        ));
        return notification.enrichWithEmailContext(super.generate());
    }
}
