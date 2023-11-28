package gov.uk.ets.registry.api.itl.reconciliation.service;


import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLRecoSequenceResponseLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLRecoSequenceResponseLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationStatusHistoryRepository;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import lombok.AllArgsConstructor;
import uk.gov.ets.lib.commons.kyoto.types.ReconciliationResultRequest;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ITLReconciliationResultService {

    private ITLReconciliationLogRepository reconciliationRepository;
    private ITLRecoSequenceResponseLogRepository recoSequenceResponseLogRepository;
    private ITLReconciliationStatusHistoryRepository reconciliationStatusHistoryRepository;
    
    public void writeReconciliationResult(ReconciliationResultRequest request) {
        Optional<ITLReconciliationLog> reconciliationLog =  reconciliationRepository.findById(request.getReconciliationIdentifier());
        ITLReconciliationStatusHistory history = new ITLReconciliationStatusHistory();
        Optional<ITLReconciliationStatus> reconciliationStatus = ITLReconciliationStatus.fromCode(request.getReconciliationStatus());
        if (reconciliationStatus.isPresent()) {
            history.setReconStatus(reconciliationStatus.get());
        }

        if (reconciliationLog.isPresent()) {
            history.setReconciliationLog(reconciliationLog.get());
        }
        history.setReconLogDatetime(new Date());

        reconciliationStatusHistoryRepository.save(history);
    
        Arrays.stream(request.getResponseCodes()).forEach(t -> {
            recoSequenceResponseLogRepository.save(toITLRecoSequenceResponseLog(reconciliationLog.get().getReconId(),t));
        });
    
        ITLRecoSequenceResponseLog sequenceResponseLog = new ITLRecoSequenceResponseLog();
        sequenceResponseLog.setDatetime(new Date());

    }
    
    private ITLRecoSequenceResponseLog toITLRecoSequenceResponseLog(String reconId, int responseCode) {
        ITLRecoSequenceResponseLog sequenceResponseLog = new ITLRecoSequenceResponseLog();
        sequenceResponseLog.setResponseCode(responseCode);
        sequenceResponseLog.setReconId(reconId);
        
        return sequenceResponseLog;
    }
}
