package gov.uk.ets.registry.api.itl.notice;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.commons.logging.RequestParamType;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorOrReadOnlyAdminRule;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.itl.notice.service.ITLNoticeManagementService;
import gov.uk.ets.registry.api.itl.notice.web.model.*;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles end-user requests related to notices with ITL.
 */
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class ITLNoticeController {

    /**
     * The ITL Notification management service.
     */
    private final ITLNoticeManagementService itlNoticeManagementService;


    /**
     * Searches for ITL notices according to the criteria parameter and returns the page of sorted
     * results as the pageParameters argument instructs
     *
     * @param criteria       The {@link gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticeSearchCriteria} search criteria
     * @param pageParameters The {@link PageParameters} paging info
     * @return The {@link SearchResponse <ITLNoticeSearchResult>} response
     */
    @Protected({SeniorOrReadOnlyAdminRule.class})
    @GetMapping(path = "/itl.notices.list")
    public SearchResponse<ITLNoticeResult> search(ITLNoticeSearchCriteria criteria, PageParameters pageParameters) {
        return itlNoticeManagementService.search(criteria, pageParameters);
    }

    /**
     * Retrieves an ITL notice based on its notification identity.
     *
     * @param notificationIdentity The unique identifier.
     * @return a {@link ITLNoticeDetailResult}.
     */
    @Protected({SeniorOrReadOnlyAdminRule.class})
    @GetMapping(path = "/itl.notices.get")
    public ResponseEntity<List<ITLNoticeDetailResult>> getITLNotice(@RequestParam
                                                                        @MDCParam(RequestParamType.ITL_NOTIFICATION_ID)
                                                                                Long notificationIdentity) {
        Optional<List<ITLNoticeDetailResult>> itlNoticeDetailResultsOptional =
                Optional.ofNullable(itlNoticeManagementService.getITLDetails(notificationIdentity));

        return itlNoticeDetailResultsOptional
                .map(itlNoticeDetailResults -> new ResponseEntity<>(itlNoticeDetailResults, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK));

    }

    /**
     * Validates an ITL notice based on its notification identity.
     *
     * @param notificationIdentifier The ITL Notification ID
     * @param transactionType        The transaction type
     * @return The ITL Notification
     */
    @Protected({SeniorAdminRule.class})
    @GetMapping(path = "/itl.notices.validate")
    public ResponseEntity<ITLNoticeDetailResult> validate(
            @RequestParam(required = false) @MDCParam(RequestParamType.ITL_NOTIFICATION_ID) Long notificationIdentifier,
            @RequestParam TransactionType transactionType) {
        return new ResponseEntity<>(itlNoticeManagementService
                .performChecks(notificationIdentifier, transactionType), HttpStatus.OK);
    }
}
