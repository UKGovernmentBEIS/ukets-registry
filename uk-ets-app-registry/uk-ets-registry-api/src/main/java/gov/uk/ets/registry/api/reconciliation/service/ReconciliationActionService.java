package gov.uk.ets.registry.api.reconciliation.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountStatusService;
import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Responsible for performing reconciliation actions.
 */
@Transactional
@AllArgsConstructor
@Service
public class ReconciliationActionService {
    private final ReconciliationService reconciliationService;
    private final AccountRepository accountRepository;
    private final AccountStatusService accountStatusService;

    /**
     * Creates a new reconciliation.
     * @param startDate The date that the reconciliation started
     * @return
     */
    public Long createReconciliation(Date startDate) {
        Reconciliation reconciliation = reconciliationService.createReconciliation(startDate);
        reconciliationService.keepHistory(reconciliation);
        return reconciliation.getIdentifier();
    }

    /**
     * Calculates the unit blocks totals per account and unit type and returns them.
     * @return The {@link ReconciliationEntrySummary} results
     */
    public List<ReconciliationEntrySummary> calculateTotals() {
        Iterable<Account> etsAccounts = accountRepository
            .findAll(QAccount.account.registryAccountType.in(RegistryAccountType.getETSRegistryAccountTypes()));
        Set<Long> identifiers = StreamSupport.stream(etsAccounts.spliterator(), false)
            .map(Account::getIdentifier)
            .collect(Collectors.toSet());
        return reconciliationService
            .getReconciliationEntrySummaries(identifiers);
    }


    /**
     * Deletes the reconciliation history and reconciliation of identifier.
     * @param identifier The identifier of reconciliation
     */
    public void deleteFailedToStartReconciliation(Long identifier) {
        reconciliationService.deleteReconciliationHistory(identifier);
        reconciliationService.deleteReconciliation(identifier);
    }

    /**
     * Updates the state of the reconciliation
     * @param reconciliationSummary The reconciliation transfer object
     */
    public void updateReconciliation(ReconciliationSummary reconciliationSummary) {
            Reconciliation reconciliation = reconciliationService.updateReconciliation(reconciliationSummary);
            reconciliationService.keepHistory(reconciliation);
    }

    /**
     * Processes the failed entries.
     * @param reconciliation The {@link ReconciliationSummary} transfer object.
     */
    public void processFailedEntries(ReconciliationSummary reconciliation) {
        if (CollectionUtils.isEmpty(reconciliation.getFailedEntries())) {
            throw new IllegalStateException("Inconsistent reconciliations should return their failed entries summaries");
        }
        reconciliation.getFailedEntries().stream().forEach(entry -> {
            String message = String.format("Account %s has been restricted from all transactions due to failed reconciliation %s.",
                entry.getAccountIdentifier(),
                reconciliation.getIdentifier());

            accountStatusService.changeAccountStatus(entry.getAccountIdentifier(), AccountStatus.ALL_TRANSACTIONS_RESTRICTED, message,
                                                     "Change account status (system)");
        });
    }

    /**
     * Closes the pending reconciliations
     */
    public void closePendingReconciliations() {
        reconciliationService.closePendingReconciliations();
    }
}
