package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.EmergencyChangeNotificationProperties;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyOtpChangeTaskApprovedNotification;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmergencyOtpChangeTaskCompletedEmailGenerator extends EmailGenerator {
    private final EmergencyOtpChangeTaskApprovedNotification notification;
    private final NotificationProperties notificationProperties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "emergency-otp-change-approve.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("url", notification.getVerificationUrl());

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
        EmergencyChangeNotificationProperties properties =
            notificationProperties.getEmergencyOtpChangeTaskApproved();
        this.subject(new EmailSentence(properties.getSubject()));
        return notification.enrichWithEmailContext(super.generate());
    }
}
