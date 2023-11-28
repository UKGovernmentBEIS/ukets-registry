package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;

import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.AccountHolderParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.AircraftOperatorParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.InstallationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.services.StringEmailTemplateProcessor;
import gov.uk.ets.registry.api.notification.userinitiated.util.HtmlToPlainTextConverter;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Generates actual notification with recipients, subject, body (with parameters).
 */
@Service
@RequiredArgsConstructor
@Log4j2
class ComplianceNotificationGenerator {

    private final StringEmailTemplateProcessor templateProcessor;

    IdentifiableEmailNotification generate(NotificationParameterHolder recipient, NotificationDefinition definition) {
        log.info("generating notification for: {}", recipient);
        // subject is of the form: "(<notification_instance_id>) <subject>'"
        String subject = String.format("(%s) %s", recipient.getNotificationInstanceId(), definition.getShortText());

        BaseNotificationParameters baseParams = recipient.getBaseNotificationParameters();
        AccountHolderParameters accountHolder = recipient.getAccountHolderParameters();
        InstallationParameters installationParameters = recipient.getInstallationParameters();
        AircraftOperatorParameters aircraftOperatorParameters = recipient.getAircraftOperatorParameters();

        String balance = recipient.getBalance() != null ? recipient.getBalance().toString() : "";
        String currentDate = baseParams.getCurrentDate() != null ? baseParams.getCurrentDate().toString() : "";
        String year = baseParams.getCurrentYear() != null ? baseParams.getCurrentYear().toString() : "";
        String accountId = baseParams.getAccountFullIdentifier() != null ? Utils.maskFullIdentifier(baseParams.getAccountFullIdentifier()) : "";

        // we cannot set null value to the map, so instead we set an empty string
        Map<String, Object> params = Map.of(
            "user", baseParams,
            "accountHolder", accountHolder != null ? accountHolder : "",
            "installation", installationParameters != null ? installationParameters : "",
            "operator", aircraftOperatorParameters != null ? aircraftOperatorParameters : "",
            "balance", balance,
            "currentDate", currentDate,
            "currentYear", year,
            "accountId", accountId
        );

        String body = templateProcessor.processTemplate(definition.getLongText(), params);

        return IdentifiableEmailNotification.builder()
            // this is needed here to correlate with the channel when sending the notification
            .notificationId(recipient.getNotificationId())
            // this is needed for logging purposes
            .notificationInstanceId(recipient.getNotificationInstanceId())
            .recipients(Set.of(recipient.getEmail()))
            .subject(subject)
            .bodyHtml(body)
            .bodyPlain(HtmlToPlainTextConverter.convertHtmlToPlainText(body))
            .includeBcc(true)
            .build();
    }
}

