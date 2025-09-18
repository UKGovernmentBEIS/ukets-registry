package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.userinactivity;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.SelectionCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.UserInitiatedNotificationService;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationParameterRetriever;
import gov.uk.ets.registry.api.notification.userinitiated.util.IdentifierGenerator;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserInactivityNotificationParameterRetriever implements NotificationParameterRetriever {
    private final NotificationSchedulingRepository schedulingRepository;
    private final UserWorkContactRepository userWorkContactRepository;
    private final UserInitiatedNotificationService userInitiatedNotificationService;

    /**
     * For each recipient of the notification, creates a {@link NotificationParameterHolder} by retrieving
     * appropriate parameter data and recipient email.
     */
    public List<NotificationParameterHolder> getNotificationParameters(Notification notification) {
        SelectionCriteria selectionCriteria = notification.getDefinition().getSelectionCriteria();

        List<String> userIdList = userInitiatedNotificationService.getInactiveUsers();

        List<BaseNotificationParameters> basicNotificationParameters =
            schedulingRepository.getUserInactivityNotificationParameters(
                userIdList,
                selectionCriteria.getAccountStatuses(),
                selectionCriteria.getUserStatuses(),
                selectionCriteria.getAccountAccessStates());

        Set<String> urIds = basicNotificationParameters.stream()
            .map(BaseNotificationParameters::getUrid)
            .collect(toSet());

        List<UserWorkContact> userWorkContacts = userWorkContactRepository.fetchUserWorkContactsInBatches(urIds);

        var nParams = basicNotificationParameters.stream()
            .map(baseParams ->
                NotificationParameterHolder.builder()
                    .recurrenceId(ObjectUtils.defaultIfNull(notification.getTimesFired(), 0L) + 1)
                    .notificationInstanceId(IdentifierGenerator.generate(notification))
                    .notificationId(notification.getId())
                    .email(getEmailForUser(userWorkContacts, baseParams.getUrid()))
                    .baseNotificationParameters(baseParams)
                    .build())
            .collect(toList());
        log.info("NotificationParameterRetriever::getNotificationParameters:Parameters: {}", nParams);
        return nParams;
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
}
