package gov.uk.ets.registry.api.tal.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountListType;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.error.TrustedAccountListActionException;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;


class TrustedAccountListServiceImplTest {
    @Mock
    TrustedAccountRepository trustedAccountRepository;

    @Mock
    AccountRepository accountRepository;

    @Mock
    UserService userService;
    @Mock
    PersistenceService persistenceService;

    @Mock
    TaskEventService taskEventService;

    @Mock
    EventService eventService;

    @Mock
    Mapper mapper;

    TrustedAccountListServiceImpl service;

    private static final String ACCOUNT_FULL_IDENTIFIER = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TrustedAccountListServiceImpl(trustedAccountRepository, userService,
                accountRepository, persistenceService, new TrustedAccountConversionService(accountRepository),
            eventService, taskEventService, mapper);
        List<Account> trustedAccountsUnderSameAccountHolder = getTrustedAccountsOfTheSameAccountHolder();
        Mockito.when(accountRepository.findAccountsOfTheSameAccountHolder(10001L))
                .thenReturn(trustedAccountsUnderSameAccountHolder);
        List<TrustedAccount> otherTrustedAccountsApprovedOrActive = getOtherTrustedAccounts(
                Arrays.asList(TrustedAccountStatus.ACTIVE, TrustedAccountStatus.PENDING_ACTIVATION));
        Mockito.when(trustedAccountRepository.findAllByAccountIdentifierAndStatusIn(10001L,
                        Arrays.asList(TrustedAccountStatus.ACTIVE, TrustedAccountStatus.PENDING_ACTIVATION, TrustedAccountStatus.PENDING_REMOVAL_APPROVAL)))
                .thenReturn(otherTrustedAccountsApprovedOrActive);
        List<TrustedAccount> otherTrustedAccountsPendingApproval = getOtherTrustedAccounts(
                Collections.singletonList(TrustedAccountStatus.PENDING_ADDITION_APPROVAL));
        Mockito.when(trustedAccountRepository.findAllByAccountIdentifierAndStatusIn(10001L,
                        Collections.singletonList(TrustedAccountStatus.PENDING_ADDITION_APPROVAL)))
                .thenReturn(otherTrustedAccountsPendingApproval);
        List<TrustedAccount> otherTrustedAccountsAll = getOtherTrustedAccounts(null);
        Mockito.when(trustedAccountRepository.findAllByAccountIdentifier(10001L))
                .thenReturn(otherTrustedAccountsAll);

        Account mockedAccount = Mockito.mock(Account.class);
        Mockito.when(mockedAccount.getFullIdentifier()).thenReturn(ACCOUNT_FULL_IDENTIFIER);
        Mockito.when(accountRepository.findByIdentifier(20002L)).thenReturn(Optional.of(mockedAccount));
    }

    @DisplayName("Test all trusted accounts")
    @Test
    void testFetchingOfAllTrustedAccounts() {
        Set<TrustedAccountDTO> trustedAccounts = service.getTrustedAccounts(10001L);
        Assertions.assertTrue(trustedAccounts.stream().filter(TrustedAccountDTO::getUnderSameAccountHolder)
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("UK-100-123456-123")));
        Assertions.assertTrue(trustedAccounts.stream().filter(TrustedAccountDTO::getUnderSameAccountHolder)
                .anyMatch(ta -> ta.getName().equals("Test trusting account under same account holder 1")));
        Assertions.assertTrue(trustedAccounts.stream().filter(ta -> !ta.getUnderSameAccountHolder())
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("JP-100-123131-123")));
        Assertions.assertTrue(trustedAccounts.stream().filter(ta -> !ta.getUnderSameAccountHolder())
                .anyMatch(ta -> ta.getDescription().equals("Other test trusting account")));
    }

    @DisplayName("Test account not found when adding trusted account")
    @Test
    void testAddTrustedAccountAccountNotFound() {

        TrustedAccountDTO TRUSTED_ACCOUNT_DTO = TrustedAccountDTO.builder()
                .accountFullIdentifier(ACCOUNT_FULL_IDENTIFIER)
                .id(123L)
                .description("test test")
                .underSameAccountHolder(false)
                .build();

        Assert.assertThrows(NotFoundException.class, () -> {
            service.addTrustedAccount(TRUSTED_ACCOUNT_DTO, 10001L);
        });
    }

    @DisplayName("Test account cannot be added on its own trusted account list")
    @Test
    void testAddTrustedCannotAddAccountToItsOwnTrustedAccountList() {
        TrustedAccountDTO TRUSTED_ACCOUNT_DTO = TrustedAccountDTO.builder()
                .accountFullIdentifier(ACCOUNT_FULL_IDENTIFIER)
                .id(123L)
                .description("test test")
                .underSameAccountHolder(false)
                .build();

        Assert.assertThrows(TrustedAccountListActionException.class, () -> {
            service.addTrustedAccount(TRUSTED_ACCOUNT_DTO, 20002L);
        });
    }

    @DisplayName("Test trusted accounts under same account holder")
    @Test
    void testFetchingTrustedAccountsUnderSameAccountHolder() {
        Set<TrustedAccountDTO> trustedAccounts =
                service.getTrustedAccounts(10001L, TrustedAccountListType.SAME_ACCOUNT_HOLDER);
        Assertions.assertTrue(trustedAccounts.stream().allMatch(TrustedAccountDTO::getUnderSameAccountHolder));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("UK-100-123456-123")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getName().equals("Test trusting account under same account holder 1")));
    }

    @DisplayName("Test other trusted accounts pending approval")
    @Test
    void testFetchingOtherPendingApprovalTrustedAccounts() {
        Set<TrustedAccountDTO> trustedAccounts =
                service.getTrustedAccounts(10001L, TrustedAccountListType.OTHER_PENDING_APPROVAL);
        Assertions.assertTrue(trustedAccounts.stream().noneMatch(TrustedAccountDTO::getUnderSameAccountHolder));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("JP-100-432345-123")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getDescription().equals("Other test trusting account pending approval")));
    }

    @DisplayName("Test other trusted accounts active or pending activation")
    @Test
    void testFetchingOtherActiveOrPendingActivationTrustedAccounts() {
        Set<TrustedAccountDTO> trustedAccounts =
                service.getTrustedAccounts(10001L, TrustedAccountListType.OTHER_APPROVED_OR_ACTIVATED);
        Assertions.assertTrue(trustedAccounts.stream().noneMatch(TrustedAccountDTO::getUnderSameAccountHolder));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("JP-100-123131-123")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getDescription().equals("Other test trusting account")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("JP-100-125474-123")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getDescription().equals("Other test trusting account pending activation")));
    }

    @DisplayName("Test all other trusted accounts")
    @Test
    void testFetchingAllOtherTrustedAccounts() {
        Set<TrustedAccountDTO> trustedAccounts = service.getTrustedAccounts(10001L, TrustedAccountListType.OTHER_ALL);
        Assertions.assertTrue(trustedAccounts.stream().noneMatch(TrustedAccountDTO::getUnderSameAccountHolder));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("JP-100-432345-123")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getDescription().equals("Other test trusting account pending approval")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("JP-100-123131-123")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getDescription().equals("Other test trusting account")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("JP-100-125474-123")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getDescription().equals("Other test trusting account pending activation")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getAccountFullIdentifier().equals("JP-100-67453-123")));
        Assertions.assertTrue(trustedAccounts.stream()
                .anyMatch(ta -> ta.getDescription().equals("Other test trusting account rejected")));
    }

    private List<Account> getTrustedAccountsOfTheSameAccountHolder() {
        Account trustedAccount = Mockito.mock(Account.class);
        Mockito.when(trustedAccount.getAccountName()).thenReturn("Test trusting account under same account holder 1");
        Mockito.when(trustedAccount.getFullIdentifier()).thenReturn("UK-100-123456-123");
        return Collections.singletonList(trustedAccount);
    }

    private List<TrustedAccount> getOtherTrustedAccounts(List<TrustedAccountStatus> statuses) {
        TrustedAccount trustedAccount = Mockito.mock(TrustedAccount.class);
        Mockito.when(trustedAccount.getDescription()).thenReturn("Other test trusting account");
        Mockito.when(trustedAccount.getTrustedAccountFullIdentifier()).thenReturn("JP-100-123131-123");
        Mockito.when(trustedAccount.getStatus()).thenReturn(TrustedAccountStatus.ACTIVE);
        TrustedAccount trustedAccount2 = Mockito.mock(TrustedAccount.class);
        Mockito.when(trustedAccount2.getDescription()).thenReturn("Other test trusting account pending approval");
        Mockito.when(trustedAccount2.getTrustedAccountFullIdentifier()).thenReturn("JP-100-432345-123");
        Mockito.when(trustedAccount2.getStatus()).thenReturn(TrustedAccountStatus.PENDING_ADDITION_APPROVAL);
        TrustedAccount trustedAccount3 = Mockito.mock(TrustedAccount.class);
        Mockito.when(trustedAccount3.getDescription()).thenReturn("Other test trusting account pending activation");
        Mockito.when(trustedAccount3.getTrustedAccountFullIdentifier()).thenReturn("JP-100-125474-123");
        Mockito.when(trustedAccount3.getStatus()).thenReturn(TrustedAccountStatus.PENDING_ACTIVATION);
        TrustedAccount trustedAccount4 = Mockito.mock(TrustedAccount.class);
        Mockito.when(trustedAccount4.getDescription()).thenReturn("Other test trusting account rejected");
        Mockito.when(trustedAccount4.getTrustedAccountFullIdentifier()).thenReturn("JP-100-67453-123");
        Mockito.when(trustedAccount4.getStatus()).thenReturn(TrustedAccountStatus.REJECTED);
        List<TrustedAccount> trustedAccounts = new ArrayList<>();
        trustedAccounts.add(trustedAccount);
        trustedAccounts.add(trustedAccount2);
        trustedAccounts.add(trustedAccount3);
        trustedAccounts.add(trustedAccount4);
        return trustedAccounts.stream().filter(ta -> statuses == null || statuses.contains(ta.getStatus()))
                .collect(Collectors.toList());
    }
}
