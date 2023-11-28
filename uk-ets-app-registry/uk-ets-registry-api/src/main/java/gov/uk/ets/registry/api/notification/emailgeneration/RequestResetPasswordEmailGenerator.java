package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.RequestResetPasswordLinkNotification;
import gov.uk.ets.registry.api.notification.RequestResetPasswordNotificationProperties;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RequestResetPasswordEmailGenerator extends EmailGenerator {
    private final RequestResetPasswordLinkNotification groupNotification;
    private final RequestResetPasswordNotificationProperties properties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "reset-password.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("url", groupNotification.getResetPasswordUrl());
        params.put("expiration", groupNotification.getExpiration());

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

        this.subject(new EmailSentence(properties.getLinkSubject()));
        return groupNotification.enrichWithEmailContext(super.generate());
    }
}
