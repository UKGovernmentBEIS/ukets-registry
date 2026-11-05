package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.AccountRepresentativeInvitation;
import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Data repository for account holder representative invitations.
 */
public interface AccountRepresentativeInvitationRepository extends JpaRepository<AccountRepresentativeInvitation, Long> {

    @Query(value = """
    select ari
    from AccountRepresentativeInvitation ari
    where ari.accountHolderRepresentative.id = :representativeId
      and ari.account.id = :accountId
        """)
    Optional<AccountRepresentativeInvitation> findByRepresentativeIdAndAccountId(
            Long representativeId,
            Long accountId
    );

    @Query(value = """
    select ari
    from AccountRepresentativeInvitation ari
    where ari.accountHolderRepresentative.id in :representativeIds
      and ari.account.id = :accountId
    """)
    List<AccountRepresentativeInvitation> findByRepresentativeIdInAndAccountId(
            List<Long> representativeIds,
            Long accountId
    );

    List<AccountRepresentativeInvitation> findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(Long accountIdentifier, Set<String> emailAddresses);

    @Query(value = """
    select ari
    from AccountRepresentativeInvitation ari
    where ari.account.identifier = :accountIdentifier
      and ari.accountHolderRepresentative.accountContactType = :accountContactType
      and ari.accountHolderRepresentative.contact.emailAddress = :emailAddress
        """)
    Optional<AccountRepresentativeInvitation> findByAccountIdentifierAndContactTypeAndEmailAddress(Long accountIdentifier, AccountContactType accountContactType, String emailAddress);
    
    Long countByAccount_IdentifierAndInvitationStatus(Long accountIdentifier, boolean invitationStatus);

    @Transactional
    void deleteByAccountHolderRepresentative_Id(Long representativeId);
}
