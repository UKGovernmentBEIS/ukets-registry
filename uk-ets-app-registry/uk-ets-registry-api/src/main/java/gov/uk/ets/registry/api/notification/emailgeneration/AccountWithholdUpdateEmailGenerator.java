package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.AccountWithholdUpdateGroupNotification;
import gov.uk.ets.registry.api.notification.AccountWithholdUpdateNotificationProperties;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class AccountWithholdUpdateEmailGenerator extends EmailGenerator {

    private final NotificationProperties notificationProperties;
    private final AccountWithholdUpdateGroupNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "account-withhold-update.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("integrationPoint", notification.getIntegrationPoint().name());
        params.put("registryId", notification.getRegistryId());
        params.put("withholdFlag", notification.getWithholdFlag().toString());
        params.put("year", notification.getYear().toString());

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
        AccountWithholdUpdateNotificationProperties accountWithholdUpdateNotificationProperties =
                notificationProperties.getWithholdUpdate();

        this.subject(new EmailSentence(accountWithholdUpdateNotificationProperties.getSubject(),
                        notification.getIntegrationPoint().name(), notification.getRegistryId()
                )
        );
        return notification.enrichWithEmailContext(super.generate());
    }
}
