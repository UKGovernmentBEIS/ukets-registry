package gov.uk.ets.registry.api.allocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.service.dto.AccountAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.AggregatedAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.AnnualAllocationDTO;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.allocationtable.services.AllocationTableService;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAccountAllocationBDDTest {

    @Mock
    protected AccountRepository accountRepository;

    @Mock
    protected CompliantEntityRepository compliantEntityRepository;

    @Mock
    protected AllocationStatusService allocationStatusService;

    @Mock
    private AllocationDTOFactory dtoFactory;

    @Mock
    protected CompliantEntity compliantEntity;

    @Mock
    protected UserService userService;

    @Mock
    protected EventService eventService;

    @Mock
    protected AllocationConfigurationService allocationConfigurationService;

    @Mock
    protected TransactionRepository transactionRepository;

    @Mock
    protected AllocationTableService allocationTableService;
    
    @Mock
    protected RequestAllocationService requestAllocationService;
    
    protected @Mock
    Account account;

    protected AccountAllocationService serviceUnderTest;

    @BeforeEach
    void setUp() {
        serviceUnderTest = new AccountAllocationService(
            accountRepository,
            compliantEntityRepository,
            allocationStatusService,
            dtoFactory, eventService, userService, allocationConfigurationService,transactionRepository,
            allocationTableService,requestAllocationService);
    }

    @Test
    @DisplayName("Given account of registry type of AIRCRAFT_OPERATOR_HOLDING_ACCOUNT getAccountAllocation should return NAVAT account allocation")
    void getNavatAccountAllocation() {
        // given
        Long accountId = 1234L;
        Long compliantEntityId = 999L;
        RegistryAccountType accountType = RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT;
        TestUtils.MockAllocationSummaryCommand mockNatOrNavatAllocationSummaryCommand = TestUtils.MockAllocationSummaryCommand.builder()
            .year(2023)
            .entitlement(100L)
            .allocated(60L)
            .status(AllocationStatusType.ALLOWED).build();
       mockAccount(MockAccountCommand.builder()
            .compliantEntityId(compliantEntityId)
            .identifier(accountId)
            .registryAccountType(accountType)
            .build());
        AggregatedAllocationDTO expectedStandardAllocation = getAggregatedAllocationDTO(mockNatOrNavatAllocationSummaryCommand);
        AccountAllocationDTO expectedResult = AccountAllocationDTO.builder()
            .underNewEntrantsReserve(null)
            .standard(expectedStandardAllocation)
            .build();

        given(accountRepository.findByIdentifier(accountId)).willReturn(Optional.of(account));
        List<AllocationSummary> expectedNatAllocationSummaries = TestUtils.mockAllocationSummaryList(List.of(mockNatOrNavatAllocationSummaryCommand));
        given(allocationStatusService.getAllocationEntries(compliantEntityId, AllocationType.NAVAT)).willReturn(expectedNatAllocationSummaries);
        given(dtoFactory.createAggregatedAllocationDTO(expectedNatAllocationSummaries, new ArrayList<>(), false)).willReturn(expectedStandardAllocation);

        // when
        AccountAllocationDTO dto = serviceUnderTest.getAccountAllocation(accountId);

        // then
        then(accountRepository).should(times(1)).findByIdentifier(accountId);
        then(allocationStatusService).should(times(1)).getAllocationEntries(any(), any());
        then(allocationStatusService).should(times(1)).getAllocationEntries(compliantEntityId, AllocationType.NAVAT);
        assertEquals(expectedResult, dto);
    }

    @Test
    @DisplayName("Given account of registry type of OPERATOR_HOLDING_ACCOUNT getAccountAllocation should return NAT and NER account allocations")
    void getNatAndNerAccountAllocation() {
        // given
        Long accountId = 1234L;
        Long compliantEntityId = 999L;
        RegistryAccountType accountType = RegistryAccountType.OPERATOR_HOLDING_ACCOUNT;
        TestUtils.MockAllocationSummaryCommand mockNatOrNavatAllocationSummaryCommand = TestUtils.MockAllocationSummaryCommand.builder()
            .year(2023)
            .entitlement(100L)
            .allocated(60L)
            .status(AllocationStatusType.ALLOWED).build();
        mockAccount(MockAccountCommand.builder()
            .compliantEntityId(compliantEntityId)
            .identifier(accountId)
            .registryAccountType(accountType)
            .build());
        AggregatedAllocationDTO expectedStandardAllocation = getAggregatedAllocationDTO(mockNatOrNavatAllocationSummaryCommand);
        given(accountRepository.findByIdentifier(accountId)).willReturn(Optional.of(account));
        List<AllocationSummary> expectedNatAllocationSummaries = TestUtils.mockAllocationSummaryList(List.of(mockNatOrNavatAllocationSummaryCommand));
        given(allocationStatusService.getAllocationEntries(compliantEntityId, AllocationType.NAT)).willReturn(expectedNatAllocationSummaries);
        given(dtoFactory.createAggregatedAllocationDTO(expectedNatAllocationSummaries, new ArrayList<>(), false)).willReturn(expectedStandardAllocation);
        TestUtils.MockAllocationSummaryCommand mockedNerAllocationSummaryCommand = TestUtils.MockAllocationSummaryCommand.builder()
            .year(2022)
            .entitlement(300L)
            .allocated(178L)
            .status(AllocationStatusType.WITHHELD).build();
        List<AllocationSummary> expectedNerAllocationSummaries = TestUtils.mockAllocationSummaryList(List.of(mockedNerAllocationSummaryCommand));
        given(allocationStatusService.getAllocationEntries(compliantEntityId, AllocationType.NER)).willReturn(expectedNerAllocationSummaries);
        AggregatedAllocationDTO expectedNerAllocation = getAggregatedAllocationDTO(mockedNerAllocationSummaryCommand);
        given(dtoFactory.createAggregatedAllocationDTO(expectedNerAllocationSummaries, new ArrayList<>(), true)).willReturn(expectedNerAllocation);
        AccountAllocationDTO expectedResult = AccountAllocationDTO.builder()
            .underNewEntrantsReserve(expectedNerAllocation)
            .standard(expectedStandardAllocation)
            .build();
        // when
        AccountAllocationDTO dto = serviceUnderTest.getAccountAllocation(accountId);

        // then
        then(accountRepository).should(times(1)).findByIdentifier(accountId);
        then(allocationStatusService).should(times(2)).getAllocationEntries(any(), any());
        then(allocationStatusService).should(times(1)).getAllocationEntries(compliantEntityId, AllocationType.NAT);
        then(allocationStatusService).should(times(1)).getAllocationEntries(compliantEntityId, AllocationType.NER);
        assertEquals(expectedResult, dto);
    }

    @Test
    public void shouldThrowRuntimeException() {
        // given
        Long accountId = 1234L;
        given(accountRepository.findByIdentifier(accountId)).willReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> serviceUnderTest.getAccountAllocation(accountId));
    }

    @ParameterizedTest
    @MethodSource("typesWithoutAllocation")
    public void shouldReturnNull(RegistryAccountType accountType) {
        Long accountId = 1234L;
        given(account.getRegistryAccountType()).willReturn(accountType);
        given(account.getCompliantEntity()).willReturn(compliantEntity);

        given(accountRepository.findByIdentifier(accountId)).willReturn(Optional.of(account));

        //when
        AccountAllocationDTO dto = serviceUnderTest.getAccountAllocation(accountId);

        //then
        assertNull(dto);
    }

    private static Stream<RegistryAccountType> typesWithoutAllocation() {
        return Stream.of(RegistryAccountType.values()).filter(
            type -> type != RegistryAccountType.OPERATOR_HOLDING_ACCOUNT
                && type != RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
    }


    private AggregatedAllocationDTO getAggregatedAllocationDTO(
        TestUtils.MockAllocationSummaryCommand mockAllocationSummaryCommand) {
        return AggregatedAllocationDTO.builder()
            .annuals(List.of(AnnualAllocationDTO.builder()
                .year(mockAllocationSummaryCommand.getYear())
                .entitlement(mockAllocationSummaryCommand.getEntitlement())
                .allocated(mockAllocationSummaryCommand.getAllocated())
                .remaining(mockAllocationSummaryCommand.getRemaining())
                .status(mockAllocationSummaryCommand.getStatus().name())
                .build()))
            .build();
    }

    protected void mockAccount(MockAccountCommand command) {
        given(account.getRegistryAccountType()).willReturn(command.registryAccountType);
        given(account.getCompliantEntity()).willReturn(compliantEntity);
        given(compliantEntity.getId()).willReturn(command.compliantEntityId);
    }

    @Builder
    protected static class MockAccountCommand {
        private final Long identifier;
        private final RegistryAccountType registryAccountType;
        private final Long compliantEntityId;
    }
}