package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.AccountManagementNotificationProperties;
import gov.uk.ets.registry.api.notification.AccountUpdateGroupNotification;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountUpdateEmailGenerator extends EmailGenerator {
    private final NotificationProperties notificationProperties;
    private final AccountUpdateGroupNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "account-update.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("requestId", notification.getRequestId());
        params.put("requestType", AccountManagementNotificationProperties.ACCOUNT_MANAGEMENT_REQUEST_TYPES
            .get(notification.getRequestType()));
        params.put("fullIdentifier", Utils.maskFullIdentifier(notification.getAccountFullIdentifier()));
        params.put("taskOutcome",
            notification.getTaskOutcome() != null ? lowercase(notification.getTaskOutcome().toString()) : "");

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
        AccountManagementNotificationProperties accountManagementNotificationProperties =
            notificationProperties.getAccountManagement();
        String taskOutcome = notification.getTaskOutcome() == TaskOutcome.APPROVED ? "accepted" : "rejected";
        this.subject(new EmailSentence(
            accountManagementNotificationProperties.getSubject(),
            notification.getTaskOutcome() != null ? taskOutcome : "submitted"
        ));
        return notification.enrichWithEmailContext(super.generate());
    }
}
