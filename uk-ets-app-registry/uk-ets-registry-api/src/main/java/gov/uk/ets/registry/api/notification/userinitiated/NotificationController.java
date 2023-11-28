package gov.uk.ets.registry.api.notification.userinitiated;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.commons.logging.RequestParamType;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.services.UserInitiatedNotificationService;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.DashboardNotificationDTO;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationDTO;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchPageableMapper;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchResult;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final UserInitiatedNotificationService service;
    private final NotificationSearchPageableMapper pageableMapper;

    @Protected({
        SeniorAdminRule.class
    })
    @PostMapping(value = "notifications.create")
    public ResponseEntity<Long> create(@RequestBody @Valid NotificationDTO notificationRequest) {
        return ResponseEntity.ok(service.createNotification(notificationRequest).getId());
    }

    @Protected({
        SeniorAdminRule.class
    })
    @PutMapping(value = "notifications.update")
    public ResponseEntity<Long> update(@RequestParam Long id, @RequestBody @Valid NotificationDTO notificationRequest) {
        return ResponseEntity.ok(service.updateNotification(id, notificationRequest).getId());
    }

    @GetMapping(value = "notifications.get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationDTO> getDefinition(@RequestParam @MDCParam(RequestParamType.NOTIFICATION_ID) Long id) {
        return ResponseEntity.ok(service.retrieveNotificationById(id));
    }

    @GetMapping(value = "notifications.get.definition", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationDefinitionDTO> getDefinition(@RequestParam NotificationType type) {
        return ResponseEntity.ok(service.retrieveDefinitionByType(type));
    }

    /**
     * Retrieve Notifications with pagination
     *
     * @param pageParameters The paging and sorting parameters
     * @return
     */
    @GetMapping(path = "/notifications.list", produces = MediaType.APPLICATION_JSON_VALUE)
    public SearchResponse<NotificationSearchResult> search(NotificationSearchCriteria criteria,
                                                           PageParameters pageParameters) {
        Pageable pageable = pageableMapper.get(pageParameters);
        Page<NotificationSearchResult> searchResults = service.search(criteria, pageable);
        SearchResponse<NotificationSearchResult> response = new SearchResponse<>();
        pageParameters.setTotalResults(searchResults.getTotalElements());
        response.setPageParameters(pageParameters);
        response.setItems(searchResults.getContent());
        return response;
    }

    @GetMapping(value = "notifications.list.for-dashboard")
    public ResponseEntity<List<DashboardNotificationDTO>> getDefinition() {
        return ResponseEntity.ok(service.retrieveDashboardNotifications());
    }

    @GetMapping(value = "notifications.get.definition.recipients.count")
    public ResponseEntity<Integer> getTentativeRecipientCount(@RequestParam NotificationType type) {
        return ResponseEntity.ok(service.calculateNumberOfRecipients(type));
    }
}
