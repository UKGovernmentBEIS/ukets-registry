package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.DateUtils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.RequestDeadlineNotification;
import gov.uk.ets.registry.api.notification.RequestDeadlineNotificationProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeadlineEmailGenerator extends EmailGenerator {

    private final RequestDeadlineNotificationProperties properties;
    private final RequestDeadlineNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "request-deadline.ftl";
    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("requestType", notification.getType().name());
        params.put("requestId", notification.getRequestId());
        params.put("deadline", DateUtils.prettyCalendarDate(notification.getDeadline()));
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

        if (GroupNotificationType.DEADLINE_REMINDER.equals(notification.getType())) {
            this.subject(new EmailSentence(properties.getReminderSubject()));

        } else if (GroupNotificationType.DEADLINE_UPDATE.equals(notification.getType())) {
            this.subject(new EmailSentence(properties.getUpdateSubject()));
        }

        return notification.enrichWithEmailContext(super.generate());
    }
}
