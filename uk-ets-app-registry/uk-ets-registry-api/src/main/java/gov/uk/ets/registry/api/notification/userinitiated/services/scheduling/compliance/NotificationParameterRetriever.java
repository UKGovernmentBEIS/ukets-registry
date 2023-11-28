package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.compliance.web.model.ComplianceOverviewDTO;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.SelectionCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.AircraftOperatorParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.InstallationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.notification.userinitiated.util.IdentifierGenerator;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
class NotificationParameterRetriever {
    private final NotificationSchedulingRepository schedulingRepository;
    private final UserWorkContactRepository userWorkContactRepository;
    private final ComplianceService complianceService;


    /**
     * For each recipient of the notification, creates a {@link NotificationParameterHolder} by retrieving
     * appropriate parameter data and recipient email.
     */
    List<NotificationParameterHolder> getNotificationParameters(Notification notification) {
        SelectionCriteria selectionCriteria = notification.getDefinition().getSelectionCriteria();

        // TODO what to do if selection criteria are null?

        List<BaseNotificationParameters> baseNotificationParams =
            schedulingRepository.getBasicNotificationParameters(
                selectionCriteria.getAccountTypes(),
                selectionCriteria.getAccountStatuses(),
                selectionCriteria.getUserStatuses(),
                selectionCriteria.getAccountAccessStates(),
                selectionCriteria.getComplianceStatuses()
            );

        List<Long> accountIds = baseNotificationParams.stream()
            .map(BaseNotificationParameters::getAccountId)
            .collect(toList());

        Set<String> urids = baseNotificationParams.stream()
            .map(BaseNotificationParameters::getUrid)
            .collect(toSet());

        List<UserWorkContact> userWorkContacts = userWorkContactRepository.fetch(urids, true);

        List<InstallationParameters> installationParams =
            schedulingRepository.getInstallationParams(accountIds);

        List<AircraftOperatorParameters> aircraftOperatorParams =
            schedulingRepository.getAircraftOperatorParameters(accountIds);

        return baseNotificationParams.stream()
            .map(baseParams ->
                NotificationParameterHolder.builder()
                    .recurrenceId(ObjectUtils.defaultIfNull(notification.getTimesFired(), 0L) + 1)
                    .notificationInstanceId(IdentifierGenerator.generate(notification))
                    .notificationId(notification.getId())
                    .email(getEmailForUser(userWorkContacts, baseParams.getUrid()))
                    .baseNotificationParameters(baseParams)
                    .installationParameters(
                        getInstallationForAccount(installationParams, baseParams.getAccountId())
                    )
                    .aircraftOperatorParameters(
                        getAircraftOperatorForAccount(aircraftOperatorParams, baseParams.getAccountId())
                    )
                    .balance(calculateComplianceBalance(baseParams))
                    .build())
            .peek(this::logFilteredOutAccount)
            .filter(h -> h.getInstallationParameters() != null || h.getAircraftOperatorParameters() != null)
            .collect(toList());
    }

    private Long calculateComplianceBalance(BaseNotificationParameters baseParams) {
        ComplianceOverviewDTO complianceOverview =
            complianceService.getComplianceOverview(baseParams.getAccountIdentifier());
        return ObjectUtils.defaultIfNull(complianceOverview.getTotalNetSurrenders(), 0L) -
            ObjectUtils.defaultIfNull(complianceOverview.getTotalVerifiedEmissions(), 0L);
    }

    private String getEmailForUser(List<UserWorkContact> userWorkContacts, String urid) {
        return userWorkContacts.stream()
            .filter(contact -> contact.getUrid().equals(urid))
            .map(UserWorkContact::getEmail)
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException(String.format("Email for user with urid: %s not found", urid))
            );
    }

    private InstallationParameters getInstallationForAccount(List<InstallationParameters> installationParameters,
                                                             Long accountId) {
        return installationParameters.stream()
            .filter(ip -> ip.getAccountId().equals(accountId))
            .findFirst()
            .orElse(null);
    }

    private AircraftOperatorParameters getAircraftOperatorForAccount(
        List<AircraftOperatorParameters> aircraftOperatorParameters, Long accountId) {
        return aircraftOperatorParameters.stream()
            .filter(ao -> ao.getAccountId().equals(accountId))
            .findFirst()
            .orElse(null);
    }

    private void logFilteredOutAccount(NotificationParameterHolder holder) {
        if (holder.getInstallationParameters() == null && holder.getAircraftOperatorParameters() == null) {
            log.warn(
                "The account with id: {} is not related to an installation or an aircraft operator. " +
                    "The notification with instance id {} for this account will be skipped",
                holder.getBaseNotificationParameters().getAccountFullIdentifier(), holder.getNotificationInstanceId());
        }
    }
}
