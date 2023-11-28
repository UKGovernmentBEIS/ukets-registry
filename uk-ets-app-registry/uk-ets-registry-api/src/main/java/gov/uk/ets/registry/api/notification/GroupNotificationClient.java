package gov.uk.ets.registry.api.notification;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.emailgeneration.EmailGeneratorSelector;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Client that sends {@link GroupNotification} objects.
 */
@Service
public class GroupNotificationClient {

    @Value("${kafka.group.notification.topic}")
    private String groupNotificationTopic;


    private final KafkaTemplate<String, Serializable> groupNotificationProducerTemplate;

    private final NotificationProperties notificationProperties;
    private final MailConfiguration mailConfiguration;
    private final Configuration freemarkerConfiguration;

    public GroupNotificationClient(@Qualifier("groupNotificationProducerTemplate")
                                       KafkaTemplate<String, Serializable> groupNotificationProducerTemplate,
                                   NotificationProperties notificationProperties,
                                   MailConfiguration mailConfiguration,
                                   Configuration freemarkerConfiguration) {
        this.groupNotificationProducerTemplate = groupNotificationProducerTemplate;
        this.notificationProperties = notificationProperties;
        this.mailConfiguration = mailConfiguration;
        this.freemarkerConfiguration = freemarkerConfiguration;
    }

    /**
     * Emits a {@link GroupNotification} that is, sends an object to the related groupNotificationTopic.
     *
     * @param groupNotification the notification object
     */
    public void emitGroupNotification(GroupNotification groupNotification) {
        if (!groupNotification.recipients().isEmpty()) {
            GroupNotification emailTemplate = (GroupNotification) EmailGeneratorSelector
                .select(groupNotification, notificationProperties, freemarkerConfiguration, mailConfiguration)
                .generate();
            if (valid(emailTemplate)) {
                groupNotificationProducerTemplate.send(groupNotificationTopic, emailTemplate);
            }
        }
    }

    /**
     * Emits an already generate notification.
     * (user defined notifications are generated separately from rest of notifications).
     */
    public void emitUserDefinedNotification(EmailNotification notification) {
        groupNotificationProducerTemplate.send(groupNotificationTopic, notification);
    }

    private boolean valid(GroupNotification groupNotification) {
        return groupNotification.type() != null
            && groupNotification.recipients() != null
            && !groupNotification.recipients().isEmpty();
    }
}
