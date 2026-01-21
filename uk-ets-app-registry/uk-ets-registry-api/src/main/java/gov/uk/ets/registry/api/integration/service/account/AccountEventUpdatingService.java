package gov.uk.ets.registry.api.integration.service.account;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.UpdateAccountService;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.integration.changelog.service.AccountAuditService;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.integration.service.RegistryIntegrationHeadersUtil;
import gov.uk.ets.registry.api.integration.service.account.validators.UpdateAccountEventValidator;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountEventUpdatingService {

    private static final String UPDATE_EVENT_ACTION = "Update of account via IP";
    private static final String UPDATE_EVENT_COMMENT_WITHOUT_CHANGES = "From %s. No update detected. No records stored in the Change Log";
    private static final String UPDATE_EVENT_COMMENT_FORMAT = "%s updated the account via IP";

    private final EventService eventService;
    private final UpdateAccountEventValidator eventValidator;
    private final UpdateAccountService updateAccountService;
    private final AccountEventMapper accountEventMapper;

    private final RegistryIntegrationHeadersUtil util;
    private final AccountAuditService accountAuditService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AccountModificationResult process(AccountUpdatingEvent event, Map<String, Object> headers) {

        log.info("Received event {} with value {} and headers {}",
                OperationEvent.UPDATE_ACCOUNT_DETAILS, event, headers);

        String correlationId = util.getCorrelationId(headers);

        OperatorType operatorType = util.getOperatorType(headers);
        List<IntegrationEventErrorDetails> errors = eventValidator.validate(event, operatorType);
        if (errors.isEmpty()) {
            return process(event, operatorType, util.getSourceSystem(headers), correlationId);
        } else {
            log.warn(
                    "Event {} with correlationId: {} from {} and value {} has to following errors {} and was not processed successfully.",
                    OperationEvent.UPDATE_ACCOUNT_DETAILS, correlationId, util.getSourceSystem(headers), event,
                    errors);
        }

        String accountIdentifier = event.getAccountDetails() != null && event.getAccountDetails().getRegistryId() != null
                ? event.getAccountDetails().getRegistryId() : "INVALID_IDENTIFIER";

        return new AccountModificationResult(accountIdentifier, errors);
    }

    private AccountModificationResult process(AccountUpdatingEvent event, OperatorType operatorType, SourceSystem sourceSystem, String correlationId) {
        AccountDTO accountDTO = accountEventMapper.convert(event, operatorType);
        AccountDTO existing = accountAuditService.getAccountDTOByComplianceEntityIdentifier(Long.parseLong(event.getAccountDetails().getRegistryId()));
        Account updated = updateAccountService.updateAccount(accountDTO);
        boolean hasChanges = accountAuditService.logChanges(existing, updated, sourceSystem);
        Long identifier = updated.getIdentifier();
        addCommentAndHistory(identifier, sourceSystem, hasChanges);
        log.info("Event {} with correlationId: {} from {} and value {} was processed successfully.",
                OperationEvent.UPDATE_ACCOUNT_DETAILS, correlationId, sourceSystem, event);

        return new AccountModificationResult(updated.getFullIdentifier(), hasChanges);
    }

    private void addCommentAndHistory(Long identifier, SourceSystem sourceSystem, Boolean hasChanges) {
        String comment = hasChanges
                ? String.format(UPDATE_EVENT_COMMENT_FORMAT, sourceSystem)
                : String.format(UPDATE_EVENT_COMMENT_WITHOUT_CHANGES, sourceSystem);

        eventService.createAndPublishEvent(String.valueOf(identifier), null,
                comment, EventType.ACCOUNT_DETAILS_UPDATED, UPDATE_EVENT_ACTION);

    }
}
