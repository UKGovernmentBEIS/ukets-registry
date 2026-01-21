package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.integration.AccountModificationOutcomeNotification;
import gov.uk.ets.registry.api.notification.integration.AccountModificationSuccessOutcomeNotificationProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Abstract base class for Integration Email Generators that share the same
 * structure but differ in template and notification types.
 */
@RequiredArgsConstructor
public class IntegrationAccountModificationEmailGenerator extends EmailGenerator {

    protected final AccountModificationSuccessOutcomeNotificationProperties properties;
    protected final AccountModificationOutcomeNotification notification;
    protected final Configuration freemarkerConfiguration;
    protected final MailConfiguration mailConfiguration;
    protected final String template;

    @Override
    Map<String, Object> params() {
        return notification.getMapParameters();
    }

    @Override
    String htmlTemplate() {
        return mailConfiguration.getHtmlTemplatesFolder() + template;
    }

    @Override
    String textTemplate() {
        return mailConfiguration.getTextTemplatesFolder() + template;
    }

    @Override
    Configuration freemarkerConfiguration() {
        return freemarkerConfiguration;
    }

    @Override
    public GroupNotification generate() {

        this.subject(new EmailSentence(properties.getSubject(), notification.getAccountFullIdentifier()));

        return notification.enrichWithEmailContext(super.generate());
    }
}
