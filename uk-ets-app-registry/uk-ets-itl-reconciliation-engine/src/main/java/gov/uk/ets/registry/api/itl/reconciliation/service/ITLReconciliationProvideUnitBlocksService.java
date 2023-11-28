package gov.uk.ets.registry.api.itl.reconciliation.service;

import static java.util.stream.Collectors.toList;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLProvideUnitBlocksQueryDslRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationPhase;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import uk.gov.ets.lib.commons.kyoto.types.ProvideUnitBlocksRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveUnitBlocksRequest;
import uk.gov.ets.lib.commons.kyoto.types.UnitBlock;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ITLReconciliationProvideUnitBlocksService {

    private final ITLProvideUnitBlocksQueryDslRepository provideUnitBlocksRepository;
    private final ITLReconciliationLogRepository reconciliationLogRepository;


    /**
     * Retrieves unit blocks for specified account type, unit type and/or account period.
     * If none of the above is specified, retrieves all unit blocks.
     * Also updates the reconciliation log appropriately.
     */
    @Transactional
    public ReceiveUnitBlocksRequest provideUnitBlocks(ProvideUnitBlocksRequest request) {
        String reconciliationIdentifier = request.getReconciliationIdentifier();

        updateReconciliationLog(reconciliationIdentifier);

        List<UnitBlockDto> unitBlocks =
            ArrayUtils.isEmpty(request.getUnitBlockRequests()) ? provideAllUnitBlocks(request) :
                provideUnitBlocksWithCriteria(request);

        ReceiveUnitBlocksRequest receiveUnitBlocksRequest = new ReceiveUnitBlocksRequest();
        receiveUnitBlocksRequest.setReconciliationIdentifier(reconciliationIdentifier);
        receiveUnitBlocksRequest.setUnitBlocks(unitBlocks.toArray(new UnitBlock[0]));
        receiveUnitBlocksRequest.setMajorVersion(Constants.ITL_MAJOR_VERSION);
        receiveUnitBlocksRequest.setMinorVersion(Constants.ITL_MINOR_VERSION);
        receiveUnitBlocksRequest.setFrom(Constants.KYOTO_REGISTRY_CODE);
        receiveUnitBlocksRequest.setTo(Constants.ITL_TO);

        return receiveUnitBlocksRequest;
    }

    /**
     * Updates log phase to UNIT_BLOCK_DETAIL  and adds new history entry with status either INITIATED or ITL_TOTAL_INCON
     * (depending if this is an initial request or not).
     */
    private void updateReconciliationLog(String reconciliationIdentifier) {
        ITLReconciliationLog reconciliationLog = reconciliationLogRepository.findById(reconciliationIdentifier)
            .orElseThrow(
                () -> new IllegalArgumentException("No reconciliation log found for id: " + reconciliationIdentifier));

        ITLReconciliationStatusHistory historyEntry = new ITLReconciliationStatusHistory();
        historyEntry.setReconLogDatetime(new Date());
        // if this requests follows a prior request to provide totals it means that ITL found the totals inconsistent
        if (reconciliationLog.getReconPhaseCode() == ITLReconciliationPhase.TOTALS) {
            historyEntry.setReconStatus(ITLReconciliationStatus.ITL_TOTAL_INCON);
        } else {
            historyEntry.setReconStatus(ITLReconciliationStatus.INITIATED);
        }
        reconciliationLog.setReconPhaseCode(ITLReconciliationPhase.UNIT_BLOCK_DETAIL);
        reconciliationLog.addHistoryEntry(historyEntry);
    }


    private List<UnitBlockDto> provideAllUnitBlocks(ProvideUnitBlocksRequest request) {
        return provideUnitBlocksRepository.generateUnitBlocks(request.getReconciliationIdentifier());
    }


    private List<UnitBlockDto> provideUnitBlocksWithCriteria(ProvideUnitBlocksRequest request) {
        return Arrays.stream(request.getUnitBlockRequests())
            .flatMap(unitBlockRequest -> provideUnitBlocksRepository
                .generateUnitBlocks(request.getReconciliationIdentifier(),
                    KyotoAccountType.parse(unitBlockRequest.getAccountType()),
                    UnitType.findByPrimaryCode(unitBlockRequest.getUnitType()),
                    CommitmentPeriod.findByCode(unitBlockRequest.getAccountCommitPeriod()))
                .stream()
            )
            .collect(toList());
    }

}
