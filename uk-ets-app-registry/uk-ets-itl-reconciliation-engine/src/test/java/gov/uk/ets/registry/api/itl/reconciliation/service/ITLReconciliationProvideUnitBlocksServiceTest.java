package gov.uk.ets.registry.api.itl.reconciliation.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLProvideUnitBlocksQueryDslRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationPhase;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import uk.gov.ets.lib.commons.kyoto.types.ProvideUnitBlocksRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveUnitBlocksRequest;
import uk.gov.ets.lib.commons.kyoto.types.UnitBlock;
import uk.gov.ets.lib.commons.kyoto.types.UnitBlockRequest;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ITLReconciliationProvideUnitBlocksServiceTest {

    private static final long TEST_START_BLOCK_1 = 1;
    private static final long TEST_END_BLOCK_1 = 10;
    private static final UnitType TEST_UNIT_TYPE_1 = UnitType.AAU;
    private static final AccountType TEST_ACCOUNT_TYPE_1 = AccountType.PARTY_HOLDING_ACCOUNT;
    private static final String TEST_RECON_ID_1 = "test-recon-1";
    private static final CommitmentPeriod TEST_COMMITMENT_PERIOD_1 = CommitmentPeriod.CP1;
    @Mock
    private ITLProvideUnitBlocksQueryDslRepository provideUnitBlocksRepository;
    @Mock
    private ITLReconciliationLogRepository reconciliationLogRepository;

    @InjectMocks
    ITLReconciliationProvideUnitBlocksService cut;

    ProvideUnitBlocksRequest provideUnitBlocksRequest;

    @BeforeEach
    public void setUp() {

        provideUnitBlocksRequest = new ProvideUnitBlocksRequest();
        provideUnitBlocksRequest.setReconciliationIdentifier(TEST_RECON_ID_1);
    }

    @Test
    public void shouldConvertDtoToITlModel() {

        ITLReconciliationLog reconciliationLog = new ITLReconciliationLog();
        reconciliationLog.setReconId(TEST_RECON_ID_1);
        reconciliationLog.setReconPhaseCode(ITLReconciliationPhase.TOTALS);
        given(reconciliationLogRepository.findById(TEST_RECON_ID_1)).willReturn(Optional.of(reconciliationLog));
        UnitBlockDto unitBlockDto1 = UnitBlockDto.builder()
            .startBlock(TEST_START_BLOCK_1)
            .endBlock(TEST_END_BLOCK_1)
            .unitType(TEST_UNIT_TYPE_1)
            .accountType(TEST_ACCOUNT_TYPE_1.getKyotoType())
            .applicablePeriod(TEST_COMMITMENT_PERIOD_1)
            .build();
        given(provideUnitBlocksRepository.generateUnitBlocks(TEST_RECON_ID_1)).willReturn(List.of(unitBlockDto1));
        ReceiveUnitBlocksRequest receiveUnitBlocksRequest = cut.provideUnitBlocks(provideUnitBlocksRequest);

        assertThat(receiveUnitBlocksRequest.getUnitBlocks()).hasSize(1);
        UnitBlock unitBlock1 = receiveUnitBlocksRequest.getUnitBlocks()[0];
        assertThat(unitBlock1.getUnitSerialBlockStart()).isEqualTo(TEST_START_BLOCK_1);
        assertThat(unitBlock1.getUnitSerialBlockEnd()).isEqualTo(TEST_END_BLOCK_1);
        assertThat(unitBlock1.getUnitType()).isEqualTo(TEST_UNIT_TYPE_1.getPrimaryCode());
        assertThat(unitBlock1.getAccountType()).isEqualTo(TEST_ACCOUNT_TYPE_1.getKyotoType().getCode());
    }

    @Test
    public void shouldUpdateLogWhenRequestAfterCalculateTotals() {
        ITLReconciliationLog reconciliationLog = new ITLReconciliationLog();
        reconciliationLog.setReconId(TEST_RECON_ID_1);
        reconciliationLog.setReconPhaseCode(ITLReconciliationPhase.TOTALS);
        given(reconciliationLogRepository.findById(TEST_RECON_ID_1)).willReturn(Optional.of(reconciliationLog));

        cut.provideUnitBlocks(provideUnitBlocksRequest);

        assertThat(reconciliationLog.getReconPhaseCode()).isEqualTo(ITLReconciliationPhase.UNIT_BLOCK_DETAIL);
        ITLReconciliationStatusHistory historyEntry = reconciliationLog.getReconciliationStatusHistories().get(0);
        assertThat(historyEntry.getReconStatus()).isEqualTo(ITLReconciliationStatus.ITL_TOTAL_INCON);
    }

    @Test
    public void shouldUpdateLogWhenInitialRequest() {

        ITLReconciliationLog reconciliationLog = new ITLReconciliationLog();
        reconciliationLog.setReconId(TEST_RECON_ID_1);
        given(reconciliationLogRepository.findById(TEST_RECON_ID_1)).willReturn(Optional.of(reconciliationLog));

        cut.provideUnitBlocks(provideUnitBlocksRequest);

        assertThat(reconciliationLog.getReconPhaseCode()).isEqualTo(ITLReconciliationPhase.UNIT_BLOCK_DETAIL);
        ITLReconciliationStatusHistory historyEntry = reconciliationLog.getReconciliationStatusHistories().get(0);
        assertThat(historyEntry.getReconStatus()).isEqualTo(ITLReconciliationStatus.INITIATED);
    }

    @Test
    public void shouldProvideSpecificUnitBlocks() {
        ITLReconciliationLog reconciliationLog = new ITLReconciliationLog();
        reconciliationLog.setReconId(TEST_RECON_ID_1);
        given(reconciliationLogRepository.findById(TEST_RECON_ID_1)).willReturn(Optional.of(reconciliationLog));

        UnitBlockRequest unitBlockRequest = new UnitBlockRequest();
        unitBlockRequest.setUnitType(TEST_UNIT_TYPE_1.getPrimaryCode());
        unitBlockRequest.setAccountType(TEST_ACCOUNT_TYPE_1.getKyotoType().getCode());
        unitBlockRequest.setAccountCommitPeriod(TEST_COMMITMENT_PERIOD_1.getCode());
        provideUnitBlocksRequest.setUnitBlockRequests(new UnitBlockRequest[] {unitBlockRequest});

        cut.provideUnitBlocks(provideUnitBlocksRequest);

        verify(provideUnitBlocksRepository, times(1))
            .generateUnitBlocks(TEST_RECON_ID_1, TEST_ACCOUNT_TYPE_1.getKyotoType(), TEST_UNIT_TYPE_1,
                TEST_COMMITMENT_PERIOD_1);
    }
}
