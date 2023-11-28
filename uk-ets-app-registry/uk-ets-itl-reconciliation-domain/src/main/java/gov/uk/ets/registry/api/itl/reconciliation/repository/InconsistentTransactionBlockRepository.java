package gov.uk.ets.registry.api.itl.reconciliation.repository;

import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InconsistentTransactionBlockRepository extends JpaRepository<TransactionBlock, Long> {

    /**
     * Query that finds the inconsistent transaction blocks
     * in the specified time frame.
     * @param auditTrailBeginDatetime the start time frame
     * @param auditTrailEndDatetime the end time frame
     * @param originatingRegistryCode the OriginatingRegistryCode of the block
     * @param startBlock the start block to search for inconsistency
     * @param endBlock the end block to search for inconsistency
     * @return the list of inconsistent blocks
     * @see table 54 (Generate_Audit_Trail (Function)) of DES
     */
    @Query("select distinct b from TransactionBlock b left join fetch b.transaction t "
            + "where t.executionDate >= :auditTrailBeginDatetime "
            + "and t.executionDate <= :auditTrailEndDatetime "
            + "and b.originatingCountryCode = :originatingRegistryCode and ("
            + "(b.startBlock >= :startBlock and b.startBlock <= :endBlock) or"
            + "(b.endBlock >= :startBlock and b.endBlock <= :endBlock) or "
            + "(b.startBlock < :startBlock and b.endBlock > :endBlock))")
    List<TransactionBlock> findInconsistentTransactionBlocks(
            @Param("auditTrailBeginDatetime") LocalDateTime auditTrailBeginDatetime,
            @Param("auditTrailEndDatetime") LocalDateTime auditTrailEndDatetime,
            @Param("originatingRegistryCode") String originatingRegistryCode,
            @Param("startBlock") Long startBlock,
            @Param("endBlock") Long endBlock);
}
