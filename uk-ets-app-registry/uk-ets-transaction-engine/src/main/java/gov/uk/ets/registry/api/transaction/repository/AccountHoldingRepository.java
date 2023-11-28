package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for account holdings.
 */
public interface AccountHoldingRepository extends JpaRepository<UnitBlock, Long> {

    /**
     * Retrieves the account identifier.
     *
     * @param fullAccountIdentifier The full account identifier.
     * @return an identifier, or null if the account does not exist.
     */
    @Query("select a.identifier from Account a where a.fullIdentifier = ?1")
    Long getAccountIdentifier(String fullAccountIdentifier);

    /**
     * Updates the account balance.
     *
     * @param identifier The account identifier.
     * @param balance    The balance.
     * @param unitType   The unit type (e.g. Multiple).
     */
    @Modifying
    @Query("update Account set balance = ?2, unitType = ?3 where identifier = ?1")
    void updateAccountBalance(Long identifier, Long balance, UnitType unitType);

    /**
     * Returns the distinct available unit types by this account.
     *
     * @param accountIdentifier The account identifier.
     * @return some unit types.
     */
    @Query("select distinct type from UnitBlock where accountIdentifier = ?1 and reservedForTransaction is null")
    List<UnitType> getAccountUnitTypes(Long accountIdentifier);

    /**
     * Retrieves the balance of the account, regardless the unit type.
     *
     * @param accountIdentifier The account identifier.
     * @return the balance.
     */
    @Query("select sum(endBlock - startBlock + 1)" +
        "  from UnitBlock " +
        " where accountIdentifier = ?1")
    Long getAccountBalance(Long accountIdentifier);

    /**
     * Retrieves the holdings of the account.
     *
     * @param accountIdentifier The account identifier.
     * @return the holdings.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
        "       type,                          " +
        "       originalPeriod,                " +
        "       applicablePeriod,              " +
        "       environmentalActivity,         " +
        "       sum(endBlock - startBlock + 1)," +
        "       '',                            " +
        "       subjectToSop,                  " +
        "       projectNumber)                 " +
        " from UnitBlock                       " +
        "where accountIdentifier = ?1          " +
        "  and reservedForTransaction is null  " +
        "group by type, originalPeriod, applicablePeriod, environmentalActivity, subjectToSop, projectNumber")
    List<TransactionBlockSummary> getHoldingsOverview(Long accountIdentifier);

    /**
     * Retrieves the holdings of the account.
     *
     * @param accountIdentifier The account identifier.
     * @return the holdings.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
        "       type,                          " +
        "       originalPeriod,                " +
        "       applicablePeriod,              " +
        "       environmentalActivity,         " +
        "       sum(endBlock - startBlock + 1)," +
        "       '',                            " +
        "       subjectToSop,                  " +
        "       projectNumber,                 " +
        "       originatingCountryCode)        " +
        " from UnitBlock                       " +
        "where accountIdentifier = ?1          " +
        "  and reservedForTransaction is null  " +
        "group by type, originalPeriod, applicablePeriod, environmentalActivity, subjectToSop, projectNumber, " +
        "originatingCountryCode")
    List<TransactionBlockSummary> getHoldingsOverviewForProposal(Long accountIdentifier);

    /**
     * Retrieves the holdings of the account.
     *
     * @param accountIdentifier The account identifier.
     * @param unitTypes         The unit types.
     * @return the holdings.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
        "       type,                          " +
        "       originalPeriod,                " +
        "       applicablePeriod,              " +
        "       sum(endBlock - startBlock + 1)," +
        "       subjectToSop)                  " +
        " from UnitBlock                       " +
        "where accountIdentifier = ?1          " +
        "  and type in ?2                      " +
        "  and reservedForTransaction is null  " +
        "group by type, originalPeriod, applicablePeriod, subjectToSop")
    List<TransactionBlockSummary> getHoldingsForTransaction(Long accountIdentifier, List<UnitType> unitTypes);


    /**
     * Retrieves the holdings of the account in order to be used in the Replacement Transaction to filter
     * units for ITL Notification type 4 and 5.
     *
     * @param accountIdentifier     The account identifier
     * @param unitTypes             The unit types
     * @param itlProjectNumber      The ITL project number
     * @param replacementUnitType   The Replacement Unit type
     * @return the holdings
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
            "       type,                          " +
            "       originalPeriod,                " +
            "       applicablePeriod,              " +
            "       sum(endBlock - startBlock + 1)," +
            "       subjectToSop)                  " +
            " from UnitBlock                       " +
            "where accountIdentifier = ?1          " +
            "  and type in ?2                      " +
            "  and reservedForTransaction is null  " +
            "  and id not in " +
            "        ( select ub2.id from UnitBlock ub2 " +
            "          where ub2.projectNumber = ?3 " +
            "          and ub2.type = ?4 " +
            "          and ub2.reservedForReplacement is null " +
            "          and (ub2.replaced = false or ub2.replaced is null) " +
            "        )  " +
            "group by type, originalPeriod, applicablePeriod, subjectToSop")
    List<TransactionBlockSummary> getHoldingsForTransactionReplacement(Long accountIdentifier, List<UnitType> unitTypes, 
                                                                       String itlProjectNumber, UnitType replacementUnitType);

    /**
     * Retrieves the holdings of the account in order to be used in the Replacement Transaction to filter
     * units for ITL Notification type 4 and 5.
     *
     * @param accountIdentifier     The account identifier
     * @param unitTypes             The unit types
     * @param itlProjectNumber      The ITL project number
     * @param replacementUnitType   The Replacement Unit type
     * @return the holdings
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
            "       type,                          " +
            "       originalPeriod,                " +
            "       applicablePeriod,              " +
            "       sum(endBlock - startBlock + 1)," +
            "       subjectToSop)                  " +
            " from UnitBlock                       " +
            "where accountIdentifier = ?1          " +
            "  and projectNumber = ?2 " + 
            "  and (replaced = false or replaced is null) " + 
            "  and reservedForTransaction is null  " +
            "  and reservedForReplacement is null " +
            "group by type, originalPeriod, applicablePeriod, subjectToSop")
    List<TransactionBlockSummary> getHoldingsToBeReplacedForTransactionReplacement(Long accountIdentifier,String itlProjectNumber);
    
    /**
     * Retrieves the holdings of the account.
     *
     * @param accountIdentifier The account identifier.
     * @return the holdings.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
        "       type,                          " +
        "       originalPeriod,                " +
        "       applicablePeriod,              " +
        "       sum(endBlock - startBlock + 1)," +
        "       subjectToSop)                  " +
        " from UnitBlock                       " +
        "where accountIdentifier = ?1          " +
        "  and type in ?2                      " +
        "  and applicablePeriod = ?3           " +
        "  and reservedForTransaction is null  " +
        "group by type, originalPeriod, applicablePeriod, subjectToSop")
    List<TransactionBlockSummary> getHoldingsForTransaction(Long accountIdentifier, List<UnitType> unitTypes,
                                                            CommitmentPeriod commitmentPeriod);

    /**
     * Retrieves the holding of the account with the following criteria
     *
     * @param accountIdentifier The account identifier
     * @param unitTypes         The requested unit types ie AAU, RMU etc
     * @param commitmentPeriod  The commitment period ie CP1, CP2
     * @param subjectToSop      The flag true or false for subject to SOP
     * @return
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
            "       type,                          " +
            "       originalPeriod,                " +
            "       applicablePeriod,              " +
            "       sum(endBlock - startBlock + 1)," +
            "       subjectToSop)                  " +
            " from UnitBlock                       " +
            "where accountIdentifier = ?1          " +
            "  and type in ?2                      " +
            "  and applicablePeriod = ?3           " +
            "  and subjectToSop = ?4           " +
            "  and reservedForTransaction is null  " +
            "group by type, originalPeriod, applicablePeriod, subjectToSop")
    List<TransactionBlockSummary> getHoldingsForTransaction(Long accountIdentifier, List<UnitType> unitTypes,
                                                            CommitmentPeriod commitmentPeriod, boolean subjectToSop);

    /**
     * Retrieves the holdings of the account.
     *
     * @param accountIdentifier The account identifier.
     * @return the holdings.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
        "       type,                          " +
        "       originalPeriod,                " +
        "       applicablePeriod,              " +
        "       sum(endBlock - startBlock + 1)," +
        "       subjectToSop)                  " +
        " from UnitBlock                       " +
        "where accountIdentifier = ?1          " +
        "  and type in ?2                      " +
        "  and applicablePeriod = ?3           " +
        "  and projectNumber = ?4              " +
        "  and reservedForTransaction is null  " +
        "group by type, originalPeriod, applicablePeriod, subjectToSop")
    List<TransactionBlockSummary> getHoldingsForTransaction(
        Long accountIdentifier, List<UnitType> unitTypes, CommitmentPeriod commitmentPeriod, String projectNumber);

    /**
     * Retrieves the holding of the account with the following criteria
     *
     * @param accountIdentifier         The account identifier
     * @param unitTypes                 The requested unit types ie AAU, RMU etc
     * @param commitmentPeriod          The commitment period ie CP1, CP2
     * @param originatingCountryCode    The originating country code i.e GB
     * @return the holdings
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
            "       type,                          " +
            "       originalPeriod,                " +
            "       applicablePeriod,              " +
            "       sum(endBlock - startBlock + 1)," +
            "       subjectToSop)                  " +
            " from UnitBlock                       " +
            "where accountIdentifier = ?1          " +
            "  and type in ?2                      " +
            "  and applicablePeriod = ?3           " +
            "  and originatingCountryCode = ?4     " +
            "  and reservedForTransaction is null  " +
            "group by type, originalPeriod, applicablePeriod, subjectToSop")
    List<TransactionBlockSummary> getHoldingsForTransactionWithSpecificOriginatingCountryCode(Long accountIdentifier, List<UnitType> unitTypes,
                                                                                              CommitmentPeriod commitmentPeriod, String originatingCountryCode);

    /**
     * Retrieves the holdings of the account.
     *
     * @param accountIdentifier The account identifier.
     * @return the holdings.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary(" +
        "       type,                          " +
        "       originalPeriod,                " +
        "       applicablePeriod,              " +
        "       sum(endBlock - startBlock + 1)," +
        "       subjectToSop)                  " +
        " from UnitBlock                       " +
        "where accountIdentifier = ?1          " +
        "  and type in ?2                      " +
        "  and projectNumber = ?3              " +
        "  and reservedForTransaction is null  " +
        "group by type, originalPeriod, applicablePeriod, subjectToSop")
    List<TransactionBlockSummary> getHoldingsForTransaction(
        Long accountIdentifier, List<UnitType> unitTypes, String projectNumber);

}
