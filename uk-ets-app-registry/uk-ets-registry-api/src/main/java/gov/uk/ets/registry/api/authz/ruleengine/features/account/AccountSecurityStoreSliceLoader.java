package gov.uk.ets.registry.api.authz.ruleengine.features.account;

import static gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleAppliance.YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferRequestDTO;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountSecurityStoreSliceLoader {

    private BusinessSecurityStore businessSecurityStore;
    private RuleInputStore ruleInputStore;
    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TrustedAccountRepository trustedAccountRepository;
    private final ComplianceService complianceService;

    private static final List<RequestType> COMPLIANT_UPDATE_REQUESTS = List.of(
            RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST,
            RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST,
            RequestType.MARITIME_OPERATOR_UPDATE_REQUEST);

    /**
     * Loads the {@link AccountSecurityStoreSlice account action dto store slice}.
     */
    public void load() {
        if (businessSecurityStore.getAccountSecurityStoreSlice() != null ||
            !ruleInputStore.containsKey(RuleInputType.ACCOUNT_FULL_ID) &&
            !ruleInputStore.containsKey(RuleInputType.ACCOUNT_ID) &&
            !ruleInputStore.containsKey(RuleInputType.ACCOUNT_TRANSFER_REQUEST)) {
            return;
        }

        AccountSecurityStoreSlice accountSecurityStoreSlice =
            new AccountSecurityStoreSlice();

        Account account = Optional.ofNullable((String) ruleInputStore.get(RuleInputType.ACCOUNT_FULL_ID))
            .flatMap(accountRepository::findByFullIdentifier)
            .orElseGet(() ->
                accountRepository.findByIdentifier((Long) ruleInputStore.get(RuleInputType.ACCOUNT_ID))
                    .orElseGet(() ->
                            accountRepository.findByIdentifier(((AccountTransferRequestDTO) ruleInputStore.get(RuleInputType.ACCOUNT_TRANSFER_REQUEST)).getAccountIdentifier())
                    .orElseThrow(
                        () -> new NotFoundException(YOU_ARE_REQUESTING_TO_READ_OR_WRITE_A_NON_EXISTENT_ACCOUNT))
            ));

        if (businessSecurityStore.getTask() != null) {
            accountSecurityStoreSlice.setPendingTasks(taskRepository
                .countPendingTasksByAccountIdAndRequestIdentifierNotEqual(account.getId(),
                    businessSecurityStore.getTask().getRequestId()));
        } else {
            accountSecurityStoreSlice.setPendingTasks(taskRepository
                .countPendingTasksByAccountIdExcludingAHDocumentTask(account.getId()));
        }

        accountSecurityStoreSlice
            .setPendingComplianceEntityUpdate(taskRepository
                .countPendingTasksByAccountIdInAndType(List.of(account.getId()), COMPLIANT_UPDATE_REQUESTS));

        if (account.getCompliantEntity() != null) {
            Long compliantEntityId = account.getCompliantEntity().getIdentifier();
            accountSecurityStoreSlice.setVerifiedEmissionsList(complianceService.getReportableVerifiedEmissions(
                compliantEntityId, LocalDate.now().getYear()).getVerifiedEmissions());
        }

        accountSecurityStoreSlice
            .setActiveTransactions(transactionRepository
                .countByRelatedAccountAndAndStatusIn(
                    account.getIdentifier(), TransactionStatus.getPendingAndDelayedStatuses()));

        accountSecurityStoreSlice.setPendingActivationTrustedAccounts(trustedAccountRepository
                .countAllByAccountIdentifierAndStatusIn(account.getIdentifier(), List.of(TrustedAccountStatus.PENDING_ACTIVATION)));

        var trustedAccounts = trustedAccountRepository.
                findTrustedAccountsForAccountIdAndStatuses(account.getFullIdentifier(),
                                                List.of(TrustedAccountStatus.PENDING_ADDITION_APPROVAL,TrustedAccountStatus.PENDING_REMOVAL_APPROVAL));
        accountSecurityStoreSlice.setLinkedPendingTrustedAccounts(trustedAccounts);
        businessSecurityStore.setAccountSecurityStoreSlice(accountSecurityStoreSlice);
    }

    @Resource(name = "requestScopedBusinessSecurityStore")
    protected void setBusinessSecurityStore(BusinessSecurityStore businessSecurityStore) {
        this.businessSecurityStore = businessSecurityStore;
    }

    @Resource(name = "requestScopedRuleInputStore")
    protected void setRuleInputStore(RuleInputStore ruleInputStore) {
        this.ruleInputStore = ruleInputStore;
    }

}
