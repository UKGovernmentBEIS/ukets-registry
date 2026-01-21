package gov.uk.ets.registry.api.integration.changelog.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountDTOFactory;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.integration.changelog.DomainObject;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class AccountAuditService {

    @Value("${account.audit.enabled:true}")
    private boolean auditEnabled;

    private final CommonAuditService commonAuditService;
    private final AccountRepository accountRepository;
    private final AccountDTOFactory accountDTOFactory;

    public AccountDTO getAccountDTOByComplianceEntityIdentifier(Long complianceEntityId) {
        return toAccountDto(getAccountByComplianceEntityIdentifier(complianceEntityId));
    }

    public Account getAccountByComplianceEntityIdentifier(Long complianceEntityId) {
        return accountRepository.findByCompliantEntityIdentifierWithContacts(complianceEntityId)
                .orElseThrow(() -> new RuntimeException("Account does not exist"));
    }

    public AccountDTO toAccountDto(Account account) {
        return accountDTOFactory.create(account, true);
    }


    public boolean logChanges(AccountDTO current, Account updated, SourceSystem updatedBy) {
        if(!auditEnabled) {
            return false;
        }

        AccountDTO newVersion = toAccountDto(updated);

        boolean hasAccountDetailsChanges = commonAuditService.handleChanges(DomainObject.ACCOUNT_DETAILS,
                current.getAccountDetails(),
                newVersion.getAccountDetails(),
                updated.getId(),
                updated.getFullIdentifier(),
                updated.getCompliantEntity().getIdentifier(), updatedBy);

        boolean hasOperatorChanges = commonAuditService.handleChanges(DomainObject.OPERATOR,
                current.getOperator(),
                newVersion.getOperator(),
                updated.getCompliantEntity().getId(),
                updated.getFullIdentifier(),
                updated.getCompliantEntity().getIdentifier(), updatedBy);

        boolean hasAccountHolderChanges = commonAuditService.handleChanges(DomainObject.ACCOUNT_HOLDER,
                current.getAccountHolder(),
                newVersion.getAccountHolder(),
                updated.getAccountHolder().getId(),
                updated.getFullIdentifier(),
                updated.getCompliantEntity().getIdentifier(), updatedBy);

        return hasAccountDetailsChanges || hasAccountHolderChanges || hasOperatorChanges;
    }

}
