package gov.uk.ets.registry.api.accounttransfer.service;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountOwnership;
import gov.uk.ets.registry.api.account.domain.SalesContact;
import gov.uk.ets.registry.api.account.domain.types.AccountOwnershipStatus;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountOwnershipRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.service.TransferValidationService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferAction;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferActionType;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferTaskDetailsDTO;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountTransferTaskServiceTest {

    private static final String TEST_ACCOUNT_NUMBER = "123456";
    private static final Long TEST_ACCOUNT_HOLDER_ID = 1L;
    private static final Long TRUSTED_ACCOUNT_ID_1 = 2L;
    private static final Long TRUSTED_ACCOUNT_ID_2 = 3L;
    public static final String TEST_URID = "UK12345667890";
    public static final String IAM_ID = "a1404809-742f-49f6-b14c-39149369ecea";
    private static final String REMOVAL_REASON = "account transfer";


    @Mock
    private Mapper mapper;
    @Mock
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountHolderRepository accountHolderRepository;
    @Mock
    private TrustedAccountRepository trustedAccountRepository;
    @Mock
    private TransferValidationService transferValidationService;
    @Mock
    private UserService userService;
    @Mock
    private EventService eventService;
    @Mock
    private User currentUser;
    @Mock
    private AccountOwnershipRepository accountOwnershipRepository;
    @Mock
    private AccountAccessRepository accountAccessRepository;
    @Mock
    private AuthorizedRepresentativeService authorizedRepresentativeService;

    @InjectMocks
    private AccountTransferTaskService cut;

    @Spy
    private Account account;
    @Spy
    private AccountOwnership accountOwnership;
    @Captor
    ArgumentCaptor<AccountOwnership> accountOwnershipCaptor;
    @Spy
    AccountAccess ar1;

    private AccountTransferTaskDetailsDTO task;
    private AccountHolder newAccountHolder;
    private AccountTransferAction action;

    @BeforeEach
    void setUp() {
        newAccountHolder = new AccountHolder();
        Account a1 = new Account();
        a1.setId(TRUSTED_ACCOUNT_ID_1);
        
        newAccountHolder.setAccounts(List.of(a1));
        AccountHolder oldAccountHolder = new AccountHolder();
        account.setAccountHolder(oldAccountHolder);
        SalesContact salesContact = SalesContact.builder()
								                .emailAddress("dummy@trasys.gr")
								                .phoneNumber("6909999999")
								                .phoneNumberCountry("GR(30)")
								                .build() ;
        account.setSalesContact(salesContact);

        when(accountHolderRepository.getAccountHolder(TEST_ACCOUNT_HOLDER_ID)).thenReturn(newAccountHolder);

        when(accountRepository.findByIdentifier(Long.valueOf(TEST_ACCOUNT_NUMBER))).thenReturn(Optional.of(account));

        when(userService.getCurrentUser()).thenReturn(currentUser);

        when(accountOwnershipRepository
            .findByAccountAndHolderAndStatus(account, newAccountHolder
                , AccountOwnershipStatus.ACTIVE))
            .thenReturn(List.of(accountOwnership));

        TaskDetailsDTO taskDetails = new TaskDetailsDTO();
        task = new AccountTransferTaskDetailsDTO(taskDetails);
        action = new AccountTransferAction();
        action.setType(AccountTransferActionType.ACCOUNT_TRANSFER_TO_EXISTING_HOLDER);
        action.setAccountHolderDTO(
            AccountHolderDTO.builder().id(TEST_ACCOUNT_HOLDER_ID).details(new DetailsDTO()).build());
        action.setPreviousAccountStatus(AccountStatus.OPEN);
        task.setAction(action);
        task.setAccountNumber(TEST_ACCOUNT_NUMBER);
    }

    @Test
    void shouldUpdateAccountHolderAndStatus() {
        TaskOutcome taskOutcome = TaskOutcome.APPROVED;
        String comment = "";
        TaskCompleteResponse complete = cut.complete(task, taskOutcome, comment);

        assertThat(complete).isNotNull();

        verify(account, times(2)).setAccountHolder(newAccountHolder); // first call is during setup
        verify(account, times(1)).setAccountStatus(action.getPreviousAccountStatus());
    }

    @Test
    void shouldRemoveFromTrustedAccounts() {

        TrustedAccount ta1 = new TrustedAccount();
        ta1.setId(TRUSTED_ACCOUNT_ID_1);
        TrustedAccount ta2 = new TrustedAccount();
        ta2.setId(TRUSTED_ACCOUNT_ID_2);
        when(trustedAccountRepository.findAllByAccountIdentifier(Long.valueOf(TEST_ACCOUNT_NUMBER)))
            .thenReturn(List.of(ta1, ta2));

        TaskOutcome taskOutcome = TaskOutcome.APPROVED;
        String comment = "";
        cut.complete(task, taskOutcome, comment);

        verify(trustedAccountRepository, times(1)).deleteAll(List.of(ta1,ta2));
    }

    @Test
    void shouldCreateNewAccountHolder() {
        action.setType(AccountTransferActionType.ACCOUNT_TRANSFER_TO_CREATED_HOLDER);
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        accountHolderDTO.setDetails(new DetailsDTO());
        action.setAccountHolderDTO(accountHolderDTO);
        AccountHolderRepresentativeDTO primaryContact = new AccountHolderRepresentativeDTO();
        action.setAccountHolderContactInfo(primaryContact);

        AccountHolder newAh = new AccountHolder();
        newAh.setAccounts(new ArrayList<>());
        when(accountService.createHolder(accountHolderDTO)).thenReturn(newAh);

        TaskOutcome taskOutcome = TaskOutcome.APPROVED;
        String comment = "";
        cut.complete(task, taskOutcome, comment);

        verify(accountService, times(1)).createHolder(accountHolderDTO);
        verify(accountService, times(1)).addAccountHolderContact(newAccountHolder, primaryContact, true);


    }

    @Test
    void shouldRevertAccountStatusWhenRejectingTask() {
        cut.complete(task, TaskOutcome.REJECTED, "");

        verify(accountService, never()).createHolder(any());
        verify(account, times(1)).setAccountStatus(action.getPreviousAccountStatus());
    }

    @Test
    void shouldUpdateAccountOwnershipRecords() {

        cut.complete(task, TaskOutcome.APPROVED, "");

        verify(accountOwnershipRepository, times(1))
            .findByAccountAndHolderAndStatus(account, newAccountHolder, AccountOwnershipStatus.ACTIVE);
        verify(accountOwnership, times(1)).setStatus(AccountOwnershipStatus.INACTIVE);

        verify(accountOwnershipRepository, times(1)).save(accountOwnershipCaptor.capture());
        AccountOwnership newAccountOwnership = accountOwnershipCaptor.getValue();
        assertThat(newAccountOwnership.getAccount()).isEqualTo(account);
        assertThat(newAccountOwnership.getStatus()).isEqualTo(AccountOwnershipStatus.ACTIVE);

    }

    @Test
    void shouldRemoveArs() {
        cut.complete(task, TaskOutcome.APPROVED, "");
        verify(accountService, times(1)).removeAccountArs(Long.valueOf(TEST_ACCOUNT_NUMBER), REMOVAL_REASON, currentUser);
    }

    @Test
    void shouldClearSalesContactDetails() {
        cut.complete(task, TaskOutcome.APPROVED, "");
        assertNull(account.getSalesContact());
    }

    @Test
    void shouldKeepSalesContactDetails() {
        cut.complete(task, TaskOutcome.REJECTED, "");
        assertThat(account.getSalesContact()).isNotNull();
        assertThat(account.getSalesContact().getEmailAddress()).isEqualTo("dummy@trasys.gr");
        assertThat(account.getSalesContact().getPhoneNumber()).isEqualTo("6909999999");
        assertThat(account.getSalesContact().getPhoneNumberCountry()).isEqualTo("GR(30)");
    }
}
