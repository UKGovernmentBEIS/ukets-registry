package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.EmailChangeNotificationProperties;

import java.util.HashMap;
import java.util.Map;

import gov.uk.ets.registry.api.user.profile.notification.EmailChangeApprovedNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailChangeApprovedEmailGenerator extends EmailGenerator {
    private final EmailChangeNotificationProperties properties;
    private final EmailChangeApprovedNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "email-change-approve.ftl";

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
        this.subject(new EmailSentence(properties.getApproveSubject()));
        return notification.enrichWithEmailContext(super.generate());
        // resume CPD analysis - CPD-ON
    }
}
