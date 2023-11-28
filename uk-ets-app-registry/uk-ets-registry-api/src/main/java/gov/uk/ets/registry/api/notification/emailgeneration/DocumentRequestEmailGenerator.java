package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.*;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DocumentRequestEmailGenerator extends EmailGenerator {
    private final NotificationProperties notificationProperties;
    private final DocumentRequestGroupNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "document-request.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("requestType", notification.getType().name());
        params.put("documentsRequestType", notification.getDocumentsRequestType().name());
        params.put("requestId", notification.getRequestId());
        params.put("userId", notification.getUserId());
        params.put("userFullName", notification.getUserFullName());
        params.put("accountName", notification.getAccountName());
        params.put("accountHolderName", notification.getAccountHolderName());

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

        if (GroupNotificationType.DOCUMENT_REQUEST.equals(notification.getType())) {
            DocumentRequestNotificationProperties documentRequestNotificationProperties =
                notificationProperties.getRequestDocument();
            this.subject(new EmailSentence(documentRequestNotificationProperties.getSubject()));

        } else if (GroupNotificationType.DOCUMENT_REQUEST_FINALISATION.equals(notification.getType())) {
            DocumentSubmitNotificationProperties documentSubmitNotificationProperties =
                notificationProperties.getSubmitDocument();
            this.subject(new EmailSentence(documentSubmitNotificationProperties.getSubject()));
        }

        return notification.enrichWithEmailContext(super.generate());
    }
}
