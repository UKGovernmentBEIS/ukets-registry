package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.AccountRepresentativeInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Data repository for account holder representative invitations.
 */

@Repository
public interface AccountRepresentativeInvitationRepository extends JpaRepository<AccountRepresentativeInvitation, Long> {

    @Query(value = """
    select ari.invitedDate
    from AccountRepresentativeInvitation ari
    where ari.accountHolderRepresentative.id = :representativeId
      and ari.account.id = :accountId
    """)
    Optional<LocalDateTime> findDateByRepresentativeIdAndAccountId(
            Long representativeId,
            Long accountId
    );

}
