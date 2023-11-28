package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.LoginErrorNotification;
import gov.uk.ets.registry.api.notification.LoginErrorNotificationProperties;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginErrorEmailGenerator extends EmailGenerator {

    private final LoginErrorNotification loginErrorNotification;
    private final LoginErrorNotificationProperties properties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "login-error.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("url", loginErrorNotification.getUrl());
        params.put("date", Utils.formatDay(LocalDateTime.now()));
        params.put("time", Utils.formatTime(LocalDateTime.now()));

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
        this.subject(new EmailSentence(properties.getSubject()));
        return loginErrorNotification.enrichWithEmailContext(super.generate());
    }
}
