package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.EmergencyChangeNotificationProperties;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyPasswordOtpChangeRequestedNotification;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmergencyPasswordOtpChangeRequestEmailGenerator extends EmailGenerator {
    private final EmergencyPasswordOtpChangeRequestedNotification notification;
    private final NotificationProperties notificationProperties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "emergency-password-otp-change.ftl";

    // tell cpd to start ignoring code - CPD-OFF
    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("url", notification.getVerificationUrl());
        params.put("expiration", notification.getExpiration());

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
        EmergencyChangeNotificationProperties properties = notificationProperties.getEmergencyPasswordOtpChange();
        this.subject(new EmailSentence(properties.getSubject()));
        return notification.enrichWithEmailContext(super.generate());
    }
    // resume CPD analysis - CPD-ON
}
