package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.TransactionNotificationProperties;
import gov.uk.ets.registry.api.notification.TransactionRelatedGroupNotification;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionCompletionEmailGenerator extends EmailGenerator {
    private final NotificationProperties notificationProperties;
    private final TransactionRelatedGroupNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "transaction-completion.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("amount", notification.getAmount());
        params.put("fullIdentifier", Utils.maskFullIdentifier(notification.getAccountFullId()));
        params.put("completed", notification.getTransactionOutcome().equalsIgnoreCase("completed"));
        params.put("requestId", notification.getRequestId());
        params.put("transactionId", notification.getTransactionId());

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

        this.subject(new EmailSentence(transactionNotificationProperties.getSubject(), ""));
        return notification.enrichWithEmailContext(super.generate());
    }
}
