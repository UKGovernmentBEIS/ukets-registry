package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountHolderChangeValidationServiceTest {

    @InjectMocks
    private AccountHolderChangeValidationService accountHolderChangeValidationService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TrustedAccountRepository trustedAccountRepository;

    @Mock
    private AccountHolderRepository accountHolderRepository;

    @Mock
    private AuthorizedRepresentativeService authorizedRepresentativeService;

    @Mock
    private AccountHolderService accountHolderService;

    @Test
    void shouldThrowException_whenAccountNotFound() {
        // Arrange
        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.empty());

        // Act + Assert
        AccountActionException exception = assertThrows(AccountActionException.class, () ->
                accountHolderChangeValidationService.validateAccountHolderChangeRequestForAccountIdentifier(
                        1L, 2L, true, false)
        );

        assertThat(exception.getMessage())
                .contains("You cannot change the Account Holder - Missing account");
        verify(accountRepository).findByIdentifier(1L);
    }

    @Test
    void shouldThrowException_whenAccountIsClosed() {
        // Arrange
        AccountHolder holder = new AccountHolder();
        holder.setId(10L);
        Account account = new Account();
        account.setAccountHolder(holder);
        account.setAccountStatus(AccountStatus.CLOSED);
        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.of(account));

        // Act + Assert
        AccountActionException exception = assertThrows(AccountActionException.class, () ->
                accountHolderChangeValidationService.validateAccountHolderChangeRequestForAccountIdentifier(
                        1L, 2L, false, false)
        );

        assertThat(exception.getMessage())
                .contains("The account is closed");
        verify(accountRepository).findByIdentifier(1L);
    }

    @Test
    void shouldThrowException_whenPendingTransactionsExist() {
        // Arrange
        AccountHolder holder = new AccountHolder();
        holder.setId(10L);
        Account account = new Account();
        account.setId(99L);
        account.setIdentifier(1L);
        account.setAccountHolder(holder);
        account.setAccountStatus(AccountStatus.OPEN);

        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.countByRelatedAccountAndAndStatusIn(eq(1L), anyList()))
                .thenReturn(2L); // simulate pending transactions

        // Act + Assert
        AccountActionException exception = assertThrows(AccountActionException.class, () ->
                accountHolderChangeValidationService.validateAccountHolderChangeRequestForAccountIdentifier(
                        1L, 2L, false, false)
        );

        assertThat(exception.getMessage())
                .contains("The account has outstanding transactions. You cannot proceed with the transfer.");
        verify(transactionRepository).countByRelatedAccountAndAndStatusIn(eq(1L), anyList());
    }

    @Test
    void shouldThrowException_whenAcquiringAccountHolderDoesNotExist() {
        // Arrange
        AccountHolder holder = new AccountHolder();
        holder.setId(10L);
        holder.setIdentifier(100L);

        Account account = new Account();
        account.setId(99L);
        account.setIdentifier(1L);
        account.setAccountHolder(holder);
        account.setAccountStatus(AccountStatus.OPEN);

        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.countByRelatedAccountAndAndStatusIn(anyLong(), anyList())).thenReturn(0L);
        when(taskRepository.countPendingTasksByAccountIdExcludingAccountHolderTasks(anyLong())).thenReturn(0L);
        when(trustedAccountRepository.countAllByAccountIdentifierAndStatusIn(anyLong(), anyList())).thenReturn(0L);
        when(authorizedRepresentativeService.hasSuspendedAR(anyLong())).thenReturn(false);
        when(taskRepository.countPendingTasksByAccountIdInAndType(anyList(), anyList())).thenReturn(0L);
        when(accountHolderRepository.getAccountHolder(2L)).thenReturn(null);
        when(accountHolderService.isOrphanedAccountHolder(anyLong(), anyLong())).thenReturn(false);

        // Act + Assert
        AccountActionException exception = assertThrows(AccountActionException.class, () ->
                accountHolderChangeValidationService.validateAccountHolderChangeRequestForAccountIdentifier(
                        1L, 2L, true, false)
        );

        assertThat(exception.getMessage())
                .contains("The Account Holder does not exist. Enter a valid Account Holder ID.");
        verify(accountHolderRepository).getAccountHolder(2L);
    }

    @Test
    void shouldThrowException_whenOrphanedHolderWithoutDeleteFlag() {
        AccountHolder holder = new AccountHolder();
        holder.setId(10L);
        holder.setIdentifier(100L);

        Account account = new Account();
        account.setId(99L);
        account.setIdentifier(1L);
        account.setAccountHolder(holder);
        account.setAccountStatus(AccountStatus.OPEN);

        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.countByRelatedAccountAndAndStatusIn(anyLong(), anyList())).thenReturn(0L);
        when(taskRepository.countPendingTasksByAccountIdExcludingAccountHolderTasks(anyLong())).thenReturn(0L);
        when(trustedAccountRepository.countAllByAccountIdentifierAndStatusIn(anyLong(), anyList())).thenReturn(0L);
        when(authorizedRepresentativeService.hasSuspendedAR(anyLong())).thenReturn(false);
        when(taskRepository.countPendingTasksByAccountIdInAndType(anyList(), anyList())).thenReturn(0L);
        when(accountHolderService.isOrphanedAccountHolder(100L, 1L)).thenReturn(true);

        AccountActionException exception = assertThrows(AccountActionException.class, () ->
                accountHolderChangeValidationService.validateAccountHolderChangeRequestForAccountIdentifier(
                        1L, 2L, false, null)
        );

        assertThat(exception.getMessage())
                .contains("Deletion flag must be specified");
    }

    @Test
    void shouldThrowException_whenNonOrphanHolderMarkedForDeletion() {
        AccountHolder holder = new AccountHolder();
        holder.setId(10L);
        holder.setIdentifier(100L);

        Account account = new Account();
        account.setId(99L);
        account.setIdentifier(1L);
        account.setAccountHolder(holder);
        account.setAccountStatus(AccountStatus.OPEN);

        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.countByRelatedAccountAndAndStatusIn(anyLong(), anyList())).thenReturn(0L);
        when(taskRepository.countPendingTasksByAccountIdExcludingAccountHolderTasks(anyLong())).thenReturn(0L);
        when(trustedAccountRepository.countAllByAccountIdentifierAndStatusIn(anyLong(), anyList())).thenReturn(0L);
        when(authorizedRepresentativeService.hasSuspendedAR(anyLong())).thenReturn(false);
        when(taskRepository.countPendingTasksByAccountIdInAndType(anyList(), anyList())).thenReturn(0L);
        when(accountHolderService.isOrphanedAccountHolder(100L, 1L)).thenReturn(false);

        AccountActionException exception = assertThrows(AccountActionException.class, () ->
                accountHolderChangeValidationService.validateAccountHolderChangeRequestForAccountIdentifier(
                        1L, 2L, false, true)
        );

        assertThat(exception.getMessage())
                .contains("The account holder has multiple accounts and cannot be deleted");
    }

    @Test
    void shouldPassValidation_whenAllChecksOk() {
        // Arrange
        AccountHolder holder = new AccountHolder();
        holder.setId(10L);
        holder.setIdentifier(100L);

        Account account = new Account();
        account.setId(99L);
        account.setIdentifier(1L);
        account.setAccountHolder(holder);
        account.setAccountStatus(AccountStatus.OPEN);

        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.countByRelatedAccountAndAndStatusIn(eq(1L), anyList())).thenReturn(0L);
        when(taskRepository.countPendingTasksByAccountIdExcludingAccountHolderTasks(eq(99L))).thenReturn(0L);
        when(trustedAccountRepository.countAllByAccountIdentifierAndStatusIn(eq(1L), anyList())).thenReturn(0L);
        when(authorizedRepresentativeService.hasSuspendedAR(eq(1L))).thenReturn(false);
        when(taskRepository.countPendingTasksByAccountIdInAndType(anyList(), anyList())).thenReturn(0L);
        when(accountHolderService.isOrphanedAccountHolder(anyLong(), anyLong())).thenReturn(false);
        when(accountHolderRepository.getAccountHolder(2L)).thenReturn(new AccountHolder());

        // Act
        accountHolderChangeValidationService.validateAccountHolderChangeRequestForAccountIdentifier(
                1L, 2L, true, false
        );

        // Assert
        verify(accountRepository).findByIdentifier(1L);
        verify(accountHolderRepository).getAccountHolder(2L);
        verifyNoMoreInteractions(transactionRepository);
    }

}
