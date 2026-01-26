package gov.uk.ets.registry.api.integration.service.metscontacts;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountContactService;
import gov.uk.ets.registry.api.account.validation.AccountStatusValidationException;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.integration.changelog.service.ContactsAuditService;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.integration.service.RegistryIntegrationHeadersUtil;
import gov.uk.ets.registry.api.integration.service.account.AccountModificationResult;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class MetsContactsEventService {

    private static final String EVENT_ACTION = "Update of contacts via IP";
    private static final String EVENT_COMMENT_FORMAT = "%s updated contacts via IP";

    private final MetsContactsEventValidator metsContactsEventValidator;
    private final MetsContactMapper metsContactMapper;
    private final RegistryIntegrationHeadersUtil util;
    private final EventService eventService;
    private final ContactsAuditService contactsAuditService;
    private final AccountContactService accountContactService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AccountModificationResult process(MetsContactsEvent event, Map<String, Object> headers) {

        log.info("Received event {} with value {} and headers {}",
                OperationEvent.UPDATE_ACCOUNT_METS_CONTACT_DETAILS, event, headers);

        String correlationId = util.getCorrelationId(headers);
        OperatorType operatorType = util.getOperatorType(headers);

        List<IntegrationEventErrorDetails> errors = null;
        try {
            errors = metsContactsEventValidator.validate(event, operatorType);
        } catch (AccountStatusValidationException e) {
            log.info("Event {} with correlationId: {} from {} and value {} was not processed. " +
                            "Status of account is closed or pending for closed.",
                    OperationEvent.UPDATE_ACCOUNT_METS_CONTACT_DETAILS, correlationId, util.getSourceSystem(headers), event);
            return new AccountModificationResult(null);
        }
        if (errors.isEmpty()) {
            Account newAccount = process(event, util.getSourceSystem(headers));
            log.info("Event {} with correlationId: {} from {} and value {} was processed successfully.",
                    OperationEvent.UPDATE_ACCOUNT_METS_CONTACT_DETAILS, correlationId, util.getSourceSystem(headers), event);
            return new AccountModificationResult(newAccount.getFullIdentifier(), true);
        } else {
            log.warn(
                    "Event {} with correlationId: {} from {} and value {} has to following errors {} and was not processed successfully.",
                    OperationEvent.UPDATE_ACCOUNT_METS_CONTACT_DETAILS, correlationId, util.getSourceSystem(headers), event,
                    errors);
        }

        return new AccountModificationResult(errors);
    }

    private Account process(MetsContactsEvent event, SourceSystem sourceSystem) {
        List<MetsContactDTO> metsContacts = metsContactMapper.convert(event);

        Long operatorId = Long.valueOf(event.getOperatorId());
        List<MetsContactDTO> existing = contactsAuditService.getMetsContactDTO(operatorId);

        Account updated = accountContactService.saveOrUpdate(operatorId, metsContacts);
        contactsAuditService.logChanges(existing, updated.getMetsAccountContacts(), sourceSystem,
                updated.getFullIdentifier(), updated.getCompliantEntity().getIdentifier());
        Long identifier = updated.getIdentifier();

        eventService.createAndPublishEvent(String.valueOf(identifier), null,
                String.format(EVENT_COMMENT_FORMAT, sourceSystem), EventType.ACCOUNT_DETAILS_UPDATED, EVENT_ACTION);
        return updated;
    }
}
