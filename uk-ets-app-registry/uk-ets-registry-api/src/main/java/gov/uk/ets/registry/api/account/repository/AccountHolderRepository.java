package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderFileDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderTypeAheadSearchResultDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Data repository for account holders.
 */
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {

    Optional<AccountHolder> findByName(String name);
    
    List<AccountHolder> findByNameAndType(String name,AccountHolderType type);

    /**
     * Retrieves the next business identifier value.
     *
     * @return the next business identifier value.
     */
    @Query(value = "select nextval('account_holder_identifier_seq')", nativeQuery = true)
    Long getNextIdentifier();

    /**
     * Searches for account holders based on their name.
     *
     * @param nameUpperCased The name in upper case.
     * @param type           The type.
     * @return some account holders.
     */
    @Query(value =
        "select new gov.uk.ets.registry.api.accountholder.web.model.AccountHolderTypeAheadSearchResultDTO(hol.identifier, hol.name, hol.firstName, hol.lastName, hol.type)" +
            "  from AccountHolder hol" +
            " where (upper(hol.name) like %?1% or upper(concat(hol.firstName, ' ', hol.lastName)) like %?1%)" +
            "   and hol.type = ?2")
    List<AccountHolderTypeAheadSearchResultDTO> getAccountHolders(String nameUpperCased, AccountHolderType type);

    /**
     * Searches for account holders based on their identifier.
     *
     * @param identifier The identifier as a string.
     * @return some account holders.
     */
    @Query(value =
        "select new gov.uk.ets.registry.api.accountholder.web.model.AccountHolderTypeAheadSearchResultDTO(hol.identifier, hol.name, hol.firstName, hol.lastName, hol.type)" +
            "  from AccountHolder hol" +
            " where cast(identifier as string) like ?1%" +
            " and hol.type IN ?2" +
            " order by identifier")
    List<AccountHolderTypeAheadSearchResultDTO> getAccountHolders(String identifier,
                                                                  Set<AccountHolderType> includeTypes);

    /**
     * Searches for account holders connected to the provided user.
     *
     * @param urid        The URID.
     * @param holderType  The account holder type.
     * @param accessState The access state.
     * @return some account holders.
     */
    @Query(value =
        "select distinct new gov.uk.ets.registry.api.account.shared.AccountHolderDTO(hol.identifier, hol.name, hol.type, hol.firstName, hol.lastName)" +
            "  from AccountAccess acs" +
            "  join acs.user u " +
            "  join acs.account acc " +
            "  join acc.accountHolder hol " +
            " where (:urid is null or u.urid = :urid) " +
            "   and hol.type = :holderType" +
            "   and acs.state = :accessState " +
            " and acs.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED " +
            " order by hol.name")
    List<AccountHolderDTO> getAccountHolders(String urid, AccountHolderType holderType,
                                             AccountAccessState accessState);

    /**
     * Retrieves an account holder by its business identifier.
     *
     * @param identifier the business identifier.
     * @return an account holder.
     */
    @Query(value = "select h from AccountHolder h left join fetch h.contact where h.identifier = ?1")
    AccountHolder getAccountHolder(Long identifier);

    /**
     * Retrieves an account holder by the business identifier of one of its accounts.
     *
     * @param accountIdentifier The account identifier
     * @return an account holder.
     */
    @Query(value = "select h from Account a join a.accountHolder h where a.identifier = ?1")
    AccountHolder getAccountHolderOfAccount(Long accountIdentifier);

    /**
     * Retrieves the submitted account holder files.
     *
     * @param accountHolderIdentifier The account holder identifier
     * @return The list of files
     */
    @Query(value =
        "select new gov.uk.ets.registry.api.account.web.model.AccountHolderFileDTO(ahf.id, ahf.uploadedFile.fileName, ahf.documentName, ahf.uploadedFile.creationDate) " +
            "   from AccountHolderFile ahf" +
            "   join ahf.accountHolder ah " +
            "   where ah.identifier = ?1 and ahf.uploadedFile.fileStatus='SUBMITTED'")
    List<AccountHolderFileDTO> getAccountHolderFiles(Long accountHolderIdentifier);
}
