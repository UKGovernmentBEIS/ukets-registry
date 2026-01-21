package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.AccountSendInvitationGroupNotification;
import gov.uk.ets.registry.api.notification.AccountSendInvitationNotificationProperties;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class AccountSendInvitationEmailGenerator extends EmailGenerator {

    private final NotificationProperties notificationProperties;
    private final AccountSendInvitationGroupNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String METS_CONTACT_TEMPLATE = "send-invitation-to-mets-contacts.ftl";
    private static final String REGISTRY_CONTACT_TEMPLATE = "send-invitation-to-registry-contacts.ftl";

    @Override
    Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("contactFullName", notification.getContactFullName());
        params.put("operatorName", notification.getOperatorName());
        params.put("accountType", notification.getAccountType());
        params.put("registryAccountNumber", notification.getAccountNumber());
        params.put("emitterId", notification.getEmitterId());
        params.put("accountClaimCode", notification.getAccountClaimCode());

        return params;
    }

    @Override
    String htmlTemplate() {
        return notification.getIsMetsContact() ? mailConfiguration.getHtmlTemplatesFolder() + METS_CONTACT_TEMPLATE :
                mailConfiguration.getHtmlTemplatesFolder() + REGISTRY_CONTACT_TEMPLATE;
    }

    @Override
    String textTemplate() {
        return notification.getIsMetsContact() ? mailConfiguration.getTextTemplatesFolder() + METS_CONTACT_TEMPLATE :
                mailConfiguration.getTextTemplatesFolder() + REGISTRY_CONTACT_TEMPLATE;
    }

    @Override
    Configuration freemarkerConfiguration() {
        return freemarkerConfiguration;
    }

    @Override
    public GroupNotification generate() {
        AccountSendInvitationNotificationProperties accountSendInvitationNotificationProperties =
                notificationProperties.getSendInvitation();

        this.subject(new EmailSentence(accountSendInvitationNotificationProperties.getSubject()));
        return notification.enrichWithEmailContext(super.generate());
    }
}
