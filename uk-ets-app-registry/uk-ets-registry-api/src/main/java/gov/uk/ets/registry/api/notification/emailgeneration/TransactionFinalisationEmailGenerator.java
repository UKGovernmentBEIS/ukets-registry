package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.TransactionNotificationProperties;
import gov.uk.ets.registry.api.notification.TransactionRelatedGroupNotification;
import gov.uk.ets.registry.api.task.domain.TaskCompletionStatus;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionFinalisationEmailGenerator extends EmailGenerator {
    private final NotificationProperties notificationProperties;
    private final TransactionRelatedGroupNotification groupNotification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "transaction-finalisation.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("taskOutcome", groupNotification.getTaskOutcome().name());
        params.put("isIssueAllowances", groupNotification.isIssueAllowances());
        params.put("approvalRequired", groupNotification.isApprovalRequired());
        params.put("units", groupNotification.getAmount());
        params.put("fullIdentifier", Utils. maskFullIdentifier(groupNotification.getAccountFullId()));
        params.put("transactionId", groupNotification.getTransactionId());
        params.put("requestId", groupNotification.getRequestId());

        if (TaskCompletionStatus.APPROVED.equals(groupNotification.getTaskOutcome())) {
            params.put("date", groupNotification.getEstimatedCompletionDate());
            params.put("time", groupNotification.getEstimatedCompletionTime());
        } else if (TaskCompletionStatus.REJECTED.equals(groupNotification.getTaskOutcome())) {
            params.put("rejectedBy", groupNotification.getRejectedBy());
        }

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
        TransactionNotificationProperties transactionNotificationProperties =
            notificationProperties.getTransaction();
        TransactionRelatedGroupNotification notification = groupNotification;

        this.subject(new EmailSentence(
            transactionNotificationProperties.getSubject(),
            lowercase(notification.getTaskOutcome() == TaskCompletionStatus.APPROVED ? "approval" : "rejection")
        ));
        return notification.enrichWithEmailContext(super.generate());
    }
}
