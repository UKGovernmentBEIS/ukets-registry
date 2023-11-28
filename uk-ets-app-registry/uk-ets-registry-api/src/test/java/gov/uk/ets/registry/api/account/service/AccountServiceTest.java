package gov.uk.ets.registry.api.account.service;


import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.repository.AccountOwnershipRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.repository.InstallationOwnershipRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHoldingsSummaryDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHoldingsSummaryResultDTO;
import gov.uk.ets.registry.api.account.web.model.AccountStatusAction;
import gov.uk.ets.registry.api.account.web.model.AccountStatusActionOptionDTO;
import gov.uk.ets.registry.api.account.web.model.AccountStatusChangeDTO;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.AuthorizationServiceImpl;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.common.reports.ReportRequestService;
import gov.uk.ets.registry.api.common.reports.ReportRoleMappingService;
import gov.uk.ets.registry.api.common.view.ConversionParameters;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.helper.AuthorizationTestHelper;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckService;
import gov.uk.ets.registry.api.transaction.common.GeneratorService;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilterFactory;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.AccountTotalRepository;
import gov.uk.ets.registry.api.transaction.repository.SearchableTransactionRepository;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import gov.uk.ets.registry.api.transaction.service.TransactionProposalService;
import gov.uk.ets.registry.api.transaction.service.TransactionProposalTaskService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Testing account related service methods")
class AccountServiceTest {

    @Mock
    AccountHolderRepresentativeRepository legalRepresentativeRepository;
    @Mock
    private PersistenceService persistenceService;
    @Mock
    private ConversionService conversionService;
    @Mock
    private AccountConversionService accountConversionService;
    @Mock
    private GeneratorService generatorService;
    @Mock
    private UserService userService;
    @Mock
    private AccountHolderRepository holderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountDTOFactory accountDTOFactory;
    @Mock
    private CompliantEntityRepository compliantEntityRepository;
    @Mock
    private AccountAccessRepository accountAccessRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AuthorizationServiceImpl authService;
    @Mock
    private UnitBlockRepository unitBlockRepository;
    @Mock
    private EventService eventService;

    private AccountService accountService;

    @Mock
    private TransactionProposalService transactionProposalService;
    @Mock
    private BusinessCheckService businessCheckService;
    @Mock
    private TransactionFilterFactory transactionFilterFactory;
    @Mock
    private SearchableTransactionRepository searchableTransactionRepository;
    @Mock
    private AccountTotalRepository accountTotalRepository;
    @Mock
    private ReportRequestService reportRequestService;
    @Mock
    private TransferValidationService transferValidationService;
    @Mock
    private InstallationOwnershipRepository installationOwnershipRepository;
    @Mock
    private AccountOwnershipRepository accountOwnershipRepository;
    @Mock
    private AuthorizedRepresentativeService authorizedRepresentativeService;
    @Mock
    private TaskEventService taskEventService;
    @Mock
    private AccountStatusService accountStatusService;
    @Mock
    private AccountValidationService accountValidationService;
    @Mock
    private ReportRoleMappingService reportRoleMappingService;
    @Mock
    private Mapper mapper;

    @Spy
    AccountAccess accountAccess;

    AccountServiceTest() {
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        User currentUser = Mockito.mock(User.class);
        authService = Mockito.mock(AuthorizationServiceImpl.class);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        accountService = new AccountService(
            persistenceService,
            conversionService,
            accountConversionService,
            generatorService, userService,
            accountRepository,
            holderRepository,
            userRepository,
            legalRepresentativeRepository,
            compliantEntityRepository,
            accountDTOFactory,
            authService,
            unitBlockRepository,
            transactionProposalService,
            businessCheckService,
            eventService,
            transactionFilterFactory,
            searchableTransactionRepository,
            accountTotalRepository,
            reportRequestService,
            mapper,
            transferValidationService,
            installationOwnershipRepository,
            accountOwnershipRepository,
            taskEventService,
            accountValidationService,
            accountAccessRepository,
            authorizedRepresentativeService,
            accountStatusService,
            reportRoleMappingService);
    }

    @Test
    @DisplayName("Non Kyoto Accounts with status OPEN should have 3 options , expected to succeed")
    void changeAccountStatusOptionsWithNonKyotoOpenAccount() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        List<AccountStatusActionOptionDTO> result =
            accountService.getAccountStatusAvailableActions(account.getIdentifier());
        Set<AccountStatusAction> codes =
            result.stream().map(AccountStatusActionOptionDTO::getValue).collect(Collectors.toSet());
        assertEquals(3, result.size());
        assertTrue(codes.contains(AccountStatusAction.SUSPEND));
        assertTrue(codes.contains(AccountStatusAction.SUSPEND_PARTIALLY));
        assertTrue(codes.contains(AccountStatusAction.RESTRICT_SOME_TRANSACTIONS));
    }

    @Test
    @DisplayName("Accounts with status TRANSFER_PENDING expected to fail")
    void changeAccountStatusOptionsWithTransferPendingAccount() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.TRANSFER_PENDING);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        Long accountIdentifier = account.getIdentifier();
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountStatusAvailableActions(accountIdentifier));
    }

    @Test
    @DisplayName("Non Kyoto Accounts with status OPEN should have 2 options , expected to succeed")
    void changeAccountStatusOptionsWithKyotoOpenAccount() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.NONE);
        account.setKyotoAccountType(KyotoAccountType.VOLUNTARY_CANCELLATION_ACCOUNT);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        List<AccountStatusActionOptionDTO> result =
            accountService.getAccountStatusAvailableActions(account.getIdentifier());
        Set<AccountStatusAction> codes =
            result.stream().map(AccountStatusActionOptionDTO::getValue).collect(Collectors.toSet());
        assertEquals(2, result.size());
        assertTrue(codes.contains(AccountStatusAction.SUSPEND));
        assertTrue(codes.contains(AccountStatusAction.SUSPEND_PARTIALLY));
    }

    @Test
    @DisplayName("Accounts with status SOME_TRANSACTIONS_RESTRICTED should have 3 options , expected to succeed")
    void changeAccountStatusOptionsWithBlockedByAdminAccount() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.SOME_TRANSACTIONS_RESTRICTED);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        List<AccountStatusActionOptionDTO> result =
            accountService.getAccountStatusAvailableActions(account.getIdentifier());
        Set<AccountStatusAction> codes =
            result.stream().map(AccountStatusActionOptionDTO::getValue).collect(Collectors.toSet());
        assertEquals(3, result.size());
        assertTrue(codes.contains(AccountStatusAction.SUSPEND));
        assertTrue(codes.contains(AccountStatusAction.SUSPEND_PARTIALLY));
        assertTrue(codes.contains(AccountStatusAction.REMOVE_RESTRICTIONS));
    }

    @Test
    @DisplayName("Accounts with status SUSPENDED should have 1 option , expected to succeed")
    void changeAccountStatusOptionsWithSuspendedAccount() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.SUSPENDED);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        List<AccountStatusActionOptionDTO> result =
            accountService.getAccountStatusAvailableActions(account.getIdentifier());
        Set<AccountStatusAction> codes =
            result.stream().map(AccountStatusActionOptionDTO::getValue).collect(Collectors.toSet());
        assertEquals(3, result.size());
        assertTrue(codes.contains(AccountStatusAction.UNSUSPEND));
    }

    @Test
    @DisplayName("Accounts with status SUSPENDED_PARTIALLY should have 3 option , expected to succeed")
    void changeAccountStatusOptionsWithSuspendedPartiallyAccount() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.SUSPENDED_PARTIALLY);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        List<AccountStatusActionOptionDTO> result =
            accountService.getAccountStatusAvailableActions(account.getIdentifier());
        Set<AccountStatusAction> codes =
            result.stream().map(AccountStatusActionOptionDTO::getValue).collect(Collectors.toSet());
        assertEquals(3, result.size());
        assertTrue(codes.contains(AccountStatusAction.UNSUSPEND));
    }

    @Test
    @DisplayName("Non Kyoto Accounts with status ALL_TRANSACTIONS_RESTRICTED expected to fail")
    void changeAccountStatusOptionsWithNonKyotoBlockedAccount() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.ALL_TRANSACTIONS_RESTRICTED);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        Long accountIdentifier = account.getIdentifier();
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountStatusAvailableActions(accountIdentifier));
    }

    @Test
    @DisplayName("Kyoto Accounts with status ALL_TRANSACTIONS_RESTRICTED expected to fail")
    void changeAccountStatusOptionsWithKyotoBlockedAccount() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.ALL_TRANSACTIONS_RESTRICTED);
        account.setRegistryAccountType(RegistryAccountType.NONE);
        account.setKyotoAccountType(KyotoAccountType.VOLUNTARY_CANCELLATION_ACCOUNT);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        Long accountIdentifier = account.getIdentifier();
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountStatusAvailableActions(accountIdentifier));
    }

    @ParameterizedTest(name = "{index} Account with status {0} does not have any options to change ")
    @CsvSource(value = {"CLOSED"})
    @DisplayName("Accounts with invalid statuses should have no options to change the status , expected to succeed")
    void changeAccountStatusOptionsWithIvalidCurrentStatus(String status) {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.valueOf(status));
        //A kyoto account
        account.setRegistryAccountType(RegistryAccountType.NONE);
        account.setKyotoAccountType(KyotoAccountType.VOLUNTARY_CANCELLATION_ACCOUNT);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        Long accountIdentifier = account.getIdentifier();
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountStatusAvailableActions(accountIdentifier));
    }

    @Test
    @DisplayName("Partially suspend account with outstanding closure requests , expected to fail")
    @Disabled
    void partiallySuspendAccountWithOutstandingClosureRequest() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.TRANSFER_PENDING);
        //Prepare request
        AccountStatusChangeDTO request = new AccountStatusChangeDTO();
        request.setAccountStatus(AccountStatus.SUSPENDED_PARTIALLY);
        request.setComment("Partially suspend the account");
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        AccountActionException exception = assertThrows(
            AccountActionException.class,
            () -> accountService.updateAccountStatus(account.getIdentifier(), request));

        assertTrue(exception.getMessage()
            .contains("You cannot partially suspend an account with outstanding closure requests."));
        assertTrue(exception.getAccountActionError().getMessage()
            .contains("You cannot partially suspend an account with outstanding closure requests."));
        assertTrue(
            exception.getAccountActionError().getCode().contains(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED));
    }

    @Test
    @DisplayName("Partially block account with outstanding closure requests , expected to fail")
    @Disabled
    void partiallyBlockAccountWithOutstandingClosureRequest() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.TRANSFER_PENDING);
        //Prepare request
        AccountStatusChangeDTO request = new AccountStatusChangeDTO();
        request.setAccountStatus(AccountStatus.SOME_TRANSACTIONS_RESTRICTED);
        request.setComment("Partially block the account");
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        AccountActionException exception = assertThrows(
            AccountActionException.class,
            () -> accountService.updateAccountStatus(account.getIdentifier(), request));

        assertTrue(exception.getMessage()
            .contains("You cannot partially block an account with outstanding closure requests."));
        assertTrue(exception.getAccountActionError().getMessage()
            .contains("You cannot partially block an account with outstanding closure requests."));
        assertTrue(
            exception.getAccountActionError().getCode().contains(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED));
    }

    @Test
    @DisplayName("Unblock account with outstanding closure requests , expected to fail")
    @Disabled
    void unblockAccountWithOutstandingClosureRequest() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.TRANSFER_PENDING);
        //Prepare request
        AccountStatusChangeDTO request = new AccountStatusChangeDTO();
        request.setAccountStatus(AccountStatus.OPEN);
        request.setComment("Unblock the account");
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        AccountActionException exception = assertThrows(
            AccountActionException.class,
            () -> accountService.updateAccountStatus(account.getIdentifier(), request));

        assertTrue(exception.getMessage().contains("You cannot unblock an account with outstanding closure requests."));
        assertTrue(exception.getAccountActionError().getMessage()
            .contains("You cannot unblock an account with outstanding closure requests."));
        assertTrue(
            exception.getAccountActionError().getCode().contains(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED));
    }

    @Test
    @DisplayName("Account Holdings AAUs Subject & Not Subject to SOP available & reserved are group correctly.")
    void accountHoldings() {
        Long accountIdentifier = 33L;
        List<UnitBlock> blocks = new ArrayList<>();
        //Prepare unit blocks
        UnitBlock aauSOPNotReserved = new UnitBlock();
        aauSOPNotReserved.setType(UnitType.AAU);
        aauSOPNotReserved.setAccountIdentifier(accountIdentifier);
        aauSOPNotReserved.setSubjectToSop(Boolean.TRUE);
        aauSOPNotReserved.setStartBlock(3101L);
        aauSOPNotReserved.setEndBlock(3200L);
        //Quantity available = 100
        aauSOPNotReserved.setOriginalPeriod(CommitmentPeriod.CP2);
        aauSOPNotReserved.setApplicablePeriod(CommitmentPeriod.CP2);
        aauSOPNotReserved.setOriginatingCountryCode("UK");
        aauSOPNotReserved.setReservedForTransaction(null);
        blocks.add(aauSOPNotReserved);

        UnitBlock aauSOPReserved = new UnitBlock();
        aauSOPReserved.setType(UnitType.AAU);
        aauSOPReserved.setAccountIdentifier(accountIdentifier);
        aauSOPReserved.setSubjectToSop(Boolean.TRUE);
        aauSOPReserved.setStartBlock(4101L);
        aauSOPReserved.setEndBlock(4118L);
        //Quantity reserved = 18
        aauSOPReserved.setOriginalPeriod(CommitmentPeriod.CP2);
        aauSOPReserved.setApplicablePeriod(CommitmentPeriod.CP2);
        aauSOPReserved.setOriginatingCountryCode("UK");
        aauSOPReserved.setReservedForTransaction("UK-66363639");
        blocks.add(aauSOPReserved);

        UnitBlock aauNotSubjectSOPNotReserved = new UnitBlock();
        aauNotSubjectSOPNotReserved.setType(UnitType.AAU);
        aauNotSubjectSOPNotReserved.setAccountIdentifier(accountIdentifier);
        aauNotSubjectSOPNotReserved.setSubjectToSop(Boolean.FALSE);
        aauNotSubjectSOPNotReserved.setStartBlock(3201L);
        aauNotSubjectSOPNotReserved.setEndBlock(3300L);
        //Quantity available = 100
        aauNotSubjectSOPNotReserved.setOriginalPeriod(CommitmentPeriod.CP2);
        aauNotSubjectSOPNotReserved.setApplicablePeriod(CommitmentPeriod.CP2);
        aauNotSubjectSOPNotReserved.setOriginatingCountryCode("UK");
        aauNotSubjectSOPNotReserved.setReservedForTransaction(null);
        blocks.add(aauNotSubjectSOPNotReserved);

        UnitBlock aauNotSubjectSOPReserved = new UnitBlock();
        aauNotSubjectSOPReserved.setType(UnitType.AAU);
        aauNotSubjectSOPReserved.setAccountIdentifier(accountIdentifier);
        aauNotSubjectSOPReserved.setSubjectToSop(Boolean.FALSE);
        aauNotSubjectSOPReserved.setStartBlock(4201L);
        aauNotSubjectSOPReserved.setEndBlock(4300L);
        //Quantity reserved = 100
        aauNotSubjectSOPReserved.setOriginalPeriod(CommitmentPeriod.CP2);
        aauNotSubjectSOPReserved.setApplicablePeriod(CommitmentPeriod.CP2);
        aauNotSubjectSOPReserved.setOriginatingCountryCode("UK");
        aauNotSubjectSOPReserved.setReservedForTransaction("UK-87655567");
        blocks.add(aauNotSubjectSOPReserved);

        UnitBlock aauNotSubjectSOPReservedCP1Origin = new UnitBlock();
        aauNotSubjectSOPReservedCP1Origin.setType(UnitType.AAU);
        aauNotSubjectSOPReservedCP1Origin.setAccountIdentifier(accountIdentifier);
        aauNotSubjectSOPReservedCP1Origin.setSubjectToSop(Boolean.FALSE);
        aauNotSubjectSOPReservedCP1Origin.setStartBlock(5211L);
        aauNotSubjectSOPReservedCP1Origin.setEndBlock(5600L);
        //Quantity reserved = 390
        aauNotSubjectSOPReservedCP1Origin.setOriginalPeriod(CommitmentPeriod.CP1);
        aauNotSubjectSOPReservedCP1Origin.setApplicablePeriod(CommitmentPeriod.CP2);
        aauNotSubjectSOPReservedCP1Origin.setOriginatingCountryCode("UK");
        aauNotSubjectSOPReservedCP1Origin.setReservedForTransaction("UK-87655567");
        blocks.add(aauNotSubjectSOPReservedCP1Origin);

        //Mock unitBlockRepository
        Mockito.when(unitBlockRepository.findByAccountIdentifier(accountIdentifier)).thenReturn(blocks);

        //Prepare account
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setComplianceStatus(ComplianceStatus.B);
        //Mock accountRepository
        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        AccountHoldingsSummaryResultDTO resultSummary = accountService.getAccountHoldings(accountIdentifier);

        assertEquals(3, resultSummary.getItems().size());
        assertEquals(200, resultSummary.getTotalAvailableQuantity());
        assertEquals(508, resultSummary.getTotalReservedQuantity());
        assertEquals(ComplianceStatus.B, resultSummary.getCurrentComplianceStatus());
        assertTrue(resultSummary.isShouldMeetEmissionsTarget());

        AccountHoldingsSummaryDTO aausNotSubjectToSOPCP2CP2 = resultSummary.getItems().get(0);
        assertEquals(UnitType.AAU, aausNotSubjectToSOPCP2CP2.getType());
        assertEquals(CommitmentPeriod.CP2, aausNotSubjectToSOPCP2CP2.getOriginalPeriod());
        assertEquals(CommitmentPeriod.CP2, aausNotSubjectToSOPCP2CP2.getApplicablePeriod());
        assertEquals(100L, aausNotSubjectToSOPCP2CP2.getAvailableQuantity());
        assertEquals(100L, aausNotSubjectToSOPCP2CP2.getReservedQuantity());
        assertFalse(aausNotSubjectToSOPCP2CP2.getSubjectToSop());

        AccountHoldingsSummaryDTO aausSubjectToSOPCP2CP2 = resultSummary.getItems().get(1);
        assertEquals(UnitType.AAU, aausSubjectToSOPCP2CP2.getType());
        assertEquals(CommitmentPeriod.CP2, aausSubjectToSOPCP2CP2.getOriginalPeriod());
        assertEquals(CommitmentPeriod.CP2, aausSubjectToSOPCP2CP2.getApplicablePeriod());
        assertEquals(100L, aausSubjectToSOPCP2CP2.getAvailableQuantity());
        assertEquals(18L, aausSubjectToSOPCP2CP2.getReservedQuantity());
        assertTrue(aausSubjectToSOPCP2CP2.getSubjectToSop());

        AccountHoldingsSummaryDTO aausSubjectToSOPCP1CP2 = resultSummary.getItems().get(2);
        assertEquals(UnitType.AAU, aausSubjectToSOPCP1CP2.getType());
        assertEquals(CommitmentPeriod.CP1, aausSubjectToSOPCP1CP2.getOriginalPeriod());
        assertEquals(CommitmentPeriod.CP2, aausSubjectToSOPCP1CP2.getApplicablePeriod());
        assertNull(aausSubjectToSOPCP1CP2.getAvailableQuantity());
        assertEquals(390L, aausSubjectToSOPCP1CP2.getReservedQuantity());
        assertFalse(aausSubjectToSOPCP1CP2.getSubjectToSop());
    }

    @DisplayName("Account Holdings results should be sorted by UnitType.")
    @Test
    void accountHoldingsSortingPerUnitType() {
        Long accountIdentifier = 33L;
        List<UnitBlock> blocks = new ArrayList<>();
        //Prepare unit blocks
        UnitBlock aauSOPNotReserved = new UnitBlock();
        aauSOPNotReserved.setType(UnitType.AAU);
        aauSOPNotReserved.setAccountIdentifier(accountIdentifier);
        aauSOPNotReserved.setSubjectToSop(Boolean.TRUE);
        aauSOPNotReserved.setStartBlock(3101L);
        aauSOPNotReserved.setEndBlock(3200L);
        //Quantity available = 100
        aauSOPNotReserved.setOriginalPeriod(CommitmentPeriod.CP2);
        aauSOPNotReserved.setApplicablePeriod(CommitmentPeriod.CP2);
        aauSOPNotReserved.setOriginatingCountryCode("UK");
        aauSOPNotReserved.setReservedForTransaction(null);
        blocks.add(aauSOPNotReserved);

        UnitBlock rmuSOP = new UnitBlock();
        rmuSOP.setType(UnitType.RMU);
        rmuSOP.setAccountIdentifier(accountIdentifier);
        rmuSOP.setSubjectToSop(Boolean.TRUE);
        rmuSOP.setStartBlock(4101L);
        rmuSOP.setEndBlock(4118L);
        //Quantity reserved = 18
        rmuSOP.setOriginalPeriod(CommitmentPeriod.CP2);
        rmuSOP.setApplicablePeriod(CommitmentPeriod.CP2);
        rmuSOP.setOriginatingCountryCode("UK");
        rmuSOP.setReservedForTransaction("UK-66363639");
        blocks.add(rmuSOP);

        UnitBlock eru_from_rmu = new UnitBlock();
        eru_from_rmu.setType(UnitType.ERU_FROM_RMU);
        eru_from_rmu.setAccountIdentifier(accountIdentifier);
        eru_from_rmu.setSubjectToSop(Boolean.FALSE);
        eru_from_rmu.setStartBlock(5211L);
        eru_from_rmu.setEndBlock(5600L);
        //Quantity reserved = 390
        eru_from_rmu.setOriginalPeriod(CommitmentPeriod.CP1);
        eru_from_rmu.setApplicablePeriod(CommitmentPeriod.CP2);
        eru_from_rmu.setOriginatingCountryCode("UK");
        eru_from_rmu.setReservedForTransaction("UK-87655567");
        blocks.add(eru_from_rmu);

        UnitBlock eru_from_rmu_SOP = new UnitBlock();
        eru_from_rmu_SOP.setType(UnitType.ERU_FROM_RMU);
        eru_from_rmu_SOP.setAccountIdentifier(accountIdentifier);
        eru_from_rmu_SOP.setSubjectToSop(Boolean.TRUE);
        eru_from_rmu_SOP.setStartBlock(6265L);
        eru_from_rmu_SOP.setEndBlock(6688L);
        //Quantity available = 424
        eru_from_rmu_SOP.setOriginalPeriod(CommitmentPeriod.CP1);
        eru_from_rmu_SOP.setApplicablePeriod(CommitmentPeriod.CP2);
        eru_from_rmu_SOP.setOriginatingCountryCode("UK");
        eru_from_rmu_SOP.setReservedForTransaction(null);
        blocks.add(eru_from_rmu_SOP);

        //Mock unitBlockRepository
        Mockito.when(unitBlockRepository.findByAccountIdentifier(accountIdentifier)).thenReturn(blocks);

        AccountHoldingsSummaryResultDTO resultSummary = accountService.getAccountHoldings(accountIdentifier);

        assertEquals(4, resultSummary.getItems().size());
        assertEquals(524, resultSummary.getTotalAvailableQuantity());
        assertEquals(408, resultSummary.getTotalReservedQuantity());

        AccountHoldingsSummaryDTO aausSubjectToSOPCP2CP2 = resultSummary.getItems().get(0);
        assertEquals(UnitType.AAU, aausSubjectToSOPCP2CP2.getType());
        assertTrue(aausSubjectToSOPCP2CP2.getSubjectToSop());

        AccountHoldingsSummaryDTO rmu = resultSummary.getItems().get(1);
        assertEquals(UnitType.RMU, rmu.getType());
        assertTrue(rmu.getSubjectToSop());

        AccountHoldingsSummaryDTO eru_from_rmu_non_SOPCP1CP2 = resultSummary.getItems().get(2);
        assertEquals(UnitType.ERU_FROM_RMU, eru_from_rmu_non_SOPCP1CP2.getType());
        assertFalse(eru_from_rmu_non_SOPCP1CP2.getSubjectToSop());

        AccountHoldingsSummaryDTO eru_from_rmu_SOPCP1CP2 = resultSummary.getItems().get(3);
        assertEquals(UnitType.ERU_FROM_RMU, eru_from_rmu_SOPCP1CP2.getType());
        assertTrue(eru_from_rmu_SOPCP1CP2.getSubjectToSop());
    }

    @DisplayName(
        "Retrieve account name as display account name for account type, expected to succeed")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0}")
    void retrieveAccountDisplayNameForAccount(AccountType accountType, boolean displayAccountName) {
        Account account = retrieveAccountInfo(accountType);
        given(accountRepository.findByFullIdentifier(account.getFullIdentifier())).willReturn(Optional.of(account));

        String accountDisplayName = accountService.getAccountDisplayName(account.getFullIdentifier());
        if (displayAccountName) {
            assertEquals(accountDisplayName, account.getAccountName());
        } else {
            assertEquals(accountDisplayName, account.getFullIdentifier());
        }
        assertEquals(accountType.name(), account.getAccountType());
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
            Arguments.of(AccountType.OPERATOR_HOLDING_ACCOUNT, false),
            Arguments.of(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT, false),
            Arguments.of(AccountType.TRADING_ACCOUNT, false),
            Arguments.of(AccountType.UK_AUCTION_DELIVERY_ACCOUNT, false),
            Arguments.of(AccountType.PERSON_HOLDING_ACCOUNT, false),
            Arguments.of(AccountType.PARTY_HOLDING_ACCOUNT, true),
            Arguments.of(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT, false),
            Arguments.of(AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT, true),
            Arguments.of(AccountType.NET_SOURCE_CANCELLATION_ACCOUNT, true),
            Arguments.of(AccountType.NON_COMPLIANCE_CANCELLATION_ACCOUNT, true),
            Arguments.of(AccountType.UK_TOTAL_QUANTITY_ACCOUNT, true),
            Arguments.of(AccountType.UK_AVIATION_TOTAL_QUANTITY_ACCOUNT, false),
            Arguments.of(AccountType.UK_AUCTION_ACCOUNT, true),
            Arguments.of(AccountType.NATIONAL_HOLDING_ACCOUNT, true),
            Arguments.of(AccountType.RETIREMENT_ACCOUNT, true),
            Arguments.of(AccountType.VOLUNTARY_CANCELLATION_ACCOUNT, true),
            Arguments.of(AccountType.MANDATORY_CANCELLATION_ACCOUNT, true),
            Arguments.of(AccountType.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT, true)
        );
    }

    @Test
    @DisplayName(
        "Retrieve account name as display account name for Ambition Increase Cancellation account type, expected to " +
            "succeed")
    void retrieveAccountDisplayNameForAmbitionIncreaseCancellationCancellationAccount() {
        Account account = retrieveAccountInfo(AccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT);
        given(accountRepository.findByFullIdentifier(account.getFullIdentifier())).willReturn(Optional.of(account));

        String accountDisplayName = accountService.getAccountDisplayName(account.getFullIdentifier());
        assertEquals(accountDisplayName, account.getAccountName());
        assertEquals(AccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT.name(), account.getAccountType());
    }

    private Account retrieveAccountInfo(AccountType accountType) {
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setFullIdentifier("UK-100-10000047-2-14");
        account.setAccountName("Account Name");
        account.setRegistryAccountType(accountType.getRegistryType());
        account.setKyotoAccountType(accountType.getKyotoType());
        account.setAccountType(accountType.name());
        return account;
    }

    @Test()
    @DisplayName("Performs checks in the Account Full Identifier.")
    void checkAccountFullIdentifier() {
        /* Missing Fields */
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("100-1001-1-89"));
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("GB--1001-1-89"));
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("GB-100--1-89"));
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("GB-100-1001-89"));
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("GB-100-1001-1"));

        /* Invalid input in fields. Expected number or letters and vice versa. */
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("GB1-100-1001-1-89"));
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("GB-100-1001K-1-89"));
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("GB-100-1001-1K-89"));
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("GB-100-1001-1-89K"));

        /* Empty input */
        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier(""));

        when(accountTotalRepository.getAccountSummary((String) any())).thenReturn(null);

        assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("GB-100-1001-1-89"));

        when(accountTotalRepository.getAccountSummary((String) any())).thenReturn(
            AccountSummary.parse("GB-100-1001-1-89", RegistryAccountType.NONE, AccountStatus.OPEN));

        /* Correct input */
        assertDoesNotThrow(() ->
            accountService.checkAccountFullIdentifier("GB-100-1001-1-89"));
        assertDoesNotThrow(() ->
            accountService.checkAccountFullIdentifier("JP-100-1001"));
    }

    @Test()
    @DisplayName("Performs checks in the Account Full Identifier (UK-100-10-2-14).")
    void checkAccountFullIdentifier_ShouldReturnCheckDigitsError_TwoCheckDigits() {
        BusinessCheckException exception = assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("UK-100-10-2-14"));
        assertEquals("Enter a valid account number",
            exception.getErrors().get(0).getMessage());
    }

    @Test()
    @DisplayName("Performs checks in the Account Full Identifier (UK-100-10000047-2-1).")
    void checkAccountFullIdentifier_ShouldReturnCheckDigitsError_OneCheckDigits() {
        BusinessCheckException exception = assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("UK-100-10000047-2-1"));
        assertEquals("Enter a valid account number",
            exception.getErrors().get(0).getMessage()
        );
    }

    @Test()
    @DisplayName("Performs checks in the Account Full Identifier (UK-100-10000047- -14).")
    void checkAccountFullIdentifier_ShouldReturnPeriodError() {
        BusinessCheckException exception = assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier("UK-100-10000047- -14"));
        assertEquals("Invalid account number format – The period must be specified for UK registry accounts",
            exception.getErrors().get(0).getMessage()
        );
    }

    @Test()
    @DisplayName("Performs checks in the Account Full Identifier (UK-100-aaaaaaaa-1-14).")
    void checkAccountFullIdentifier_ShouldReturnNumericError() {
        BusinessCheckException exception = assertThrows(BusinessCheckException.class, () ->
            accountService.checkAccountFullIdentifier(" UK-100-aaaaaaaa-1-14"));
        assertEquals("Invalid account number format - The account ID must be numeric",
            exception.getErrors().get(0).getMessage()
        );
    }

    @Test
    void testRegistry1() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType("OPERATOR_HOLDING_ACCOUNT");
        accountDTO.setAccountDetails(new AccountDetailsDTO());
        accountService.openAccount(accountDTO);

        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(persistenceService, Mockito.times(1)).save(argument.capture());

        List<Object> values = argument.getAllValues();
        if (!CollectionUtils.isEmpty(values) && values.get(0) instanceof Account) {
            assertEquals("UK", ((Account) values.get(0)).getRegistryCode());
        }
    }

    @Test
    void testRegistry2() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountType("PARTY_HOLDING_ACCOUNT");
        accountDTO.setAccountDetails(new AccountDetailsDTO());
        accountService.openAccount(accountDTO);

        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(persistenceService, Mockito.times(1)).save(argument.capture());

        List<Object> values = argument.getAllValues();
        if (!CollectionUtils.isEmpty(values) && values.get(0) instanceof Account) {
            assertEquals("GB", ((Account) values.get(0)).getRegistryCode());
        }
    }

    @Test
    void testAccountHolderUpdate() {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        EmailAddressDTO emailAddressDTO = new EmailAddressDTO();
        emailAddressDTO.setEmailAddress("aha@gmail.com");
        emailAddressDTO.setEmailAddressConfirmation("aha@gmail.com");
        accountHolderDTO.setId(1000045L);
        accountHolderDTO.setEmailAddress(emailAddressDTO);
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setId(10L);

        accountHolder.setContact(createContactObject());
        when(conversionService.convert((ConversionParameters) any())).thenReturn(createContactObject());
        when(accountConversionService.convert(accountHolderDTO)).thenReturn(accountHolder);
        when(holderRepository.getAccountHolder(accountHolderDTO.getId())).thenReturn(accountHolder);
        accountService.updateHolder(accountHolderDTO);
        assertDoesNotThrow(() -> IllegalArgumentException.class);
    }


    @Test
    void testContactUpdate() {
        AccountHolderRepresentative representative = new AccountHolderRepresentative();
        AccountHolderRepresentativeDTO representativeDTO = new AccountHolderRepresentativeDTO();
        LegalRepresentativeDetailsDTO detailsDTO = new LegalRepresentativeDetailsDTO();
        detailsDTO.setFirstName("GI");
        detailsDTO.setLastName("Joe");
        detailsDTO.setAka("GI-Joe");
        representativeDTO.setDetails(detailsDTO);
        representative.setContact(createContactObject());
        representative.setId(50L);

        when(conversionService.convert((ConversionParameters) any())).thenReturn(createContactObject());
        when(legalRepresentativeRepository.getAccountHolderRepresentative(anyLong(), any())).thenReturn(representative);
        accountService.updateContact(1000045L, representativeDTO, true);
        assertDoesNotThrow(() -> IllegalArgumentException.class);
    }

    private Contact createContactObject() {
        Contact contact = new Contact();
        contact.setPhoneNumber1("12345");
        contact.setPostCode("11111");
        contact.setId(195L);
        return contact;
    }

    @Test
    @DisplayName("Check Account retrieval by full identifier")
    void test_getAccountFullIdentifier() {
        Account account = retrieveAccountInfo(AccountType.OPERATOR_HOLDING_ACCOUNT);
        given(accountRepository.findByFullIdentifier(account.getFullIdentifier())).willReturn(Optional.of(account));
        assertEquals(1234L, account.getIdentifier());
    }

    @Test
    @DisplayName("Retrieve Account Operator Information.")
    void test_getInstallationOrAircraftOperatorDTO() {
        //Prepare account
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);

        Installation installation = new Installation();
        installation.setStartYear(2021);
        installation.setInstallationName("Installation Name");
        installation.setActivityType("PRODUCTION_OF_NITRIC_ACID");
        installation.setPermitIdentifier("12345");
        installation.setId(1L);

        account.setCompliantEntity(installation);

        Mockito.when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        accountService.getInstallationOrAircraftOperatorDTO(account.getIdentifier());

        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(Long.class);
        verify(accountRepository, Mockito.times(1)).findByIdentifier(argument.capture());

        ArgumentCaptor<AccountDTO> argument2 = ArgumentCaptor.forClass(AccountDTO.class);
        ArgumentCaptor<Account> argument3 = ArgumentCaptor.forClass(Account.class);
        verify(accountDTOFactory, Mockito.times(1)).setupOperator(argument2.capture(), argument3.capture());
    }


    @Test
    @DisplayName("Check retrieval of account system events")
    void test_getAccountSystemEvents() {
    	AuthorizationTestHelper authorizationTestHelper = new AuthorizationTestHelper(authService);
        authorizationTestHelper.mockAuthAsAdmin();

        List<AuditEventDTO> systemEvents = new ArrayList<>();
        String seDescription = "system event description";
        String seCreator = "system event creator";
        Long identifier = 10001L;
        String creatorType = "user";
        AuditEventDTO systemDto = new AuditEventDTO(String.valueOf(identifier), EventType.TASK_COMMENT.name(),
            seDescription, seCreator, creatorType, new Date());
        systemEvents.add(systemDto);
        Mockito.when(eventService.getSystemEventsByDomainIdOrderByCreationDateDesc(String.valueOf(identifier),
            List.of(Account.class.getName()))).thenReturn(systemEvents);

        List<AuditEventDTO> otherEvents = new ArrayList<>();
        String description = "other event description";
        String creator = "other event creator";
        AuditEventDTO eventDto = new AuditEventDTO(String.valueOf(identifier), EventType.TASK_COMMENT.name(),
            description, creator, creatorType, new Date());
        otherEvents.add(eventDto);
        Mockito.when(eventService.getEventsByDomainIdForAdminUsers(String.valueOf(identifier),
            List.of(Account.class.getName()))).thenReturn(otherEvents);

        List<AuditEventDTO> accountHistory = accountService.getAccountHistory(10001L);
        assertEquals(2, accountHistory.size());
        assertEquals(eventDto.getDescription(), accountHistory.get(0).getDescription());
        assertEquals(systemDto.getDescription(), accountHistory.get(1).getDescription());
    }
    
    @Test
    @DisplayName("Check proposal events should not be displayed in the event history of the acquiring account")
    void test_getAcquiringAccountHistory() {
        AuthorizationTestHelper authorizationTestHelper = new AuthorizationTestHelper(authService);
        authorizationTestHelper.mockAuthAsNonAdmin();

        Long identifier = 10001L;
        
        List<AuditEventDTO> systemEvents = new ArrayList<>();
        String systemCreatorType = "system";
        AuditEventDTO systemDto = new AuditEventDTO(String.valueOf(identifier), EventType.TASK_COMMENT.name(),
        		"system event description", "system event creator", systemCreatorType, new Date());
        systemEvents.add(systemDto);
        Mockito.when(eventService.getSystemEventsByDomainIdOrderByCreationDateDesc(String.valueOf(identifier),
            List.of(Account.class.getName()))).thenReturn(systemEvents);

        List<AuditEventDTO> otherEvents = new ArrayList<>();
        String creatorType = "user";
        AuditEventDTO eventDto = new AuditEventDTO(String.valueOf(identifier), TransactionProposalTaskService.TRANSACTION_PROPOSAL_TASK,
        		"Transaction identifier UK100000. Transferring account 10000000", "user1", creatorType, new Date());
        AuditEventDTO eventDto1 = new AuditEventDTO(String.valueOf(identifier), "Change account status",
        		"Change to Suspended Partially", "user1", creatorType, new Date());
        otherEvents.addAll(List.of(eventDto, eventDto1));
        Mockito.when(eventService.getEventsByDomainIdForNonAdminUsers(String.valueOf(identifier),
            List.of(Account.class.getName()))).thenReturn(otherEvents);

        List<AuditEventDTO> accountHistory = accountService.getAccountHistory(identifier);
        assertEquals(2, accountHistory.size());
        assertEquals(eventDto1.getDescription(), accountHistory.get(0).getDescription());
        assertEquals(systemDto.getDescription(), accountHistory.get(1).getDescription());
    }    

    @Test
    @DisplayName("Should remove account's authorised representatives")
    void test_removeAccountArs() {
        String URID = "UK12345667890";
        String IAM_ID = "a1404809-742f-49f6-b14c-39149369ecea";
        Long accountId = 12345L;

        User user1 = new User();
        user1.setUrid(URID);
        user1.setIamIdentifier(IAM_ID);

        accountAccess.setUser(user1);

        when(accountAccessRepository.finARsByAccount_Identifier(any()))
            .thenReturn(List.of(accountAccess));
        String removalReason = "reason";
        accountService.removeAccountArs(accountId, removalReason, null);
        verify(authorizedRepresentativeService, times(1)).removeKeycloakRoleIfNoOtherAccountAccess(URID, IAM_ID);
        verify(accountAccess, times(1)).setState(AccountAccessState.REMOVED);
    }

    @Test
    @DisplayName("Exclude account from billing process expected to fail")
    void test_excludeFromBillingExpectedToFail() {
        Long accountIdentifier = 12345L; 
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setAccountType("ETS - Operator holding account");

        when(accountRepository.findByIdentifier(accountIdentifier)).thenReturn(Optional.of(account));
        when(accountValidationService.checkExcludedFromBillingRequest(account)).thenReturn(false);
        assertThrows(AccountActionException.class, () -> accountService.markAccountExcludedFromBilling(accountIdentifier, true, "Exclusion Remarks...."));
    }

    @Test
    @DisplayName("Exclude account from billing process expected to success")
    void test_excludeAccountFromBilling() {
        Long accountIdentifier = 12345L; 
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setAccountType("ETS - Trading account");
        account.setAccountStatus(AccountStatus.OPEN);
        
        User user = new User();
        user.setUrid("123456789");
        final String exclusionRemarks = "Exclusion Remarks....";
        
        when(accountRepository.findByIdentifier(accountIdentifier)).thenReturn(Optional.of(account));
        when(accountValidationService.checkExcludedFromBillingRequest(account)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user);
        accountService.markAccountExcludedFromBilling(accountIdentifier, true, exclusionRemarks);
        verify(accountRepository, times(1)).updateExcludedFromBilling(accountIdentifier, true, exclusionRemarks);
        verify(eventService, times(1)).createAndPublishEvent(accountIdentifier.toString(),user.getUrid(),exclusionRemarks, EventType.ACCOUNT_EXCLUSION_FROM_BILLING,
				 "Account exclusion from Billing");
    }

    @Test
    @DisplayName("Include account in billing process expected to fail")
    void test_includeInBillingExpectedToFail() {
        Long accountIdentifier = 12345L; 
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setAccountType("ETS - Operator holding account");

        when(accountRepository.findByIdentifier(accountIdentifier)).thenReturn(Optional.of(account));
        when(accountValidationService.checkExcludedFromBillingRequest(account)).thenReturn(false);
        assertThrows(AccountActionException.class, () -> accountService.markAccountExcludedFromBilling(accountIdentifier, false, null));
    }

    @Test
    @DisplayName("Include account in billing process")
    void test_includeInBilling() {
        Long accountIdentifier = 12345L; 
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setAccountType("ETS - Trading account");
        account.setAccountStatus(AccountStatus.OPEN);
        
        User user = new User();
        user.setUrid("123456789");
        
        when(accountRepository.findByIdentifier(accountIdentifier)).thenReturn(Optional.of(account));
        when(accountValidationService.checkExcludedFromBillingRequest(account)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user);
        accountService.markAccountExcludedFromBilling(accountIdentifier, false, "");
        verify(accountRepository, times(1)).updateExcludedFromBilling(accountIdentifier, false, "");
        verify(eventService, times(1)).createAndPublishEvent(accountIdentifier.toString(),user.getUrid(), "", EventType.ACCOUNT_INCLUSION_IN_BILLING,
				 "Account inclusion in Billing");
    }
}
