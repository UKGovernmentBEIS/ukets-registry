package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.UserDeactivationNotification;
import gov.uk.ets.registry.api.notification.UserDeactivationRequestNotificationProperties;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDeactivationRequestEmailGenerator extends EmailGenerator {

    private final UserDeactivationNotification groupNotification;
    private final NotificationProperties notificationProperties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "user-deactivation-request.ftl";
    
    @Override
    Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("requestId", groupNotification.getRequestId());
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("userId", groupNotification.getUserId());
        
        return params;
    }

    @Override
    String textTemplate() {
        return mailConfiguration.getTextTemplatesFolder() + TEMPLATE;
    }

    @Override
    String htmlTemplate() {
        return mailConfiguration.getHtmlTemplatesFolder() + TEMPLATE;
    }

    @Override
    Configuration freemarkerConfiguration() {
        return freemarkerConfiguration;
    }

    @Override
    public GroupNotification generate() {
 
        UserDeactivationRequestNotificationProperties properties = notificationProperties.getUserDeactivationRequest();
        this.subject(new EmailSentence(properties.getSubject()));
        return groupNotification.enrichWithEmailContext(super.generate());
    }
}
