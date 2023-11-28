package gov.uk.ets.registry.api.account.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.InstallationAndAccountTransferError;
import gov.uk.ets.registry.api.allocation.service.RequestAllocationService;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
//TODO convert to business Rules
@RequiredArgsConstructor
@Log4j2
public class TransferValidationService {

	private final AccountRepository accountRepository;

	private final TransactionRepository transactionRepository;

	private final TaskRepository taskRepository;

	private final AccountHolderRepository accountHolderRepository;

	private final TrustedAccountRepository trustedAccountRepository;

	private final RequestAllocationService requestAllocationService;

	/**
	 * For installation transfer.
	 */
	public void validateTransferByCompliantEntityIdentifier(Long compliantEntityIdentifier, Optional<Long> acquiringAccountHolderIdentifier) {

		Optional<Account> accountByCompliantEntityIdentifier = accountRepository
				.findByCompliantEntityIdentifier(compliantEntityIdentifier);
		// Transfer-BR2: must exist in the system. If not an error should be displayed:
		// The installation ID you entered could not be found.
		if (accountByCompliantEntityIdentifier.isEmpty()) {
			throw AccountActionException.create(
					AccountActionError.build(InstallationAndAccountTransferError.INVALID_INSTALLATION_ID.getMessage()));
		}

		Account accountToBeTransferred = accountByCompliantEntityIdentifier.get();
		// Transfer-BR1: you can only change the installation of Operator Holding
		// Accounts
		CompliantEntity compliantEntity = accountToBeTransferred.getCompliantEntity();
		if (!(compliantEntity instanceof Installation)) {
			throw AccountActionException.create(AccountActionError
					.build(InstallationAndAccountTransferError.INSTALLATION_ID_NOT_ASSOCIATED_TO_OHA.getMessage()));
		}
		
		if(acquiringAccountHolderIdentifier.isPresent()) {
			validateAcquiringAhIsDifferent(accountToBeTransferred.getAccountHolder().getIdentifier(), acquiringAccountHolderIdentifier.get());
		}
		
		validateTransfer(accountToBeTransferred);
	}

	/**
	 * For account transfer request.
	 */
	public void validateTransferRequestForAccountIdentifier(Long accountIdentifier, Long acquiringAhId,
			boolean isExistingAccountHolder) {
		Account accountByIdentifier = accountRepository.findByAccountIdentifierWithCompliantEntity(accountIdentifier)
				.orElseThrow(() -> AccountActionException.create(
						AccountActionError.build(InstallationAndAccountTransferError.TRANSFER_ONLY_OHAS.getMessage())));

		validateTransfer(accountByIdentifier);

		validateAccountTransferredIsOha(accountByIdentifier.getCompliantEntity());

		validateAcquiringAhIsDifferent(accountByIdentifier.getAccountHolder().getId(), acquiringAhId);

		if (isExistingAccountHolder) {
			validateAcquiringAhExists(acquiringAhId);
		}

	}

	/**
	 * For account transfer task.
	 */
	public void validateTransferTaskForAccountIdentifier(Long accountIdentifier, Long acquiringAhId) {
		Account accountByIdentifier = accountRepository.findByAccountIdentifierWithCompliantEntity(accountIdentifier)
				.orElseThrow(() -> AccountActionException.create(
						AccountActionError.build(InstallationAndAccountTransferError.TRANSFER_ONLY_OHAS.getMessage())));

		validatePendingTransactions(accountByIdentifier.getIdentifier());
		validateOpenTasksForAccountsUnderTheSameAccountHolder(accountByIdentifier.getId());
		validateAcquiringAhIsDifferent(accountByIdentifier.getAccountHolder().getId(), acquiringAhId);
	}

	/**
	 * @param accountToBeTransferred the account with the compliant Entity fetched.
	 */
	private void validateTransfer(Account accountToBeTransferred) {
		// Transfer-BR5: Request account opening with installation transfer must not be
		// allowed
		// for accounts which are in status CLOSURE_PENDING or CLOSED.

		AccountStatus accountToBeTransferredStatus = accountToBeTransferred.getAccountStatus();
		if (AccountStatus.CLOSED.equals(accountToBeTransferredStatus)) {
			throw AccountActionException
					.create(AccountActionError.build(InstallationAndAccountTransferError.CLOSED_ACCOUNT.getMessage()));
		}
		if (AccountStatus.CLOSURE_PENDING.equals(accountToBeTransferredStatus)) {
			throw AccountActionException.create(AccountActionError
					.build(InstallationAndAccountTransferError.ACCOUNT_WITH_PENDING_CLOSURE_REQUEST.getMessage()));
		}
		// Transfer-BR6: Request account opening with installation transfer must not be
		// allowed
		// accounts which are in status PENDING.
		if (AccountStatus.TRANSFER_PENDING.equals(accountToBeTransferredStatus)) {
			throw AccountActionException.create(AccountActionError
					.build(InstallationAndAccountTransferError.ACCOUNT_WITH_PENDING_TRANSFER.getMessage()));
		}

		// Transfer-BR3: Installation transfer cannot be requested from accounts
		// for which pending transactions (either pending to be approved or pending due
		// to delay) exist
		validatePendingTransactions(accountToBeTransferred.getIdentifier());

		// Transfer-BR7: if any task with reference to the account is pending or
		// delayed,
		// the installation transfer cannot be triggered.
		Long openTasksForAccountToBeTransferredCount = taskRepository
				.countPendingTasksByAccountIdExcludingAccountHolderTasks(accountToBeTransferred.getId());

		if (openTasksForAccountToBeTransferredCount > 0) {
			throw AccountActionException.create(AccountActionError
					.build(InstallationAndAccountTransferError.ACCOUNT_WITH_PENDING_OUTSTANDING_TASKS.getMessage()));
		}

		Long countTrustedAccountsPendingActivationByAccountId = trustedAccountRepository
				.countAllByAccountIdentifierAndStatusIn(accountToBeTransferred.getIdentifier(),
						List.of(TrustedAccountStatus.PENDING_ACTIVATION));

		if (countTrustedAccountsPendingActivationByAccountId > 0) {
			throw AccountActionException.create(AccountActionError
					.build(InstallationAndAccountTransferError.TRUSTED_ACC_ADDITION_TASK_IS_PENDING_ACTIVATION.getMessage()));
		}

		validateOpenTasksForAccountsUnderTheSameAccountHolder(accountToBeTransferred.getId());
	}

	/**
	 * Transfer-BR3: Installation transfer cannot be requested from accounts for
	 * which pending transactions (either pending to be approved or pending due to
	 * delay) exist.
	 */
	public void validatePendingTransactions(Long accountIdentifier) {
		Long activeTransactionsForAccountToBeTransferredCount = transactionRepository
				.countByRelatedAccountAndAndStatusIn(accountIdentifier, TransactionStatus.getPendingStatuses());

		if (activeTransactionsForAccountToBeTransferredCount > 0) {
			throw AccountActionException.create(AccountActionError
					.build(InstallationAndAccountTransferError.ACCOUNT_WITH_PENDING_TRANSACTIONS.getMessage()));
		}
	}

	/**
	 * Transfer-BR8 An account opening request with installation transfer cannot be
	 * submitted if there are outstanding updates (pending or delayed) for the AH of
	 * old (existing) account of the installation originating from the same account.
	 * More specifically, if any of the following tasks is outstanding (pending or
	 * delayed) for the AH of the old (existing account), the account opening with
	 * installation transfer cannot be triggered:
	 * <p>
	 * ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS ACCOUNT_HOLDER_UPDATE_DETAILS
	 * AH_REQUESTED_DOCUMENT_UPLOAD
	 */
	private void validateOpenTasksForAccountsUnderTheSameAccountHolder(Long accountId) {
		Long openTasksForAccountsUnderTheSameAccountHolderCount = taskRepository.countPendingTasksByAccountIdInAndType(
				List.of(accountId), List.of(RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
						RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS, RequestType.AH_REQUESTED_DOCUMENT_UPLOAD));

		if (openTasksForAccountsUnderTheSameAccountHolderCount > 0) {
			throw AccountActionException.create(AccountActionError
					.build(InstallationAndAccountTransferError.OUTSTANDING_TASKS_EXIST_FOR_THE_OLD_AH.getMessage()));
		}
	}

	/**
	 * BR10: Account transfer to the same AH cannot be requested.
	 */
	private void validateAcquiringAhIsDifferent(Long existingAccountHolder, Long acquiringAccountHolder) {
		if (existingAccountHolder.equals(acquiringAccountHolder)) {
			throw AccountActionException.create(AccountActionError.build(
					InstallationAndAccountTransferError.ACCOUNT_TRANSFER_TO_THE_SAME_AH_IS_NOT_PERMITTED.getMessage()));
		}
	}

	/**
	 * Transfer-BR11: Only OHA can be transferred to another AH
	 */
	private void validateAccountTransferredIsOha(CompliantEntity compliantEntity) {
		if (!(compliantEntity instanceof Installation)) {
			throw AccountActionException.create(
					AccountActionError.build(InstallationAndAccountTransferError.TRANSFER_ONLY_OHAS.getMessage()));
		}
	}

	/**
	 * BR12: AH ID must exist in the system else an error message should be
	 * displayed.
	 */
	private void validateAcquiringAhExists(Long acquiringAhId) {
		if (Optional.ofNullable(accountHolderRepository.getAccountHolder(acquiringAhId)).isEmpty()) {
			throw AccountActionException.create(AccountActionError
					.build(InstallationAndAccountTransferError.UNKNOWN_ACCOUNT_HOLDER_ID.getMessage()));
		}
	}

	public void validateNoPendingAllocationTasks(Long installationId) {
		// Check for a pending allocation request task
		if (requestAllocationService.getEntitiesInPendingAllocationRequestTaskOrJob().contains(installationId)) {
			throw AccountActionException.create(AccountActionError
					.build(InstallationAndAccountTransferError.ACCOUNT_WITH_PENDING_OUTSTANDING_TASKS.getMessage()));
		}
	}
}
