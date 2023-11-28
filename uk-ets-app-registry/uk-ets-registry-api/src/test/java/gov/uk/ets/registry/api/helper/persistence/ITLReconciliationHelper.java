package gov.uk.ets.registry.api.helper.persistence;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLSnapshotBlock;
import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLSnapshotLog;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLReconciliationLogRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLSnapshotBlockRepository;
import gov.uk.ets.registry.api.itl.reconciliation.repository.ITLSnapshotLogRepository;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Profile("integrationTest")
@Component
@RequiredArgsConstructor
public class ITLReconciliationHelper {

    public static final String TEST_RECONCILIATION_ID = "123";
    public static final Long TEST_ACCOUNT_ID_1 = 123L;
    public static final Long TEST_ACCOUNT_ID_2 = 1234L;
    public static final String TEST_DATA_ISSUE_FLAG = "DIF";
    public static final String TEST_TRANSACTION_ID_1 = "UK1234567";
    public static final String TEST_TRANSACTION_ID_2 = "UK1234568";
    public static final String TEST_TRANSACTION_ID_3 = "UK1234569";
    public static final String ORIGINATING_COUNTRY_CODE = "GB";
    public static final long START_BLOCK_1 = 1L;
    public static final long END_BLOCK_1 = 10L;
    public static final long START_BLOCK_2 = 1L;
    public static final long END_BLOCK_2 = 20L;
    public static final long START_BLOCK_3 = 21L;
    public static final long END_BLOCK_3 = 35L;

    private final ITLReconciliationLogRepository reconciliationLogRepository;

    private final ITLSnapshotLogRepository snapshotLogRepository;

    private final ITLSnapshotBlockRepository snapshotBlockRepository;

    // separate transaction is required here otherwise the tests do not seem to be aware
    // of the db changes
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setupTestData() {

        resetTestData();

        LocalDateTime now = LocalDateTime.now();
        Date snapshotDatetime = Date.from(now.plus(1, ChronoUnit.MINUTES).atZone(ZoneId.systemDefault()).toInstant());
        ITLReconciliationLog reconciliationLog = ITLReconciliationLog.builder()
            .reconId(TEST_RECONCILIATION_ID)
            .reconActionBeginDatetime(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
            .reconSnapshotDatetime(
                snapshotDatetime)
            .build();
        reconciliationLog = reconciliationLogRepository.save(reconciliationLog);

        ITLSnapshotLog snapshotLog = ITLSnapshotLog.builder()
            .reconciliationLog(reconciliationLog)
            .snapshotDatetime(snapshotDatetime)
            .build();
        snapshotLogRepository.save(snapshotLog);

        ITLSnapshotBlock snapshotBlock1 = ITLSnapshotBlock.builder()
            .snapshotLog(snapshotLog)
            .accountIdentifier(TEST_ACCOUNT_ID_1)
            .accountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .type(UnitType.AAU)
            .accountPeriod(CommitmentPeriod.CP1)
            .dataIssueFlag(TEST_DATA_ISSUE_FLAG)
            .applicablePeriod(CommitmentPeriod.CP1)
            .transactionIdentifier(TEST_TRANSACTION_ID_1)
            .startBlock(START_BLOCK_1)
            .endBlock(END_BLOCK_1)
            .originatingCountryCode(ORIGINATING_COUNTRY_CODE)
            .build();

        ITLSnapshotBlock snapshotBlock2 = ITLSnapshotBlock.builder()
            .snapshotLog(snapshotLog)
            .accountIdentifier(TEST_ACCOUNT_ID_2)
            .accountType(KyotoAccountType.PERSON_HOLDING_ACCOUNT)
            .type(UnitType.RMU)
            .accountPeriod(CommitmentPeriod.CP1)
            .dataIssueFlag(TEST_DATA_ISSUE_FLAG)
            .applicablePeriod(CommitmentPeriod.CP1)
            .transactionIdentifier(TEST_TRANSACTION_ID_2)
            .startBlock(START_BLOCK_2)
            .endBlock(END_BLOCK_2)
            .originatingCountryCode(ORIGINATING_COUNTRY_CODE)
            .build();

        ITLSnapshotBlock snapshotBlock3 = ITLSnapshotBlock.builder()
            .snapshotLog(snapshotLog)
            .accountIdentifier(TEST_ACCOUNT_ID_1)
            .accountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .type(UnitType.AAU)
            .accountPeriod(CommitmentPeriod.CP1)
            .dataIssueFlag(TEST_DATA_ISSUE_FLAG)
            .applicablePeriod(CommitmentPeriod.CP1)
            .transactionIdentifier(TEST_TRANSACTION_ID_3)
            .startBlock(START_BLOCK_3)
            .endBlock(END_BLOCK_3)
            .originatingCountryCode(ORIGINATING_COUNTRY_CODE)
            .build();

        snapshotBlockRepository.saveAll(List.of(snapshotBlock1, snapshotBlock2, snapshotBlock3));
    }

    public void resetTestData() {
        snapshotBlockRepository.deleteAll();
        snapshotLogRepository.deleteAll();
        reconciliationLogRepository.deleteAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteSnapshotData() {
        snapshotBlockRepository.deleteAll();
        snapshotLogRepository.deleteAll();
    }
}
