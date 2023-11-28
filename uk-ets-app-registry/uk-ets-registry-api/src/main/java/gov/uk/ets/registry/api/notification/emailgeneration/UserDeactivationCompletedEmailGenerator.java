package gov.uk.ets.registry.api.notification.emailgeneration;

import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.UserDeactivationCompletedNotificationProperties;
import gov.uk.ets.registry.api.notification.UserDeactivationNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDeactivationCompletedEmailGenerator extends EmailGenerator {

	private final UserDeactivationNotification groupNotification;
    private final NotificationProperties notificationProperties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "user-deactivation-completed.ftl";
    
    @Override
    Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("requestId", groupNotification.getRequestId());
        params.put("userId", groupNotification.getUserId());
        
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
    	
    	UserDeactivationCompletedNotificationProperties properties = notificationProperties.getUserDeactivationCompleted();
        this.subject(new EmailSentence(properties.getSubject()));
        return groupNotification.enrichWithEmailContext(super.generate());

    }
}
