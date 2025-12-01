package gov.uk.ets.registry.api.notification.emailgeneration;

import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.emailgeneration.EmailGenerator.EmailSentence;
import gov.uk.ets.registry.api.notification.integration.IntegrationDisabledNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntegrationDisabledEmailGenerator extends EmailGenerator {

    private final IntegrationDisabledNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "integration-disabled.ftl";
	
	@Override
	Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("requestType", notification.getType().name());
        params.put("integrationPoint", notification.getIntegrationPoint());
        params.put("correlationId", notification.getCorrelationId());
        params.put("sourceSystem", notification.getSourceSystem().name());
        params.put("payload", notification.getPayload());
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

        this.subject(new EmailSentence(notification.subject()));

        return notification.enrichWithEmailContext(super.generate());
    }    
}
