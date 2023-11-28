package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.EmailChangeNotificationProperties;
import gov.uk.ets.registry.api.user.profile.notification.EmailChangeRejectedNotification;
import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailChangeRejectedEmailGenerator extends EmailGenerator {
    private final EmailChangeRejectedNotification notification;
    private final EmailChangeNotificationProperties properties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "email-change-reject.ftl";

    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());

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
        // tell cpd to start ignoring code - CPD-OFF
        this.subject(new EmailSentence(properties.getRejectSubject()));
        return notification.enrichWithEmailContext(super.generate());
        // resume CPD analysis - CPD-ON
    }
}
