package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Data repository for legal representatives.
 */
public interface AccountHolderRepresentativeRepository extends JpaRepository<AccountHolderRepresentative, Long> {

    /**
     * Retrieves the legal representatives by an account holder business identifier.
     * @param identifier
     * @return some legal representatives.
     */
    @Query(value = "select l from AccountHolderRepresentative l join AccountHolder h on l.accountHolder.id = h.id left join fetch l.contact where h.identifier = ?1")
    List<AccountHolderRepresentative> getAccountHolderRepresentatives(Long identifier);

    /**
     * Retrieves the account holder representative by an account holder business identifier and the contact type.
     * @param identifier the account holder identifier
     * @param contactType the contact type, primary or alternative
     * @return the account holder representative.
     */
    @Query(value = "select l from AccountHolderRepresentative l join AccountHolder h on l.accountHolder.id = h.id left join fetch l.contact where h.identifier = ?1 and l.accountContactType = ?2")
    AccountHolderRepresentative getAccountHolderRepresentative(Long identifier, AccountContactType contactType);
}
