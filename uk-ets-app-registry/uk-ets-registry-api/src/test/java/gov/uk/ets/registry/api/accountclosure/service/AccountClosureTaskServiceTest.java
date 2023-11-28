package gov.uk.ets.registry.api.accountclosure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.accountclosure.web.model.AccountClosureTaskDetailsDTO;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountClosureTaskServiceTest {
    private static final String TEST_ACCOUNT_NUMBER = "UK-100-100001-1-11";
    private static final Long TEST_ACCOUNT_ID = 100001L;
    private static final Long TRUSTED_ACCOUNT_ID_1 = 2L;
    private static final Long TRUSTED_ACCOUNT_ID_2 = 3L;

    @Mock
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TrustedAccountRepository trustedAccountRepository;
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;
    @Mock
    private User currentUser;

    @InjectMocks
    private AccountClosureTaskService accountClosureTaskService;

    @Spy
    private Account account;
    @Spy
    private AccountClosureBusinessRulesApplier accountClosureBusinessRulesApplier;

    private AccountClosureTaskDetailsDTO task;
    private AccountDetailsDTO accountDetailsDTO;

    @BeforeEach
    void setUp() {
        accountDetailsDTO = new AccountDetailsDTO();
        accountDetailsDTO.setAccountStatus(AccountStatus.OPEN);
        accountDetailsDTO.setAccountNumber(TEST_ACCOUNT_NUMBER);

        TaskDetailsDTO taskDetails = new TaskDetailsDTO();
        task = new AccountClosureTaskDetailsDTO(taskDetails);
        task.setAccountDetails(accountDetailsDTO);

        account.setIdentifier(TEST_ACCOUNT_ID);
        account.setFullIdentifier(TEST_ACCOUNT_NUMBER);

        lenient().when(userService.getCurrentUser()).thenReturn(currentUser);
        when(accountRepository.findByFullIdentifier(TEST_ACCOUNT_NUMBER)).thenReturn(Optional.of(account));
    }

    @Test
    void shouldCloseAccount() {
        TaskCompleteResponse complete = accountClosureTaskService.complete(task, TaskOutcome.APPROVED, "");

        assertThat(complete).isNotNull();

        verify(accountClosureBusinessRulesApplier, times(1)).applyAccountClosureBusinessRules(anyString());
        verify(account, times(1)).setAccountStatus(AccountStatus.CLOSED);
    }

    @Test
    void shouldRevertAccountStatusWhenRejectingTask() {
        accountClosureTaskService.complete(task, TaskOutcome.REJECTED, "");
        verify(accountService, never()).removeAccountArs(anyLong(), anyString(), any());
        verify(account, times(1)).setAccountStatus(accountDetailsDTO.getAccountStatus()); // first call is during setup
    }

    @Test
    void shouldRemoveFromTrustedAccounts() {
        TrustedAccount ta1 = new TrustedAccount();
        ta1.setId(TRUSTED_ACCOUNT_ID_1);
        ta1.setTrustedAccountFullIdentifier(TEST_ACCOUNT_NUMBER);
        TrustedAccount ta2 = new TrustedAccount();
        ta2.setId(TRUSTED_ACCOUNT_ID_2);
        ta2.setTrustedAccountFullIdentifier(TEST_ACCOUNT_NUMBER);
        when(trustedAccountRepository.findAllByTrustedAccountFullIdentifier(TEST_ACCOUNT_NUMBER))
            .thenReturn(List.of(ta1, ta2));

        accountClosureTaskService.complete(task, TaskOutcome.APPROVED, "");

        verify(trustedAccountRepository, times(1)).deleteAll(List.of(ta1, ta2));
    }

    @Test
    void shouldRemoveArs() {
        accountClosureTaskService.complete(task, TaskOutcome.APPROVED, "");
        verify(accountService, times(1)).removeAccountArs(anyLong(), anyString(), any());
    }
}
