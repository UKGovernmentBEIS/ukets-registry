package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.InstallationAndAccountTransferError;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountHolderChangeValidationService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final TaskRepository taskRepository;

    private final TrustedAccountRepository trustedAccountRepository;

    private final AccountHolderRepository accountHolderRepository;

    private final AuthorizedRepresentativeService authorizedRepresentativeService;

    private final AccountHolderService accountHolderService;

    /**
     * For account holder change request.
     */
    public void validateAccountHolderChangeRequestForAccountIdentifier(Long accountIdentifier, Long newAccountHolderIdentifier,
                                                                       boolean isExistingAccountHolder, Boolean shouldDeleteAccountHolder) {
        Account account = accountRepository.findByIdentifier(accountIdentifier)
                .orElseThrow(() -> AccountActionException.create(
                        AccountActionError.build("You cannot change the Account Holder - Missing account.")));

        validateChangeAccountHolder(account);
        validateAcquiringAhIsDifferent(account.getAccountHolder().getIdentifier(), newAccountHolderIdentifier);
        validateOrphanAccountHolderDeletion(account.getAccountHolder().getIdentifier(), accountIdentifier, shouldDeleteAccountHolder);
        if (isExistingAccountHolder) {
            validateAcquiringAhExists(newAccountHolderIdentifier);
        }
    }

    public void validateChangeAccountHolderTaskForAccountIdentifier(Long accountIdentifier, Long newAccountHolderId,
                                                                    Boolean shouldDeleteAccountHolder) {
        Account account = accountRepository.findByIdentifier(accountIdentifier)
                .orElseThrow(() -> AccountActionException.create(
                        AccountActionError.build("You cannot change the Account Holder - Missing account.")));

        validatePendingTransactions(account.getIdentifier());
        validateOpenTasksForAccountsUnderTheSameAccountHolder(account.getId());
        validateAcquiringAhIsDifferent(account.getAccountHolder().getId(), newAccountHolderId);
        validateOrphanAccountHolderDeletion(account.getAccountHolder().getIdentifier(), accountIdentifier, shouldDeleteAccountHolder);
    }

    private void validateChangeAccountHolder(Account accountToBeChanged) {

        AccountStatus accountToBeChangedStatus = accountToBeChanged.getAccountStatus();
        if (AccountStatus.CLOSED.equals(accountToBeChangedStatus)) {
            throw AccountActionException
                    .create(AccountActionError.build("The account is closed. The account holder change cannot be requested."));
        }
        if (AccountStatus.CLOSURE_PENDING.equals(accountToBeChangedStatus)) {
            throw AccountActionException.create(AccountActionError
                    .build("The account has closure pending requests. The account holder change cannot be requested."));
        }

        if (AccountStatus.TRANSFER_PENDING.equals(accountToBeChangedStatus)) {
            throw AccountActionException.create(AccountActionError
                    .build("The account has outstanding transfers. The account holder change cannot be requested."));
        }

        validatePendingTransactions(accountToBeChanged.getIdentifier());

        Long openTaskCount = taskRepository
                .countPendingTasksByAccountIdExcludingAccountHolderTasks(accountToBeChanged.getId());

        if (openTaskCount > 0) {
            throw AccountActionException.create(AccountActionError
                    .build(InstallationAndAccountTransferError.ACCOUNT_WITH_PENDING_OUTSTANDING_TASKS.getMessage()));
        }
        validatePendingForApprovalTrustedAccount(accountToBeChanged.getIdentifier());
        validateHasAccountSuspendedAR(accountToBeChanged.getIdentifier());
        validateOpenTasksForAccounts(accountToBeChanged.getId());
        validateOpenTasksForAccountsUnderTheSameAccountHolder(accountToBeChanged.getId());
    }

    private void validatePendingTransactions(Long accountIdentifier) {
        Long pendingTransactionCount = transactionRepository
                .countByRelatedAccountAndAndStatusIn(accountIdentifier, TransactionStatus.getPendingStatuses());

        if (pendingTransactionCount > 0) {
            throw AccountActionException.create(AccountActionError
                    .build(InstallationAndAccountTransferError.ACCOUNT_WITH_PENDING_TRANSACTIONS.getMessage()));
        }
    }

    private void validatePendingForApprovalTrustedAccount(Long identifier) {
        Long trustedAccounts = trustedAccountRepository
                .countAllByAccountIdentifierAndStatusIn(identifier,
                        List.of(TrustedAccountStatus.PENDING_ACTIVATION));
        if (trustedAccounts > 0) {
            throw AccountActionException.create(AccountActionError
                    .build("You try to change account holder when trusted account change exists"));
        }
    }

    private void validateOpenTasksForAccountsUnderTheSameAccountHolder(Long accountId) {
        Long pendingAccountHolderTaskCount = taskRepository.countPendingTasksByAccountIdInAndType(
                List.of(accountId),
                AccountHolderChangeRules.changeAccountHolderInvalidPendingTasks()
        );

        if (pendingAccountHolderTaskCount > 0) {
            throw AccountActionException.create(AccountActionError
                    .build(InstallationAndAccountTransferError.OUTSTANDING_TASKS_EXIST_FOR_THE_OLD_AH.getMessage()));
        }
    }

    private void validateOpenTasksForAccounts(Long accountId) {
        List<RequestType> invalidRequests = new ArrayList<>(RequestType.getARUpdateTasks());
        invalidRequests.addAll(
                AccountHolderChangeRules.changeAccountHolderInvalidRequestTypes()
        );
        Long pendingTasksCount = taskRepository.countPendingTasksByAccountIdInAndType(
                List.of(accountId), invalidRequests);

        if (pendingTasksCount > 0) {
            throw AccountActionException.create(AccountActionError
                    .build("You cannot change the AccountHolder if there are pending update Requests"));
        }
    }

    private void validateHasAccountSuspendedAR(Long identifier) {
        if (authorizedRepresentativeService.hasSuspendedAR(identifier)) {
            throw AccountActionException.create(AccountActionError
                    .build("You try to change account holder when suspended account representative request exists"));
        }
    }

    private void validateAcquiringAhIsDifferent(Long existingAccountHolderIdentifier, Long acquiringAccountHolderIdentifier) {
        if (existingAccountHolderIdentifier.equals(acquiringAccountHolderIdentifier)) {
            throw AccountActionException.create(AccountActionError.build("You cannot change the account holder to the same one"));
        }
    }

    private void validateAcquiringAhExists(Long newAccountHolderIdentifier) {
        if (Optional.ofNullable(accountHolderRepository.getAccountHolder(newAccountHolderIdentifier)).isEmpty()) {
            throw AccountActionException.create(AccountActionError
                    .build(InstallationAndAccountTransferError.UNKNOWN_ACCOUNT_HOLDER_ID.getMessage()));
        }
    }

    private void validateOrphanAccountHolderDeletion(
            Long accountHolderIdentifier, Long accountIdentifier, Boolean accountHolderDelete) {

        boolean isOrphaned = accountHolderService.isOrphanedAccountHolder(accountHolderIdentifier, accountIdentifier);

        if (isOrphaned) {
            if (accountHolderDelete == null) {
                throw AccountActionException.create(AccountActionError.build(
                        "Deletion flag must be specified for orphaned account holders"));
            }
        } else {
            if (Boolean.TRUE.equals(accountHolderDelete)) {
                throw AccountActionException.create(AccountActionError.build(
                        "The account holder has multiple accounts and cannot be deleted"));
            }
        }
    }

}
