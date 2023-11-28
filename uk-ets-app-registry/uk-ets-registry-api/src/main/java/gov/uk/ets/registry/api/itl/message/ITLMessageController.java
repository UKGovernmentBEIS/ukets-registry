package gov.uk.ets.registry.api.itl.message;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorOrReadOnlyAdminRule;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;
import gov.uk.ets.registry.api.itl.message.service.ITLMessageManagementService;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSearchCriteria;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSearchResult;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSearchResultMapper;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSendRequest;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSendResponse;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSortFieldParam;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static gov.uk.ets.commons.logging.RequestParamType.ACCEPTED_MESSAGE_LOG_ID;

/**
 * Handles end-user requests related to instant messaging with ITL.
 */
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class ITLMessageController {

    /**
     * The ITL Messaging management service.
     */
    private final ITLMessageManagementService messageManagementService;


    /**
     * Searches for ITL messages according to the criteria parameter and returns the page of sorted
     * results as the pageParameters argument instructs
     *
     * @param criteria       The {@link ITLMessageSearchCriteria} search criteria
     * @param pageParameters The {@link PageParameters} paging info
     * @return The {@link SearchResponse<ITLMessageSearchResult>} response
     */
    @Protected({SeniorOrReadOnlyAdminRule.class})
    @GetMapping(path = "/itl.messages.list")
    public SearchResponse<ITLMessageSearchResult> search(@Valid ITLMessageSearchCriteria criteria,
                                                         PageParameters pageParameters) {
        SearchResponse<ITLMessageSearchResult> response = new SearchResponse<>();
        PageableMapper pageableMapper =
            new PageableMapper(ITLMessageSortFieldParam.values(), ITLMessageSortFieldParam.MESSAGE_DATE);
        Pageable pageable = pageableMapper.get(pageParameters);
        Page<AcceptMessageLog> page = messageManagementService.search(criteria, pageable);
        ITLMessageSearchResultMapper resultMapper = new ITLMessageSearchResultMapper();
        response.setItems(page.getContent().stream().map(resultMapper::map).collect(Collectors.toList()));
        pageParameters.setTotalResults(page.getTotalElements());
        response.setPageParameters(pageParameters);
        return response;
    }

    /**
     * Retrieves an ITL message based on its message Id.
     *
     * @param messageId The unique identifier.
     * @return a {@link TransactionSummary}.
     */
    @Protected({SeniorOrReadOnlyAdminRule.class})
    @GetMapping(path = "/itl.messages.get")
    public ResponseEntity<ITLMessageSummary> getITLMessage(@RequestParam @MDCParam(ACCEPTED_MESSAGE_LOG_ID)
                                                                       Long messageId) {
        ITLMessageSummary message = new ITLMessageSummary();

        Optional<AcceptMessageLog> messageOptional = messageManagementService.getMessage(messageId);

        if (messageOptional.isPresent()) {
            AcceptMessageLog acceptMessageLog = messageOptional.get();
            message = ITLMessageSummary.builder().
                messageId(acceptMessageLog.getId()).
                content(acceptMessageLog.getContent()).
                messageDate(acceptMessageLog.getMessageDatetime()).
                source(acceptMessageLog.getSource()).
                destination(acceptMessageLog.getDestination()).
                build();
        }

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * Sends a message to the ITL.
     *
     * @param messageContent The content of the message
     * @return The check result, containing the request identifier and transaction identifier in
     * case of successful proposal.
     */
    @Protected({SeniorAdminRule.class})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/itl.messages.send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ITLMessageSendResponse sendITLMessage(@RequestBody ITLMessageSendRequest request) {
        return messageManagementService.sendMessage(request.getContent());
    }
}
