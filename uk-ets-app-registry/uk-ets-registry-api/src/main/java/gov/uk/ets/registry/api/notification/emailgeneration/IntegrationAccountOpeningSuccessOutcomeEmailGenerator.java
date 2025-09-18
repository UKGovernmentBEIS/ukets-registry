package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.DateUtils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.integration.AccountOpeningSuccessOutcomeNotification;
import gov.uk.ets.registry.api.notification.integration.AccountOpeningSuccessOutcomeNotificationProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntegrationAccountOpeningSuccessOutcomeEmailGenerator extends EmailGenerator {

    private final AccountOpeningSuccessOutcomeNotificationProperties properties;
    private final AccountOpeningSuccessOutcomeNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "integration-account-opening.ftl";

    @Override
    Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("requestType", notification.getType().name());
        params.put("integrationPoint", notification.getIntegrationPoint());
        params.put("correlationId", notification.getCorrelationId());

        params.put("accountFullIdentifier", notification.getAccountFullIdentifier());
        params.put("emitterId", notification.getEmitterId());
        params.put("accountName", notification.getAccountName());
        params.put("accountType", notification.getAccountType());

        params.put("date", DateUtils.prettyCalendarDate(notification.getDate()));
        params.put("time", DateUtils.formatLondonZonedTime(notification.getDate()));
        params.put("sourceSystem", notification.getSourceSystem().name());
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

        this.subject(new EmailSentence(properties.getSubject(), notification.getAccountFullIdentifier()));

        return notification.enrichWithEmailContext(super.generate());
    }
}