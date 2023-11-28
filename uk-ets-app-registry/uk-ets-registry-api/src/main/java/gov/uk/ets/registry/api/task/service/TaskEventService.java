package gov.uk.ets.registry.api.task.service;

import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

@Service
public class TaskEventService {

    private final AuthorizationService authorizationService;
    private final EventService eventService;

    public TaskEventService(AuthorizationService authorizationService, EventService eventService) {
        this.authorizationService = authorizationService;
        this.eventService = eventService;
    }

    /**
     * Retrieves a task's history (comments and events)
     *
     * @param requestId The unique business identifier.
     * @return a task's history
     */
    public List<AuditEventDTO> getTaskHistory(Long requestId) {
        List<AuditEventDTO> taskSystemEvents =
                eventService.getSystemEventsByDomainIdOrderByCreationDateDesc(requestId.toString(), List.of(Task.class.getName()));
        if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_NON_ADMIN)) {
            return Stream.of(eventService.getEventsByDomainIdForNonAdminUsers(requestId.toString(), List.of(Task.class.getName())), taskSystemEvents)
                    .flatMap(Collection::stream)
                    .sorted(Comparator.comparing(AuditEventDTO::getCreationDate).reversed())
                    .collect(Collectors.toList());
        } else if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
            return Stream.of(eventService.getEventsByDomainIdForAdminUsers(requestId.toString(), List.of(Task.class.getName())),
                    taskSystemEvents)
                    .flatMap(Collection::stream)
                    .sorted(Comparator.comparing(AuditEventDTO::getCreationDate).reversed())
                    .collect(Collectors.toList());
        } else {
            throw new AuthorizationDeniedException("Invalid scope.", null);
        }
    }

    /**
     * Retrieves the user history (comments and events)
     *
     * @param urid The user identifier
     * @return a list of events
     */
    public List<AuditEventDTO> getTaskHistoryByUser(String urid) {
        return eventService.getAuditEventsByCreatorOrderByCreationDateDesc(urid);
    }

    /**
     * Creates and publishes events for task and account requests.
     *
     * @param task The submitted task
     * @param urid The current user
     * @param appendText The text to append at the end of the even "What" message.
     * @param comment The event comment.
     */
    public void createAndPublishTaskAndAccountRequestEvent(Task task, String urid, String appendText, String comment) {
        String action = task.generateEventTypeDescription(appendText);
        eventService.createAndPublishEvent(task.getRequestId().toString(), urid, comment,
                EventType.TASK_REQUESTED, action);
        if (task.getAccount() != null) {
            String description = String.format("Task requestId %s", task.getRequestId());
            String accountIdentifier = task.getAccount().getIdentifier().toString();
            eventService.createAndPublishEvent(accountIdentifier, urid, description,
                    EventType.ACCOUNT_TASK_REQUESTED, action);
        }
    }

    /**
     * Creates and publishes events for task and account requests.
     *
     * @param task The submitted task
     * @param urid The current user
     */
    public void createAndPublishTaskAndAccountRequestEvent(Task task, String urid) {
        createAndPublishTaskAndAccountRequestEvent(task, urid, " request submitted.", "");
    }
}
