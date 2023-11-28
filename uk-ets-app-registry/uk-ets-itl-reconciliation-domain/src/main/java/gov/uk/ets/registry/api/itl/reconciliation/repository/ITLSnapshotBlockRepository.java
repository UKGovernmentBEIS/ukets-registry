package gov.uk.ets.registry.api.itl.reconciliation.repository;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLSnapshotBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface ITLSnapshotBlockRepository  extends JpaRepository<ITLSnapshotBlock, Long> {

	//This magic is an exact replication of the query executed on the ITL side for taking snapshot block data.
	//The very first Reconciliation with the ITL failed due to missing the unit types ALLOWANCE_CP0 and FORMER_EUA.
	//The case statement is needed because the FORMER_EUA (1,1) unit types are considered as AAUs (1,0) for the ITL.
	//So please do not touch , it just works.
    @Transactional
    @Modifying
    @Query(
        value = 
           "INSERT INTO ITL_SNAPSHOT_BLOCK (ID, SNAP_LOG_ID,ACCOUNT_TYPE, ACCOUNT_ID, ORIGINATING_COUNTRY_CODE, UNIT_TYPE,START_BLOCK, END_BLOCK, ACCOUNT_PERIOD, APPLICABLE_PERIOD, TRANSACTION_ID)"
        + "	SELECT nextval('ITL_SNAPSHOT_BLOCK_SEQ'), :snapshotLogId, ACCOUNT_TYPE, ACCOUNT_IDENTIFIER,ORIGINATING_COUNTRY_CODE, UNIT_TYPE, START_BLOCK, END_BLOCK, PERIOD_CODE, APPLICABLE_PERIOD,TRANSACTION_ID"
	    + "	FROM (SELECT A.KYOTO_ACCOUNT_TYPE AS ACCOUNT_TYPE, ACCOUNT_IDENTIFIER, ORIGINATING_COUNTRY_CODE, "
	    + "                                                                                                                                                CASE WHEN UB.UNIT_TYPE='FORMER_EUA' THEN 'AAU' "
	    + "                                                                                                                                                ELSE UB.UNIT_TYPE "
	    + "                                                                                                                                                END AS UNIT_TYPE,"
	    + "                     START_BLOCK, END_BLOCK, 'CP0' as PERIOD_CODE, APPLICABLE_PERIOD, RESERVED_FOR_TRANSACTION AS TRANSACTION_ID "
	    + "		  FROM UNIT_BLOCK UB INNER JOIN ACCOUNT A ON UB.ACCOUNT_IDENTIFIER=A.IDENTIFIER "
	    + "		  WHERE UB.UNIT_TYPE IN ('AAU','RMU','ERU_FROM_AAU','ERU_FROM_RMU','CER','TCER','LCER','ALLOWANCE_CP0','FORMER_EUA') "
	    + "					AND KYOTO_ACCOUNT_TYPE IN ('PARTY_HOLDING_ACCOUNT', 'PENDING_ACCOUNT', 'FORMER_OPERATOR_HOLDING_ACCOUNT', 'PERSON_HOLDING_ACCOUNT')"
	    + "		  UNION ALL"
	    + "		  SELECT A.KYOTO_ACCOUNT_TYPE AS ACCOUNT_TYPE, ACCOUNT_IDENTIFIER, ORIGINATING_COUNTRY_CODE, "
	    + "	                                                                                                                                             CASE WHEN UB.UNIT_TYPE='FORMER_EUA' THEN 'AAU' "
	    + "	                                                                                                                                             ELSE UB.UNIT_TYPE "
	    + "                                                                                                                                             END AS UNIT_TYPE,"
	    + "                    START_BLOCK, END_BLOCK, APPLICABLE_PERIOD as PERIOD_CODE, APPLICABLE_PERIOD,RESERVED_FOR_TRANSACTION AS TRANSACTION_ID"
	    + "		  FROM UNIT_BLOCK UB INNER JOIN ACCOUNT A ON UB.ACCOUNT_IDENTIFIER=A.IDENTIFIER "
	    + "		  WHERE UB.UNIT_TYPE IN ('AAU','RMU','ERU_FROM_AAU','ERU_FROM_RMU','CER','TCER','LCER','ALLOWANCE_CP0','FORMER_EUA') "
	    + "					AND KYOTO_ACCOUNT_TYPE NOT IN ('PARTY_HOLDING_ACCOUNT', 'PENDING_ACCOUNT', 'FORMER_OPERATOR_HOLDING_ACCOUNT', 'PERSON_HOLDING_ACCOUNT')) AS snapshot_block",
        nativeQuery = true)
       void insertSnapshotBlocks(@Param("snapshotLogId") Long snapshotLogId);

}
