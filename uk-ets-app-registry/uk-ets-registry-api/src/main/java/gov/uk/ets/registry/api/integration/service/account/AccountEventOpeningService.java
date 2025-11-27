package gov.uk.ets.registry.api.integration.service.account;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.messaging.CompliantEntityInitializationEvent;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.validation.AccountValidationException;
import gov.uk.ets.registry.api.account.validation.AccountValidator;
import gov.uk.ets.registry.api.account.validation.Violation;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.error.IntegrationEventErrorDetails;
import gov.uk.ets.registry.api.integration.message.AccountOpeningEvent;
import gov.uk.ets.registry.api.integration.service.IntegrationHeadersUtil;
import gov.uk.ets.registry.api.integration.service.operator.OperatorEventService;
import gov.uk.ets.registry.api.messaging.UktlAccountNotifyMessageService;
import gov.uk.ets.registry.api.messaging.domain.AccountNotification;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.sis.internal.util.StandardDateFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountEventOpeningService {

    private final AccountEventValidator eventValidator;
    private final AccountValidator accountValidator;
    private final AccountService accountService;
    private final AccountAccessService accountAccessService;
    private final ComplianceEventService complianceEventService;
    private final AccountEventMapper accountEventMapper;
    private final OperatorEventService operatorEventService;
    private final UktlAccountNotifyMessageService uktlAccountNotifyMessageService;

    private final IntegrationHeadersUtil util;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AccountOpeningResult process(AccountOpeningEvent event, Map<String, Object> headers) {

        log.info("Received event {} with value {} and headers {}",
            OperationEvent.OPEN_ACCOUNT, event, headers);

        String correlationId = util.getCorrelationId(headers);

        OperatorType operatorType = util.getOperatorType(headers);
        List<IntegrationEventErrorDetails> errors = eventValidator.validate(event, operatorType);
        if (errors.isEmpty()) {
            Account newAccount = process(event, operatorType, correlationId);
            log.info("Event {} with correlationId: {} from {} and value {} was processed successfully.",
                OperationEvent.OPEN_ACCOUNT, correlationId, util.getSourceSystem(headers), event);
            return new AccountOpeningResult(newAccount.getFullIdentifier());
        } else {
            log.warn(
                "Event {} with correlationId: {} from {} and value {} has to following errors {} and was not processed successfully.",
                OperationEvent.OPEN_ACCOUNT, correlationId, util.getSourceSystem(headers), event,
                errors);
        }

        return new AccountOpeningResult(errors);
    }

    private Account process(AccountOpeningEvent event, OperatorType operatorType, String correlationId) {
        AccountDTO accountDTO = accountEventMapper.convert(event, operatorType);
        try {
            accountValidator.validate(accountDTO);
        } catch (AccountValidationException exception) {
            // Accounts from integration are allowed to have zero ARs
            boolean notOnlyMinARsViolation = exception.getErrors().stream().map(Violation::getFieldName)
                .anyMatch(s -> !"account.authorisedRepresentatives.min.number.violated".equals(s));
            if (notOnlyMinARsViolation) {
                throw exception;
            }
        }

        Account newAccount = accountService.createAccount(accountDTO);
        accountAccessService.createAccountAccess(newAccount, UserRole.getRolesForRoleBasedAccess(),
            AccountAccessRight.ROLE_BASED);
        sendAccountTransactionLogNotifications(newAccount);
        sendAccountComplianceNotifications(newAccount);

        operatorEventService.updateOperator(newAccount.getCompliantEntity(), newAccount.getRegistryAccountType().name(), correlationId);
        return newAccount;
    }
    
    /**
     * Send notifications to the transaction log services.
     * 
     * @param account
     */
    private void sendAccountTransactionLogNotifications(Account account) {
        if (!RegistryAccountType.NONE.equals(account.getRegistryAccountType())) {
            uktlAccountNotifyMessageService
                .sendAccountOpeningNotification(AccountNotification.convert(account));
        }    	
    }
    
    /**
     * Send notifications to the compliance calculation services.
     * 
     * @param account
     */
    private void sendAccountComplianceNotifications(Account account) {
        CompliantEntity compliantEntity = account.getCompliantEntity();
        LocalDateTime now = LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC));
        CompliantEntityInitializationEvent accountCreationEvent = CompliantEntityInitializationEvent.builder()
            .firstYearOfVerifiedEmissions(compliantEntity.getStartYear())
            .lastYearOfVerifiedEmissions(compliantEntity.getEndYear())
            .actorId("system")
            .eventId(UUID.randomUUID())
            .compliantEntityId(compliantEntity.getIdentifier())
            .dateTriggered(now)
            .dateRequested(now)
            .currentYear(now.getYear())
            .build();
        complianceEventService.processEvent(accountCreationEvent);
    }
}