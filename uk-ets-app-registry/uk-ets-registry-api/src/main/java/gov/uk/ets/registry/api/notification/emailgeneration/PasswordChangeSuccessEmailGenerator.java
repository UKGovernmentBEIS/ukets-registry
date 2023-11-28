package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.PasswordChangeNotificationProperties;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.api.user.profile.domain.PasswordChangeSuccessNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PasswordChangeSuccessEmailGenerator extends EmailGenerator {
    private final NotificationProperties notificationProperties;
    private final PasswordChangeSuccessNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "password-changed.ftl";

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
        // tell cpd to start ignoring code - CPD-OFF
        PasswordChangeNotificationProperties properties = notificationProperties.getPasswordChange();
        this.subject(new EmailSentence(properties.getSubject()));
        return notification.enrichWithEmailContext(super.generate());
        // resume CPD analysis - CPD-ON
    }
}
