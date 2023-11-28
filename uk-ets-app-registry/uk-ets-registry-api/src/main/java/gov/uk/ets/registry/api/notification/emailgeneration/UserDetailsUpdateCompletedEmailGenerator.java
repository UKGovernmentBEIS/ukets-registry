package gov.uk.ets.registry.api.notification.emailgeneration;

import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.UserDetailsUpdateCompletedNotificationProperties;
import gov.uk.ets.registry.api.notification.UserDetailsUpdateNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserDetailsUpdateCompletedEmailGenerator extends EmailGenerator {

	private final UserDetailsUpdateNotification groupNotification;
    private final NotificationProperties notificationProperties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "user-details-update-completed.ftl";
    
    @Override
    Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("requestId", groupNotification.getRequestId());
        params.put("userId", groupNotification.getUserId());
        params.put("taskOutcome", groupNotification.getTaskOutcome() != null ? 
        		lowercase(groupNotification.getTaskOutcome().toString()) : "");
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
    	
    	UserDetailsUpdateCompletedNotificationProperties properties = notificationProperties.getUserDetailsUpdateCompleted();
    	UserDetailsUpdateNotification notification = groupNotification;
    	this.subject(new EmailSentence(properties.getSubject(), 
    			notification.getTaskOutcome() != null ? lowercase(groupNotification.getTaskOutcome().toString()) : ""
    			));
        return notification.enrichWithEmailContext(super.generate());

    }
}
