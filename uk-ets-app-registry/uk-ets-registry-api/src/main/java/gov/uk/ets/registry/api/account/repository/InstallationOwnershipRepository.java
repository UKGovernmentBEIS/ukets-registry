package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.InstallationOwnership;
import gov.uk.ets.registry.api.account.domain.InstallationOwnershipStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InstallationOwnershipRepository extends JpaRepository<InstallationOwnership, Long> {

    @Query("select io from InstallationOwnership io " +
        "join fetch io.installation i " +
        "where io.account = ?1 and io.installation = ?2 and io.status = ?3 ")
    List<InstallationOwnership> findByAccountAndInstallationAndStatus(Account account, Installation installation,
                                                                      InstallationOwnershipStatus installationOwnershipStatus);

    @Query("select io from InstallationOwnership io " +
           "join fetch io.installation i " +
           "where io.account.identifier = ?1 and io.status = ?2 ")
    List<InstallationOwnership> findByAccountIdentifierAndStatus(Long accountIdentifier,
                                                                 InstallationOwnershipStatus status);

    @Query("select io from InstallationOwnership io " +
           "join fetch io.account a " +
           "where io.installation = ?1 and io.status = ?2 " +
           "order by io.ownershipDate desc ")
    List<InstallationOwnership> findByInstallationAndStatusOrderByOwnershipDateDesc(Installation installation,
                                                                                    InstallationOwnershipStatus status);
    
    @Query("select io from InstallationOwnership io " +
            "join fetch io.installation i " +
            "where io.installation = ?1 " +
            "order by io.ownershipDate desc ")
    List<InstallationOwnership> findByInstallation(Installation installation);
}
