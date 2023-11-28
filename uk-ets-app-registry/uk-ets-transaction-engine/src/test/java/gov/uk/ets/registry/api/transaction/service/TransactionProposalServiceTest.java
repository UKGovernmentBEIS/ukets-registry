package gov.uk.ets.registry.api.transaction.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.allocation.service.AllocationYearCapService;
import gov.uk.ets.registry.api.itl.notice.ITLNoticeService;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.ProposedTransactionType;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.AccountHoldingRepository;
import gov.uk.ets.registry.api.transaction.repository.AcquiringAccountRepository;
import gov.uk.ets.registry.api.transaction.repository.ProjectRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class TransactionProposalServiceTest {

    private TransactionProposalService transactionProposalService;

    @Mock
    private TransactionPersistenceService transactionPersistenceService;

    @Mock
    private AccountHoldingRepository accountHoldingRepository;

    @Mock
    private AcquiringAccountRepository acquiringAccountRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TransactionAccountService transactionAccountService;

    @Mock
    private AllocationYearCapService allocationYearCapService;

    private ProjectService projectService;

    @Mock
    private ITLNoticeService itlNoticeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectService = new ProjectService(projectRepository);
        transactionProposalService =
            new TransactionProposalService(transactionPersistenceService, accountHoldingRepository,
                acquiringAccountRepository, projectService, transactionAccountService, allocationYearCapService,
                itlNoticeService);
    }

    @DisplayName("Test the available transaction types")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {1} - {2}")
    void testTransactionTypesPartyHolding(TransactionType transactionType, boolean isSeniorAdmin, boolean result) {
        Mockito.when(transactionPersistenceService.getAccount(1L)).
            thenReturn(new AccountSummary(1L, RegistryAccountType.NONE, KyotoAccountType.PARTY_HOLDING_ACCOUNT,
                AccountStatus.OPEN, null, null, null));

        Mockito.when(accountHoldingRepository.getHoldingsOverviewForProposal(1L)).thenReturn(new ArrayList<>() {{
            add(new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2, null, 10L, null,
                    transactionType.isSubjectToSOP(), null));
        }});

        List<ProposedTransactionType> proposedTypes = transactionProposalService.getAvailableTransactionTypes(1L, isSeniorAdmin);

        ProposedTransactionType transfer = new ProposedTransactionType();
        transfer.setType(transactionType);
        transfer.setDescription(transfer.getType().getDescription());

        assertEquals(result, proposedTypes.contains(transfer));

    }

    static Stream<Arguments> getArguments() {
        return  Stream.of(
                Arguments.of(TransactionType.InternalTransfer, true, TransactionType.InternalTransfer.isAccessibleToAR()),
                Arguments.of(TransactionType.IssueOfAAUsAndRMUs, true, TransactionType.IssueOfAAUsAndRMUs.isAccessibleToAR()),
                Arguments.of(TransactionType.Art37Cancellation, false, TransactionType.Art37Cancellation.isAccessibleToAR()),
                Arguments.of(TransactionType.CancellationKyotoUnits, true, TransactionType.CancellationKyotoUnits.isAccessibleToAR()),
                Arguments.of(TransactionType.AmbitionIncreaseCancellation, false, TransactionType.AmbitionIncreaseCancellation.isAccessibleToAR()),
                Arguments.of(TransactionType.Retirement, false, TransactionType.Retirement.isAccessibleToAR()),
                Arguments.of(TransactionType.TransferToSOPforFirstExtTransferAAU, false, TransactionType.TransferToSOPforFirstExtTransferAAU.isAccessibleToAR()));
    }

    @Test
    void testAvailableUnitsPartyHolding() {
        Mockito.when(transactionPersistenceService.getAccount(2L)).
            thenReturn(new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PARTY_HOLDING_ACCOUNT,
                AccountStatus.OPEN, null, null, null));

        Mockito
            .when(accountHoldingRepository.getHoldingsForTransaction(2L, TransactionType.InternalTransfer.getUnits()))
            .thenReturn(new ArrayList<>() {{
                add(new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, null, 10L,
                    null, true, null));
                add(new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2, null, 10L,
                    null, true, null));
                add(new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2, null, 10L,
                        null, false, null));
                add(new TransactionBlockSummary(UnitType.ERU_FROM_AAU, CommitmentPeriod.CP1, CommitmentPeriod.CP1,
                    EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION, 10L, null, true, null));
                add(new TransactionBlockSummary(UnitType.ERU_FROM_AAU, CommitmentPeriod.CP1, CommitmentPeriod.CP1,
                    EnvironmentalActivity.GRAZING_LAND_MANAGEMENT, 10L, null, true, null));
            }});

        assertEquals(3, transactionProposalService.getAvailableUnits(2L, TransactionType.InternalTransfer).size());

        Mockito.when(accountHoldingRepository
            .getHoldingsForTransaction(2L, TransactionType.TransferToSOPforFirstExtTransferAAU.getUnits(),
                CommitmentPeriod.CP2, TransactionType.TransferToSOPforFirstExtTransferAAU.isSubjectToSOP()))
                .thenReturn(new ArrayList<>() {{
            add(new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2, null, 10L, null,
                true, null));
        }});
        assertEquals(1,
                                transactionProposalService.getAvailableUnits(2L, TransactionType.TransferToSOPforFirstExtTransferAAU)
                .size());
    }

    @Test
    void testProjectService() {

        //uniBlock("GB", 1L, UnitType.AAU, CommitmentPeriod.CP2, 100L, 109L, null, null));
        //uniBlock("GB", 1L, UnitType.AAU, CommitmentPeriod.CP2, 10000L, 10009L, null, null));
        //uniBlock("JP", 1L, UnitType.CER, CommitmentPeriod.CP1, 12L, 14L, "JP123", null));
        //uniBlock("KR", 1L, UnitType.CER, CommitmentPeriod.CP2, 120L, 121L, "KR555", null));
        //uniBlock("JP", 1L, UnitType.CER, CommitmentPeriod.CP1, 16L, 18L, "JP123", null));
        //uniBlock("BO", 1L, UnitType.CER, CommitmentPeriod.CP1, 16000L, 16008L, "BO111", null));
        //uniBlock("KR", 1L, UnitType.CER, CommitmentPeriod.CP2, 128L, 129L, "KR555", null));
        //uniBlock("FR", 1L, UnitType.RMU, CommitmentPeriod.CP2, 1L, 2L, null, EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION));
        //uniBlock("FR", 1L, UnitType.RMU, CommitmentPeriod.CP2, 4L, 5L, null, EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION));
        //uniBlock("FR", 1L, UnitType.RMU, CommitmentPeriod.CP2, 1324234L, 1324235L, null, EnvironmentalActivity.GRAZING_LAND_MANAGEMENT));
        //uniBlock("FR", 1L, UnitType.RMU, CommitmentPeriod.CP1, 777L, 780L, null, EnvironmentalActivity.DEFORESTATION));

        Mockito.when(projectRepository.getProjects(1L, UnitType.CER, CommitmentPeriod.CP1))
            .thenReturn(new ArrayList<>() {{
                add("JP123");
                add("BO111");
            }});
        final List<String> strings = projectService.retrieveProjects(1L, UnitType.CER, CommitmentPeriod.CP1);
        assertEquals(2, strings.size());

        Mockito.when(projectRepository.getProjects(1L, UnitType.CER, CommitmentPeriod.CP2))
            .thenReturn(new ArrayList<>() {{
                add("KR555");
            }});
        assertEquals(1, projectService.retrieveProjects(1L, UnitType.CER, CommitmentPeriod.CP2).size());

        Mockito.when(projectRepository.getProjects(1L, UnitType.CER)).thenReturn(new ArrayList<>() {{
            add("JP123");
            add("BO111");
            add("KR555");
        }});
        assertEquals(3, projectService.retrieveProjects(1L, UnitType.CER, null).size());


        Mockito.when(projectRepository.getEnvironmentalActivities(1L, UnitType.RMU)).thenReturn(new ArrayList<>() {{
            add(EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION);
            add(EnvironmentalActivity.GRAZING_LAND_MANAGEMENT);
            add(EnvironmentalActivity.DEFORESTATION);
        }});
        assertEquals(3, projectService.retrieveEnvironmentalActivities(1L, UnitType.RMU, null).size());

        Mockito.when(projectRepository.getEnvironmentalActivities(1L, UnitType.RMU, CommitmentPeriod.CP2))
            .thenReturn(new ArrayList<>() {{
                add(EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION);
                add(EnvironmentalActivity.GRAZING_LAND_MANAGEMENT);
            }});
        assertEquals(2,
                                projectService.retrieveEnvironmentalActivities(1L, UnitType.RMU, CommitmentPeriod.CP2).size());

        Mockito.when(projectRepository.getEnvironmentalActivities(1L, UnitType.RMU, CommitmentPeriod.CP1))
            .thenReturn(new ArrayList<>() {{
                add(EnvironmentalActivity.DEFORESTATION);
            }});
        assertEquals(1,
                                projectService.retrieveEnvironmentalActivities(1L, UnitType.RMU, CommitmentPeriod.CP1).size());

    }

    @Test
    void parseProjectSuccessfully() {
        assertEquals(12345, (int) projectService.extractProjectIdentifier("KR12345"));
        assertEquals("JP", projectService.extractProjectParty("JP12345"));
    }

    @Test
    void parseProjectEmpty() {
        assertNull(projectService.extractProjectParty("JP"));
        assertNull(projectService.extractProjectParty(""));
        assertNull(projectService.extractProjectParty("  "));
        assertNull(projectService.extractProjectParty(null));
        assertNull(projectService.extractProjectIdentifier("JP"));
        assertNull(projectService.extractProjectIdentifier(""));
        assertNull(projectService.extractProjectIdentifier("  "));
        assertNull(projectService.extractProjectIdentifier(null));
    }

    @Test
    void parseProjectFailure1() {
        assertThrows(IllegalArgumentException.class, () -> projectService.extractProjectIdentifier("KRZZZ"));
    }

    @Test
    void parseProjectFailure2() {
        assertThrows(IllegalArgumentException.class, () -> projectService.extractProjectParty("KRZZZ"));
    }

    @Test
    void getUserDefinedAcquiringAccount() {

        Mockito.when(acquiringAccountRepository.retrieveUserDefinedAccount("JP-100-12345-0")).thenReturn(null);

        AccountInfo result = transactionProposalService.getUserDefinedAcquiringAccount(100L, "JP-100-12345-0",
                null, null, null, null);
        assertNull(result.getAccountHolderName());
        assertNull(result.getAccountName());
        assertNull(result.getIdentifier());
        assertNotNull(result.getFullIdentifier());

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("GB-131-12345-2-44", RegistryAccountType.NONE, AccountStatus.OPEN)
        );
        result = transactionProposalService.getUserDefinedAcquiringAccount(100L, null,
            TransactionType.CarryOver_AAU, null, null, null);
        assertNotNull(result);

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse("GB-230-1020-2-82", RegistryAccountType.NONE, AccountStatus.OPEN)
        );
        result = transactionProposalService.getUserDefinedAcquiringAccount(1003L, "GB-230-1020-2-82",
                TransactionType.CancellationKyotoUnits, "CP2", null, null);
        assertNotNull(result);

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse("GB-250-1024-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );
        result = transactionProposalService.getUserDefinedAcquiringAccount(1003L, "GB-250-1024-2-76",
                TransactionType.MandatoryCancellation, "CP2", null, null);
        assertNotNull(result);

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse("GB-250-1024-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );
        result = transactionProposalService.getUserDefinedAcquiringAccount(1024L, "GB-250-1024-2-76",
                TransactionType.ExpiryDateChange, "CP2", null, null);
        assertNotNull(result);
    }

}
