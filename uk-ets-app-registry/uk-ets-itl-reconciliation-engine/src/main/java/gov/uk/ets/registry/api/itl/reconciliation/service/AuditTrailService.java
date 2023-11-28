package gov.uk.ets.registry.api.itl.reconciliation.service;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconAuditTrailTxBlock;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconAuditTrailTxLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconAuditTrailTxLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationStatusHistoryRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.InconsistentTransactionBlockRepository;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationPhase;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.ProjectTrack;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import uk.gov.ets.lib.commons.kyoto.types.ProvideAuditTrailRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveAuditTrailRequest;
import uk.gov.ets.lib.commons.kyoto.types.ReconciliationTransactionUnitBlock;
import uk.gov.ets.lib.commons.kyoto.types.Transaction;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuditTrailService {

    private ITLReconciliationLogRepository reconciliationRepository;
    private InconsistentTransactionBlockRepository auditTrailTransactionBlockRepository;
    private ITLReconAuditTrailTxLogRepository reconAuditTrailTxLogRepository;
    private ITLReconciliationStatusHistoryRepository reconciliationStatusHistoryRepository;
    private static final int COUNTRY_CODE_LENGTH = 2;

    /**
     * Finds the transaction history for each inconsistent block.
     * @param request the ProvideAuditTrailRequest
     */
    public ReceiveAuditTrailRequest provideAuditTrail(ProvideAuditTrailRequest request) {

        //Set Reconciliation status to trail.
        Optional<ITLReconciliationLog> reconOptional = reconciliationRepository.findById(request.getReconciliationIdentifier());
        if (reconOptional.isPresent()) {
            ITLReconciliationLog reconciliationLog = reconOptional.get();
            reconciliationLog.setReconPhaseCode(ITLReconciliationPhase.TRAIL);

            Set<ITLReconciliationStatus> statuses = reconciliationStatusHistoryRepository.findByReconciliationIdentifier(reconciliationLog.getReconId()).stream().map(h-> h.getReconStatus()).collect(Collectors.toSet());
            ITLReconciliationStatusHistory history = new ITLReconciliationStatusHistory();
            history.setReconciliationLog(reconciliationLog);
            if (statuses.contains(ITLReconciliationStatus.ITL_TOTAL_INCON)) {
                history.setReconStatus(ITLReconciliationStatus.ITL_UNIT_INCON);
            } else {
                history.setReconStatus(ITLReconciliationStatus.INITIATED);
            }
        }
        
        Map<gov.uk.ets.registry.api.transaction.domain.Transaction,List<TransactionBlock>>  inconsistentBlocksByTransaction = new HashMap<>();
        LocalDateTime startTransactionDate = LocalDateTime.ofInstant(request.getAuditTrailBeginDatetime().toInstant(), ZoneId.of("UTC"));
        LocalDateTime endTransactionDate = LocalDateTime.ofInstant(request.getAuditTrailEndDatetime().toInstant(), ZoneId.of("UTC"));
        Arrays.stream(request.getUnitBlockIdentifiers()).forEach(t -> {
            auditTrailTransactionBlockRepository.findInconsistentTransactionBlocks(
                    startTransactionDate,
                    endTransactionDate, 
                    t.getOriginatingRegistryCode(), 
                    t.getUnitSerialBlockStart(), 
                    t.getUnitSerialBlockEnd()).
                stream().
                collect(Collectors.groupingBy(TransactionBlock::getTransaction)).
                forEach((k,v) ->  inconsistentBlocksByTransaction.merge(k, v, (blocks1,blocks2) -> {
                    blocks1.addAll(blocks2);
                    return blocks1;
                    }
                ));
        });
        
        //Convert to ws types for ReceiveAuditTrailRequest.
        List<Transaction> auditTrail = new ArrayList<>();
        inconsistentBlocksByTransaction.keySet().forEach(k -> {
            Transaction trns = toWebServiceTransactionType(k);
            trns.setTransactionBlocks(inconsistentBlocksByTransaction.
                    get(k).
                    stream().
                    map(this::toReconciliationTransactionUnitBlock).
                    distinct().
                    toArray(ReconciliationTransactionUnitBlock[]::new));
            auditTrail.add(trns);
        });
        
        //persistAuditTrail in our DB
        auditTrail.stream().map(this::toITLReconAuditTrailTxLog).forEach(t -> {
            t.setReconciliationLog(reconOptional.get());
            reconAuditTrailTxLogRepository.save(t);
        });
        
        //Send the request to the ITL.
        ReceiveAuditTrailRequest receiveAuditTrailRequest = new ReceiveAuditTrailRequest();
        receiveAuditTrailRequest.setMajorVersion(Constants.ITL_MAJOR_VERSION);
        receiveAuditTrailRequest.setMinorVersion(Constants.ITL_MINOR_VERSION);
        receiveAuditTrailRequest.setFrom(Constants.KYOTO_REGISTRY_CODE);
        receiveAuditTrailRequest.setTo(Constants.ITL_TO);
        receiveAuditTrailRequest.setReconciliationIdentifier(request.getReconciliationIdentifier());
        receiveAuditTrailRequest.setTransactions(auditTrail.toArray(new Transaction[auditTrail.size()]));
        
        return receiveAuditTrailRequest;
    }

    Transaction toWebServiceTransactionType(gov.uk.ets.registry.api.transaction.domain.Transaction transaction) {
        Transaction trns = new Transaction();

        trns.setTransactionIdentifier(transaction.getIdentifier());
        //We need to convert the LocalDateTime to Calendar.
        ZonedDateTime zdt = ZonedDateTime.ofInstant(transaction.getExecutionDate().toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
        trns.setTransactionStatusDateTime(GregorianCalendar.from(zdt));
        trns.setTransactionType(transaction.getType().getPrimaryCode());
        trns.setSuppTransactionType(transaction.getType().getSupplementaryCode());
        trns.setNotificationIdentifier(transaction.getNotificationIdentifier());
        trns.setAcquiringRegistryAccountIdentifier(transaction.getAcquiringAccount().getAccountIdentifier());
        trns.setAcquiringRegistryAccountType(transaction.getAcquiringAccount().getAccountType().getCode());
        trns.setAcquiringRegistryCode(transaction.getAcquiringAccount().getAccountRegistryCode());
        trns.setTransferringRegistryAccountIdentifier(transaction.getTransferringAccount().getAccountIdentifier());
        trns.setTransferringRegistryAccountType(transaction.getTransferringAccount().getAccountType().getCode());
        trns.setTransferringRegistryCode(transaction.getTransferringAccount().getAccountRegistryCode());

        return trns;
    }
    
    ReconciliationTransactionUnitBlock toReconciliationTransactionUnitBlock(TransactionBlock block) {

        ReconciliationTransactionUnitBlock ub = new ReconciliationTransactionUnitBlock();
        ub.setAcquiringRegistryAccountIdentifier(block.getTransaction().getAcquiringAccount().getAccountIdentifier());
        ub.setAcquiringRegistryAccountType(block.getTransaction().getAcquiringAccount().getAccountType().getCode());
        ub.setTransferringRegistryAccountIdentifier(block.getTransaction().getTransferringAccount().getAccountIdentifier());
        ub.setTransferringRegistryAccountType(block.getTransaction().getTransferringAccount().getAccountType().getCode());
        ub.setApplicableCommitPeriod(block.getApplicablePeriod().getCode());
        ub.setBlockRole(block.getBlockRole());
        ub.setExpiryDate(block.getExpiryDate());
        if (Optional.ofNullable(block.getEnvironmentalActivity()).isPresent()) {
            ub.setLULUCFActivity(block.getEnvironmentalActivity().getCode());
        }
        ub.setOriginalCommitPeriod(block.getOriginalPeriod().getCode());
        ub.setOriginatingRegistryCode(block.getOriginatingCountryCode());
        if (Optional.ofNullable(block.getProjectNumber()).isPresent()) {
            String projectIdentifier = block.getProjectNumber().substring(COUNTRY_CODE_LENGTH);
            ub.setProjectIdentifier(Integer.valueOf(projectIdentifier));
        }
        if (Optional.ofNullable(block.getProjectTrack()).isPresent()) {
            ub.setTrack(block.getProjectTrack().getCode());
        }

        ub.setUnitType(block.getType().getPrimaryCode());
        ub.setSuppUnitType(block.getType().getSupplementaryCode());
        ub.setUnitSerialBlockStart(block.getStartBlock());
        ub.setUnitSerialBlockEnd(block.getEndBlock());
        ub.setYearInCommitmentPeriod(block.getYear());

        return ub;
    }
    
    private ITLReconAuditTrailTxLog toITLReconAuditTrailTxLog(Transaction transaction) {
        ITLReconAuditTrailTxLog reconAuditTrailTxLog = new ITLReconAuditTrailTxLog();
        reconAuditTrailTxLog.setAccountRegistryCode(transaction.getAcquiringRegistryCode());
        reconAuditTrailTxLog.setAcquiringAccountType(KyotoAccountType.parse(transaction.getAcquiringRegistryAccountType()));
        reconAuditTrailTxLog.setAcquiringRegistryAccount(transaction.getAcquiringRegistryAccountIdentifier());
        reconAuditTrailTxLog.setNotificationIdentifier(transaction.getNotificationIdentifier());
        reconAuditTrailTxLog.setTransactionDate(transaction.getTransactionStatusDateTime().getTime());
        reconAuditTrailTxLog.setTransactionId(transaction.getTransactionIdentifier());
        reconAuditTrailTxLog.setTransferringAccountType(KyotoAccountType.parse(transaction.getTransferringRegistryAccountType()));
        reconAuditTrailTxLog.setTransferringRegistryAccount(transaction.getTransferringRegistryAccountIdentifier());
        reconAuditTrailTxLog.setTransferringRegistryCode(transaction.getTransferringRegistryCode());
        reconAuditTrailTxLog.setType(TransactionType.of(transaction.getTransactionType(),transaction.getSuppTransactionType()));
        
        List<ITLReconAuditTrailTxBlock> incosistentBlocks = Stream.of(transaction.getTransactionBlocks()).
                map(this::toITLReconAuditTrailTxBlock).
                distinct().
                collect(Collectors.toList());
        reconAuditTrailTxLog.setBlocks(incosistentBlocks);
        incosistentBlocks.forEach(b -> {
            b.setReconAuditTrailTxLog(reconAuditTrailTxLog);
        });
        
        return reconAuditTrailTxLog;
    }    
    
    private ITLReconAuditTrailTxBlock toITLReconAuditTrailTxBlock(ReconciliationTransactionUnitBlock block) {
        ITLReconAuditTrailTxBlock reconAuditTrailTxBlock = new ITLReconAuditTrailTxBlock();
        reconAuditTrailTxBlock.setAcquiringAccountType(KyotoAccountType.parse(block.getAcquiringRegistryAccountType()));
        reconAuditTrailTxBlock.setApplicablePeriod(CommitmentPeriod.findByCode(block.getApplicableCommitPeriod()));
        reconAuditTrailTxBlock.setBlockRole(block.getBlockRole());
        reconAuditTrailTxBlock.setStartBlock(block.getUnitSerialBlockStart());
        reconAuditTrailTxBlock.setEndBlock(block.getUnitSerialBlockEnd());
        reconAuditTrailTxBlock.setExpiryDate(block.getExpiryDate());
        reconAuditTrailTxBlock.setInstallationIdentifier(block.getInstallationIdentifier());
        reconAuditTrailTxBlock.setLulucfCode(block.getLULUCFActivity());
        reconAuditTrailTxBlock.setOriginalPeriod(CommitmentPeriod.findByCode(block.getOriginalCommitPeriod()));
        reconAuditTrailTxBlock.setOriginatingCountryCode(block.getOriginatingRegistryCode());
        if (Optional.ofNullable(block.getProjectIdentifier()).isPresent()) {
            reconAuditTrailTxBlock.setProjectId(Optional.of(block.getProjectIdentifier()).get().toString());
        }
        if(Optional.ofNullable(block.getTrack()).isPresent()) {
            reconAuditTrailTxBlock.setProjectTrack(ProjectTrack.of(block.getTrack()));
        }
        reconAuditTrailTxBlock.setTransferringAccountType(KyotoAccountType.parse(block.getTransferringRegistryAccountType()));
        reconAuditTrailTxBlock.setType(UnitType.of(block.getUnitType(), block.getSuppUnitType()));
        reconAuditTrailTxBlock.setYear(block.getYearInCommitmentPeriod());
        
        return reconAuditTrailTxBlock;
    }
}
