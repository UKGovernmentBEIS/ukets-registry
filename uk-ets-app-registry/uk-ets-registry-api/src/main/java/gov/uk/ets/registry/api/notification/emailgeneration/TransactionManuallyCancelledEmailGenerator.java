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

/**
 * Generates emails when manually cancelling a delayed transaction.
 */
@RequiredArgsConstructor
public class TransactionManuallyCancelledEmailGenerator extends EmailGenerator {
    private final NotificationProperties notificationProperties;
    private final TransactionRelatedGroupNotification groupNotification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "transaction-manually-cancelled.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("units", groupNotification.getAmount());
        params.put("fullIdentifier", Utils.maskFullIdentifier(groupNotification.getAccountFullId()));
        params.put("requestId", groupNotification.getRequestId());

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

        this.subject(new EmailSentence(
            transactionNotificationProperties.getSubject(), "cancellation")
        );
        return groupNotification.enrichWithEmailContext(super.generate());
    }
}
