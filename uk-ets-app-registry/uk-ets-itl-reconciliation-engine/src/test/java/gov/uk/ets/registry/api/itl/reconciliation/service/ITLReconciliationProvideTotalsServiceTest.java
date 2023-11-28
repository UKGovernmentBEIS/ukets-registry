package gov.uk.ets.registry.api.itl.reconciliation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLSnapshotLog;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLCalculateTotalsRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLSnapshotLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationPhase;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import uk.gov.ets.lib.commons.kyoto.types.ProvideTotalsRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveTotalsRequest;
import uk.gov.ets.lib.commons.kyoto.types.Total;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ITLReconciliationProvideTotalsServiceTest {

    private static final KyotoAccountType TEST_ACCOUNT_TYPE_1 = KyotoAccountType.PARTY_HOLDING_ACCOUNT;
    private static final CommitmentPeriod TEST_ACCOUNT_COMMITMENT_PERIOD = CommitmentPeriod.CP1;
    private static final UnitType TEST_UNIT_TYPE_1 = UnitType.AAU;
    private static final long TEST_UNIT_COUNT_1 = 1000;
    private static final String TEST_UK_REGISTRY = "UK";
    private static final String TEST_RECONCILIATION_ID_1 = "12345";

    @Mock
    ITLCalculateTotalsRepository calculateTotalsRepository;

    @Mock
    ITLReconciliationLogRepository reconciliationLogRepository;

    @Mock
    ITLSnapshotLogRepository snapshotLogRepository;

    @InjectMocks
    ITLReconciliationProvideTotalsService cut;

    TotalDto totalDto1;
    ProvideTotalsRequest request;
    ITLReconciliationLog reconciliationLog;

    @BeforeEach
    public void setUp() {

        totalDto1 = TotalDto.builder()
            .accountType(TEST_ACCOUNT_TYPE_1)
            .accountCommitPeriod(TEST_ACCOUNT_COMMITMENT_PERIOD)
            .unitType(TEST_UNIT_TYPE_1)
            .unitCount(TEST_UNIT_COUNT_1)
            .build();
        request = new ProvideTotalsRequest();
        request.setReconciliationIdentifier(TEST_RECONCILIATION_ID_1);
        request.setTo(TEST_UK_REGISTRY);

        reconciliationLog = new ITLReconciliationLog();

        given(reconciliationLogRepository.findById(TEST_RECONCILIATION_ID_1))
            .willReturn(Optional.of(reconciliationLog));

        given(snapshotLogRepository.findByReconciliationLog_ReconId(TEST_RECONCILIATION_ID_1))
            .willReturn(Optional.of(new ITLSnapshotLog()));
    }

    @Test
    public void shouldConvertDtoToITLByAccountModel() {

        given(calculateTotalsRepository.calculateTotals(TEST_RECONCILIATION_ID_1)).willReturn(List.of(totalDto1));
        request.setByAccountFlag(ITLReconciliationProvideTotalsService.PROVIDE_TOTALS_WITHOUT_ACCOUNT_IDENTIFIER);
        ReceiveTotalsRequest receiveTotalsRequest = cut.provideTotals(request);

        assertThat(receiveTotalsRequest.getTotals()).hasSize(1);
        Total total1 = receiveTotalsRequest.getTotals()[0];
        assertThat(total1.getUnitType()).isEqualTo(TEST_UNIT_TYPE_1.getPrimaryCode());
        assertThat(total1.getSuppUnitType()).isEqualTo(TEST_UNIT_TYPE_1.getSupplementaryCode());
        assertThat(total1.getAccountType()).isEqualTo(TEST_ACCOUNT_TYPE_1.getCode());
    }

    @Test
    public void shouldUpdateLog() {

        cut.updateReconciliationLog(TEST_RECONCILIATION_ID_1);

        assertThat(reconciliationLog.getReconPhaseCode()).isEqualTo(ITLReconciliationPhase.TOTALS);
        assertThat(reconciliationLog.getReconciliationStatusHistories()).hasSize(1);
        ITLReconciliationStatusHistory historyEntry = reconciliationLog.getReconciliationStatusHistories().get(0);
        assertThat(historyEntry.getReconStatus()).isEqualTo(ITLReconciliationStatus.INITIATED);
        assertThat(historyEntry.getReconLogDatetime()).isNotNull();
        assertThat(historyEntry.getReconciliationLog()).isEqualTo(reconciliationLog);
    }
}
