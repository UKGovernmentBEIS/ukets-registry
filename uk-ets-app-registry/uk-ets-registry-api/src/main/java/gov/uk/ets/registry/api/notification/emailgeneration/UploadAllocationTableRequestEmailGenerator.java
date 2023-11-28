package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.file.upload.allocationtable.notification.UploadAllocationTableEmailNotification;
import gov.uk.ets.registry.api.notification.UploadAllocationTableNotificationsProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UploadAllocationTableRequestEmailGenerator extends EmailGenerator {

    private final UploadAllocationTableEmailNotification notification;
    private final UploadAllocationTableNotificationsProperties properties;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;
    
    private static final String TEMPLATE = "upload-allocation-table-request.ftl";
    
    // tell cpd to start ignoring code - CPD-OFF
    @Override
    Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("etrAddress", mailConfiguration.getEtrAddress());
        params.put("allocationType", notification.getAllocationType());
        params.put("requestId", notification.getRequestId());
        
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
        this.subject(new EmailSentence(properties.getRequestedSubject()));
        return notification.enrichWithEmailContext(super.generate());
    }
    // resume CPD analysis - CPD-ON
}
