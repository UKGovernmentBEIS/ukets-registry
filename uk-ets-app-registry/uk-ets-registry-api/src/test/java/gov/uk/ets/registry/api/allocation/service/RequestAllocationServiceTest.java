package gov.uk.ets.registry.api.allocation.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.error.AllocationBusinessRulesException;
import gov.uk.ets.registry.api.allocation.error.AllocationError;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.data.IssuanceBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestAllocationServiceTest {


    private static final int INITIAL_ALLOCATION_YEAR = 2021;
    private static final int PAST_ALLOCATION_YEAR = INITIAL_ALLOCATION_YEAR - 1;
    public static final int FUTURE_ALLOCATION_YEAR = INITIAL_ALLOCATION_YEAR + 1;
    public static final Long TEST_ACCOUNT_IDENTIFIER = 12345L;
    private static final String TEST_URID = "123";
    public static final Long TEST_REQUEST_ID = 11223344L;

    @Mock
    private AllocationConfigurationService allocationConfigurationService;
    @Mock
    private AllocationYearCapService allocationYearCapService;
    @Mock
    private AllocationUtils allocationUtils;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserService userService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private EventService eventService;
    @Mock
    private AllocationCalculationService allocationCalculationService;
    @Mock
    private RequestAllocationExcelFileGenerator generator;
    @Mock
    private Mapper mapper;

    @InjectMocks
    private RequestAllocationService cut;

    private IssuanceBlockSummary issuanceBlockSummary;
    private IssuanceBlockSummary issuanceBlockSummary2;
    private IssuanceBlockSummary issuanceBlockSummary3;

    @Captor
    protected ArgumentCaptor<Task> saveTaskCaptor;


    @BeforeEach
    public void setUp() {
        when(allocationConfigurationService.getAllocationYear()).thenReturn(INITIAL_ALLOCATION_YEAR);
        issuanceBlockSummary = new IssuanceBlockSummary();
        issuanceBlockSummary.setYear(INITIAL_ALLOCATION_YEAR);
        issuanceBlockSummary2 = new IssuanceBlockSummary();

        issuanceBlockSummary2.setYear(PAST_ALLOCATION_YEAR);
        issuanceBlockSummary3 = new IssuanceBlockSummary();
        issuanceBlockSummary3.setYear(FUTURE_ALLOCATION_YEAR);
    }

    @Test
    public void shouldRetrieveOnlyCurrentYearAsAllocationYear() {
        when(allocationYearCapService.getCapsForCurrentPhase())
            .thenReturn(Arrays.asList(issuanceBlockSummary, issuanceBlockSummary3));

        List<Integer> years = cut.getAvailableAllocationYears();

        assertThat(years).hasSize(1);
        assertThat(years).containsExactly(INITIAL_ALLOCATION_YEAR);
    }

    @Test
    public void shouldRetrieveCurrentYearAndPastYearsAsAllocationYears() {
        when(allocationYearCapService.getCapsForCurrentPhase())
            .thenReturn(Arrays.asList(issuanceBlockSummary, issuanceBlockSummary2, issuanceBlockSummary3));

        List<Integer> years = cut.getAvailableAllocationYears();

        assertThat(years).hasSize(2);
        assertThat(years).containsExactly(INITIAL_ALLOCATION_YEAR - 1, INITIAL_ALLOCATION_YEAR);
    }

    @Test
    public void shouldSubmitForCurrentAllocationYear() {
        when(allocationYearCapService.getCapsForCurrentPhase())
            .thenReturn(Arrays.asList(issuanceBlockSummary, issuanceBlockSummary3));
        User user = new User();
        user.setUrid(TEST_URID);
        when(userService.getCurrentUser()).thenReturn(user);
        Account account = new Account();
        account.setIdentifier(TEST_ACCOUNT_IDENTIFIER);
        Optional<Account> accountOptional = Optional.of(account);
        when(taskRepository.getNextRequestId()).thenReturn(TEST_REQUEST_ID);
        AllocationOverview allocationOverview = new AllocationOverview();
        allocationOverview.setBeneficiaryRecipients(new ArrayList<>());
        AllocationSummary allocationSummary = new AllocationSummary();
        allocationSummary.setType(AllocationType.NER);
        AllocationSummary allocationSummary2 = new AllocationSummary();
        allocationSummary2.setType(AllocationType.NER);
        when(mapper.convertToJson(allocationOverview)).thenReturn("");
        when(allocationCalculationService.calculateAllocationsOverview(INITIAL_ALLOCATION_YEAR, AllocationCategory.INSTALLATION))
            .thenReturn(allocationOverview);
        RegistryAccountType registryAccountType = allocationOverview.getBeneficiaryRecipients().stream().anyMatch(f-> f.getType() != AllocationType.NER)
                ? RegistryAccountType.UK_ALLOCATION_ACCOUNT
                : RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT;

        when(accountRepository.findByRegistryAccountTypeForNonClosedAccounts(registryAccountType))
                .thenReturn(accountOptional);

        when(allocationUtils.pendingAllocationTableTaskExists(AllocationCategory.INSTALLATION)).thenReturn(false);
        when(allocationUtils.getPendingAllocationRequest(INITIAL_ALLOCATION_YEAR, AllocationCategory.INSTALLATION)).thenReturn(null);

        BusinessCheckResult result = cut.submit(INITIAL_ALLOCATION_YEAR, AllocationCategory.INSTALLATION);

        assertThat(result.getRequestIdentifier()).isEqualTo(TEST_REQUEST_ID);

        verify(eventService, times(1))
            .createAndPublishEvent(account.getIdentifier().toString(), user.getUrid(),
                String.format("Allocation Year %d Allocation Category INSTALLATION", INITIAL_ALLOCATION_YEAR),
                EventType.TASK_REQUESTED, "Allocation request submitted");

        verify(taskRepository, times(1)).save(saveTaskCaptor.capture());
        Task savedTask = saveTaskCaptor.getValue();
        assertThat(savedTask.getRequestId()).isEqualTo(TEST_REQUEST_ID);
        assertThat(savedTask.getInitiatedBy()).isEqualTo(user);
        assertThat(savedTask.getStatus()).isEqualTo(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        assertThat(savedTask.getType()).isEqualTo(RequestType.ALLOCATION_REQUEST);
    }

    @Test
    public void shouldThrowIfTaskOfSameTypeExists() {
        when(allocationYearCapService.getCapsForCurrentPhase())
            .thenReturn(Arrays.asList(issuanceBlockSummary, issuanceBlockSummary3));
        when(allocationUtils.getPendingAllocationRequest(INITIAL_ALLOCATION_YEAR, AllocationCategory.INSTALLATION))
            .thenReturn(new Task());

        AllocationBusinessRulesException exception =
            assertThrows(AllocationBusinessRulesException.class, () -> {
                cut.submit(INITIAL_ALLOCATION_YEAR, AllocationCategory.INSTALLATION);
            });

        assertThat(exception.getAllocationErrorList().get(0))
            .isEqualTo(AllocationError.PENDING_ALLOCATION_TASK_APPROVAL);
    }

    @Test
    public void shouldThrowIfInvalidAllocationYear() {
        when(allocationYearCapService.getCapsForCurrentPhase())
            .thenReturn(Arrays.asList(issuanceBlockSummary, issuanceBlockSummary3));

        AllocationBusinessRulesException exception =
            assertThrows(AllocationBusinessRulesException.class, () -> {
                cut.submit(FUTURE_ALLOCATION_YEAR, AllocationCategory.INSTALLATION);
            });

        assertThat(exception.getAllocationErrorList().get(0)).isEqualTo(AllocationError.INVALID_ALLOCATION_YEAR);
    }

    @Test
    public void shouldThrowIfPendingTransactions() {
        when(allocationYearCapService.getCapsForCurrentPhase())
            .thenReturn(Arrays.asList(issuanceBlockSummary, issuanceBlockSummary3));
        when(allocationUtils.hasPendingAllocationJobOrTransactions(AllocationCategory.INSTALLATION, INITIAL_ALLOCATION_YEAR)).thenReturn(true);

        AllocationBusinessRulesException exception =
            assertThrows(AllocationBusinessRulesException.class, () -> {
                cut.submit(INITIAL_ALLOCATION_YEAR, AllocationCategory.INSTALLATION);
            });

        assertThat(exception.getAllocationErrorList().get(0))
            .isEqualTo(AllocationError.PENDING_ALLOCATION_TRANSACTIONS);
    }
}
