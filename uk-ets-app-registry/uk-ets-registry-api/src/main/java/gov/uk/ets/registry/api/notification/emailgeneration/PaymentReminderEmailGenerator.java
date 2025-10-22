package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.CurrencyUtils;
import gov.uk.ets.registry.api.common.DateUtils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.PaymentReminderNotification;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentReminderEmailGenerator extends EmailGenerator {

    private final PaymentReminderNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "payment-reminder.ftl";
    
    @Override
    public Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("initiationDate", DateUtils.prettyCalendarDate(notification.getInitiationDate()));
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("amount", CurrencyUtils.prettyCurrency(notification.getAmount()));
        params.put("requestId", Long.toString(notification.getRequestId()));
        params.put("description", notification.getDescription());
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
