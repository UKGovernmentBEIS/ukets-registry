package gov.uk.ets.registry.api.transaction.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.itl.notice.ITLNoticeService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class UnitReservationServiceTest {

    @Mock
    private TransactionAccountService transactionAccountService;

    @Mock
    private TransactionPersistenceService transactionPersistenceService;

    @Mock
    private UnitSplitService unitSplitService;

    @Mock
    private UnitBlockRepository unitBlockRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private ITLNoticeService itlNoticeService;

    private UnitReservationService unitReservationService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        unitReservationService = new UnitReservationService(transactionAccountService, transactionPersistenceService,
            unitSplitService, unitBlockRepository, projectService, itlNoticeService);
    }

    @Test
    void testMaximumNumberOfBlocksImposedByITL() {
        List<TransactionBlockSummary> blocks = new ArrayList<>() {{
            add(TransactionBlockSummary.builder()
                .type(UnitType.AAU)
                .originalPeriod(CommitmentPeriod.CP2)
                .applicablePeriod(CommitmentPeriod.CP2)
                .quantity("30001")
                .subjectToSop(true)
                .build());
        }};

        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .identifier("GB12345")
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("JP-100-12345-0")
            .blocks(blocks)
            .build();

        List<UnitBlock> holdings = new ArrayList<>();
        long serialNumber = 0;
        for (int index = 0; index < 40000; index++) {
            UnitBlock unitBlock = new UnitBlock();
            unitBlock.setType(UnitType.AAU);
            unitBlock.setOriginalPeriod(CommitmentPeriod.CP2);
            unitBlock.setApplicablePeriod(CommitmentPeriod.CP2);
            unitBlock.setStartBlock(serialNumber++);
            unitBlock.setEndBlock(serialNumber++);
            holdings.add(unitBlock);
        }

        Mockito.<List<UnitBlock>>when(transactionPersistenceService.find(any(), any(), any())).thenReturn(holdings);

        BusinessCheckException exception = assertThrows(BusinessCheckException.class, () ->
            unitReservationService.reserveUnits(transaction));

        assertEquals(3004, exception.getBusinessCheckErrorResult().getErrors().get(0).getCode());

    }

    @Test
    void testNoReservationDueToExternalRegistry() {
        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .transferringRegistryCode("JP")
            .build();

        unitReservationService.reserveUnits(transaction);

        verify(transactionAccountService, never()).lockAccount(any());
    }

    @Test
    void testReservationFailsDueToException() {
        List<TransactionBlockSummary> blocks = new ArrayList<>() {{
            add(TransactionBlockSummary.builder()
                .type(UnitType.AAU)
                .originalPeriod(CommitmentPeriod.CP2)
                .applicablePeriod(CommitmentPeriod.CP2)
                .quantity("1")
                .build());
        }};

        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .identifier("GB12345")
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("JP-100-12345-0")
            .blocks(blocks)
            .build();

        Mockito.<List<UnitBlock>>when(transactionPersistenceService.find(any(), any(), any())).thenThrow(new IllegalArgumentException("The query string is found to be invalid"));

        assertThrows(TransactionExecutionException.class, () ->
            unitReservationService.reserveUnits(transaction));

    }

    @Test
    void testNoUnitsFound() {
        List<TransactionBlockSummary> blocks = new ArrayList<>() {{
            add(TransactionBlockSummary.builder()
                .type(UnitType.AAU)
                .originalPeriod(CommitmentPeriod.CP2)
                .applicablePeriod(CommitmentPeriod.CP2)
                .quantity("30001")
                .build());
        }};

        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .identifier("GB12345")
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("JP-100-12345-0")
            .blocks(blocks)
            .build();

        Mockito.<List<UnitBlock>>when(transactionPersistenceService.find(any(), any(), any())).thenReturn(new ArrayList<>());

        BusinessCheckException exception = assertThrows(BusinessCheckException.class, () ->
            unitReservationService.reserveUnits(transaction));

        assertEquals(3005, exception.getBusinessCheckErrorResult().getErrors().get(0).getCode());

    }

    @Test
    void testSuccessfulReservation() {
        List<TransactionBlockSummary> blocks = new ArrayList<>() {{
            add(TransactionBlockSummary.builder()
                .type(UnitType.AAU)
                .originalPeriod(CommitmentPeriod.CP2)
                .applicablePeriod(CommitmentPeriod.CP2)
                .quantity("1")
                .subjectToSop(true)
                .build());

            add(TransactionBlockSummary.builder()
                .type(UnitType.RMU)
                .originalPeriod(CommitmentPeriod.CP1)
                .applicablePeriod(CommitmentPeriod.CP1)
                .quantity("1")
                .environmentalActivity(EnvironmentalActivity.DEFORESTATION)
                .build());

            add(TransactionBlockSummary.builder()
                .type(UnitType.CER)
                .originalPeriod(CommitmentPeriod.CP1)
                .applicablePeriod(CommitmentPeriod.CP1)
                .quantity("1")
                .projectNumber("ID1234")
                .build());

        }};

        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .identifier("GB12345")
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("JP-100-12345-0")
            .blocks(blocks)
            .build();

        List<UnitBlock> holdings = new ArrayList<>();
        UnitBlock unitBlock = new UnitBlock();
        unitBlock.setType(UnitType.AAU);
        unitBlock.setOriginalPeriod(CommitmentPeriod.CP2);
        unitBlock.setApplicablePeriod(CommitmentPeriod.CP2);
        unitBlock.setStartBlock(100L);
        unitBlock.setEndBlock(100L);
        unitBlock.setSubjectToSop(true);
        holdings.add(unitBlock);

        unitBlock = new UnitBlock();
        unitBlock.setType(UnitType.RMU);
        unitBlock.setOriginalPeriod(CommitmentPeriod.CP1);
        unitBlock.setApplicablePeriod(CommitmentPeriod.CP1);
        unitBlock.setStartBlock(100L);
        unitBlock.setEndBlock(110L);
        unitBlock.setEnvironmentalActivity(EnvironmentalActivity.DEFORESTATION);
        holdings.add(unitBlock);

        unitBlock = new UnitBlock();
        unitBlock.setType(UnitType.CER);
        unitBlock.setOriginalPeriod(CommitmentPeriod.CP1);
        unitBlock.setApplicablePeriod(CommitmentPeriod.CP1);
        unitBlock.setStartBlock(100L);
        unitBlock.setEndBlock(101L);
        unitBlock.setProjectNumber("ID1234");
        holdings.add(unitBlock);

        Mockito.<List<UnitBlock>>when(transactionPersistenceService.find(any(), any(), any())).thenReturn(holdings);

        Mockito.when(projectService.extractProjectParty(any())).thenReturn("ID");

        assertDoesNotThrow(() -> unitReservationService.reserveUnits(transaction));

    }


}
