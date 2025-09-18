package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.PaymentRequestGroupNotification;
import gov.uk.ets.registry.api.notification.PaymentRequestGroupNotificationProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentRequestEmailGenerator  extends EmailGenerator {

    private final PaymentRequestGroupNotificationProperties properties;
    private final PaymentRequestGroupNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "payment-request.ftl";
    
    @Override
    Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("requestType", notification.getType().name());
        params.put("requestId", notification.getRequestId());
        params.put("description", notification.getPaymentDescription());
        
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
        return notification.enrichWithEmailContext(super.generate());
    }    
}
