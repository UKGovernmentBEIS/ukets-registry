package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.DateUtils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.integration.error.ContactPoint;
import gov.uk.ets.registry.api.integration.error.IntegrationEventError;
import gov.uk.ets.registry.api.integration.error.IntegrationEventErrorDetails;
import gov.uk.ets.registry.api.integration.message.AccountEmissionsUpdateEvent;
import gov.uk.ets.registry.api.integration.message.AccountOpeningEvent;
import gov.uk.ets.registry.api.integration.message.OperatorUpdateEvent;
import gov.uk.ets.registry.api.notification.integration.IntegrationErrorOutcomeNotification;
import gov.uk.ets.registry.api.notification.integration.IntegrationErrorOutcomeNotificationProperties;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IntegrationErrorOutcomeEmailGenerator extends EmailGenerator {

    private final IntegrationErrorOutcomeNotificationProperties properties;
    private final IntegrationErrorOutcomeNotification notification;
    private final Configuration freemarkerConfiguration;
    private final MailConfiguration mailConfiguration;

    private static final String TEMPLATE = "integration-error-outcome.ftl";

    @Override
    Map<String, Object> params() {
        final Map<String, Object> params = new HashMap<>();
        params.put("requestType", notification.getType().name());
        params.put("integrationPoint", notification.getIntegrationPoint());
        params.put("correlationId", notification.getCorrelationId());

        params.put("fields", getEventFields());

        params.put("actionErrors", getErrors(notification.getErrors(), error -> error.isActionFor(notification.getContactPoint())));
        params.put("infoErrors", getErrors(notification.getErrors(), error -> error.isInfoFor(notification.getContactPoint())));
        params.put("date", DateUtils.prettyCalendarDate(notification.getDate()));
        params.put("time", DateUtils.formatLondonZonedTime(notification.getDate()));
        params.put("sourceSystem", notification.getSourceSystem().name());

        Set<String> actionRecipients = getRecipients(IntegrationEventError::getActionRecipients);
        String infoRecipients = getRecipients(IntegrationEventError::getInformationRecipients).stream()
            .filter(contactPoint -> !actionRecipients.contains(contactPoint))
            .collect(Collectors.joining(", "));
        params.put("actionRecipients", String.join(", ", actionRecipients));
        params.put("hasRegistryActionRecipient", actionRecipients.contains(ContactPoint.REGISTRY_ADMINISTRATORS.getDescription()));
        params.put("hasServiceDeskOperator", actionRecipients.contains(ContactPoint.SERVICE_DESK.getDescription()));
        params.put("hasTuSupport", actionRecipients.contains(ContactPoint.TU_SUPPORT.getDescription()));
        params.put("infoRecipients", infoRecipients);
        return params;
    }

    private String asStringOrEmpty(Object obj) {
        return Optional.ofNullable(obj).map(Object::toString).filter(s -> !s.isBlank()).orElse("[empty]");
    }

    private List<IntegrationEventErrorDetails> getErrors(List<IntegrationEventErrorDetails> errors,
                                                         Predicate<IntegrationEventError> filter) {
        return errors.stream()
            .filter(errorDetails -> filter.test(errorDetails.getError()))
            .toList();
    }

    private Set<String> getRecipients(Function<IntegrationEventError, List<ContactPoint>> recipientFunction) {
        return notification.getErrors()
            .stream()
            .map(IntegrationEventErrorDetails::getError)
            .map(recipientFunction)
            .flatMap(Collection::stream)
            .map(ContactPoint::getDescription)
            .collect(Collectors.toSet());
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

        List<IntegrationEventError> integrationErrors = notification.getErrors().stream()
            .map(IntegrationEventErrorDetails::getError)
            .filter(error -> error.isRelatedFor(notification.getContactPoint()))
            .toList();

        String errors = integrationErrors.stream().map(Enum::name).collect(Collectors.joining(", "));
        String integrationPoint = notification.getIntegrationPoint().name();
        String keyValue = asStringOrEmpty(notification.getKeyValue());
        String messageKey = notification.getKeyField() + ": " + keyValue;

        if (integrationErrors.stream().anyMatch(error -> error.isActionFor(notification.getContactPoint()))) {
            this.subject(new EmailSentence(properties.getActionRequiredSubject(),
                integrationPoint, messageKey, errors));
        } else {
            this.subject(new EmailSentence(properties.getInformationOnlySubject(),
                integrationPoint, messageKey, errors));
        }

        return notification.enrichWithEmailContext(super.generate());
    }

    private Map<String, String> getEventFields() {
        if (notification.getEvent() instanceof AccountEmissionsUpdateEvent event) {
            return getEmissionFields(event);
        }

        if (notification.getEvent() instanceof AccountOpeningEvent event) {
            return getAccountOpeningFields(event);
        }

        if (notification.getEvent() instanceof OperatorUpdateEvent event) {
            return getOperatorFields(event);
        }

        return Map.of();
    }

    private Map<String, String> getAccountOpeningFields(AccountOpeningEvent event) {
        Map<String, String> fields = new LinkedHashMap<>();

        // account details
        fields.put("AccountDetails.EmitterID", asStringOrEmpty(event.getAccountDetails().getEmitterId()));
        fields.put("AccountDetails.InstallationActivityType", asStringOrEmpty(event.getAccountDetails().getInstallationActivityType()));
        fields.put("AccountDetails.InstallationName", asStringOrEmpty(event.getAccountDetails().getInstallationName()));
        fields.put("AccountDetails.Account Name", asStringOrEmpty(event.getAccountDetails().getAccountName()));
        fields.put("AccountDetails.PermitID", asStringOrEmpty(event.getAccountDetails().getPermitId()));
        fields.put("AccountDetails.MonitoringPlanID", asStringOrEmpty(event.getAccountDetails().getMonitoringPlanId()));
        fields.put("AccountDetails.CompanyImoNumber", asStringOrEmpty(event.getAccountDetails().getCompanyImoNumber()));
        fields.put("AccountDetails.Regulator", asStringOrEmpty(event.getAccountDetails().getRegulator()));
        fields.put("AccountDetails.DateOfEmpIssuance", asStringOrEmpty(event.getAccountDetails().getEmpPermitIssuanceDate()));
        fields.put("AccountDetails.FirstYearOfVerifiedEmission", asStringOrEmpty(event.getAccountDetails().getFirstYearOfVerifiedEmissions()));
        //account holder
        fields.put("AccountHolder.Name", asStringOrEmpty(event.getAccountHolder().getName()));
        fields.put("AccountHolder.AccountHolderType", asStringOrEmpty(event.getAccountHolder().getAccountHolderType()));
        fields.put("AccountHolder.AddressLine1", asStringOrEmpty(event.getAccountHolder().getAddressLine1()));
        fields.put("AccountHolder.AddressLine2", asStringOrEmpty(event.getAccountHolder().getAddressLine2()));
        fields.put("AccountHolder.TownOrCity", asStringOrEmpty(event.getAccountHolder().getTownOrCity()));
        fields.put("AccountHolder.StateOrProvince", asStringOrEmpty(event.getAccountHolder().getStateOrProvince()));
        fields.put("AccountHolder.Country", asStringOrEmpty(event.getAccountHolder().getCountry()));
        fields.put("AccountHolder.PostalCode", asStringOrEmpty(event.getAccountHolder().getPostalCode()));
        fields.put("AccountHolder.CrnNotExists", asStringOrEmpty(event.getAccountHolder().getCrnNotExist()));
        fields.put("AccountHolder.CompanyRegistrationNumber", asStringOrEmpty(event.getAccountHolder().getCompanyRegistrationNumber()));
        fields.put("AccountHolder.CrnJustification", asStringOrEmpty(event.getAccountHolder().getCrnJustification()));

        return fields;
    }

    private Map<String, String> getEmissionFields(AccountEmissionsUpdateEvent event) {
        Map<String, String> fields = new LinkedHashMap<>();

        fields.put("Operator ID", asStringOrEmpty(event.getRegistryId()));
        fields.put("Reporting Year", asStringOrEmpty(event.getReportingYear()));
        fields.put("Reportable Emissions", asStringOrEmpty(event.getReportableEmissions()));

        return fields;
    }

    private Map<String, String> getOperatorFields(OperatorUpdateEvent event) {
        Map<String, String> fields = new LinkedHashMap<>();

        fields.put("Operator ID", asStringOrEmpty(event.getOperatorId()));
        fields.put("Emitter Id", asStringOrEmpty(event.getEmitterId()));
        fields.put("Regulator", asStringOrEmpty(event.getRegulator()));

        return fields;
    }

}
