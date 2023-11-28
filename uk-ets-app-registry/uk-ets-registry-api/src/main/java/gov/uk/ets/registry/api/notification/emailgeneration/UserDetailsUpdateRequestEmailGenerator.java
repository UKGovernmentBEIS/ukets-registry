package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.UserDetailsUpdateNotification;
import gov.uk.ets.registry.api.notification.UserDetailsUpdateRequestNotificationProperties;

import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDetailsUpdateRequestEmailGenerator extends EmailGenerator {

    private final UserDetailsUpdateNotification groupNotification;
    private final NotificationProperties notificationProperties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "user-details-update-proposal.ftl";
    
    @Override
    Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("requestId", groupNotification.getRequestId());
        params.put("userId", groupNotification.getUserId());
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
 
        UserDetailsUpdateRequestNotificationProperties properties = notificationProperties.getUserDetailsUpdate();
        this.subject(new EmailSentence(properties.getSubject()));
        return groupNotification.enrichWithEmailContext(super.generate());
    }
}
