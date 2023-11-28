package gov.uk.ets.registry.api.account.service;

import static java.util.Optional.empty;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.InstallationAndAccountTransferError;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransferValidationServiceTest {

	@Mock
	AccountRepository accountRepository;

	@Mock
	TaskRepository taskRepository;

	@Mock
	TrustedAccountRepository trustedAccountRepository;

	@Mock
	TransactionRepository transactionRepository;

	@InjectMocks
	TransferValidationService transferValidationService;

	@Test
	void testValidateTransferWithInvalidInstallationID() {
		final String expectedMessage = InstallationAndAccountTransferError.INVALID_INSTALLATION_ID.getMessage();
		Long compliantEntityId = 1L;
		Mockito.when(accountRepository.findByCompliantEntityIdentifier(compliantEntityId)).thenReturn(empty());
		try {
			transferValidationService.validateTransferByCompliantEntityIdentifier(compliantEntityId, Optional.empty());
			fail(expectedMessage);
		} catch (AccountActionException ignore) {
			// ignore
		}
	}

	@Test
	void testValidateTransferWhenTrustedAccTaskPendingActivation() {
		final String expectedMessage = InstallationAndAccountTransferError.TRUSTED_ACC_ADDITION_TASK_IS_PENDING_ACTIVATION
				.getMessage();
		Long compliantEntityId = 1L;
		Account accountToBeTransferred = new Account();
		accountToBeTransferred.setAccountStatus(AccountStatus.OPEN);
		accountToBeTransferred.setIdentifier(22L);
		AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(1000036L);
		accountToBeTransferred.setAccountHolder(accountHolder);
		Installation installation = new Installation();
		installation.setIdentifier(compliantEntityId);
		installation.setStartYear(2019);
		installation.setEndYear(null);
		accountToBeTransferred.setCompliantEntity(installation);
		when(accountRepository.findByCompliantEntityIdentifier(compliantEntityId))
				.thenReturn(Optional.of(accountToBeTransferred));
		when(transactionRepository.countByRelatedAccountAndAndStatusIn(accountToBeTransferred.getIdentifier(),
				TransactionStatus.getPendingStatuses())).thenReturn(0L);
		when(taskRepository.countPendingTasksByAccountIdExcludingAccountHolderTasks(accountToBeTransferred.getId()))
				.thenReturn(0L);
		when(trustedAccountRepository.countAllByAccountIdentifierAndStatusIn(accountToBeTransferred.getIdentifier(),
				List.of(TrustedAccountStatus.PENDING_ACTIVATION))).thenReturn(1L);
		try {

			transferValidationService.validateTransferByCompliantEntityIdentifier(compliantEntityId, Optional.of(1000066L));
			fail(expectedMessage);
		} catch (AccountActionException ignore) {
			// ignore
		}
	}

	@Test
	void testValidateTransferToTheSameAccountHolder() {
		final String expectedMessage = InstallationAndAccountTransferError.ACCOUNT_TRANSFER_TO_THE_SAME_AH_IS_NOT_PERMITTED
				.getMessage();
		Long compliantEntityId = 1L;
		Account accountToBeTransferred = new Account();
		accountToBeTransferred.setAccountStatus(AccountStatus.OPEN);
		accountToBeTransferred.setIdentifier(22L);
		AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(1000036L);
		accountToBeTransferred.setAccountHolder(accountHolder);
		Installation installation = new Installation();
		installation.setIdentifier(compliantEntityId);
		installation.setStartYear(2019);
		installation.setEndYear(null);
		accountToBeTransferred.setCompliantEntity(installation);
		try {

			transferValidationService.validateTransferByCompliantEntityIdentifier(compliantEntityId, Optional.of(1000036L));
			fail(expectedMessage);
		} catch (AccountActionException ignore) {
			// ignore
		}
	}
}
