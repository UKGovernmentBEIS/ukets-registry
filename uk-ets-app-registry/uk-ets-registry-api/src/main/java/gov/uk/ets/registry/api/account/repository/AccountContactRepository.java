package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AccountContactRepository extends JpaRepository<MetsAccountContact, Long> {

    @Transactional(readOnly = true)
    List<MetsAccountContact> findByAccountIdentifier(Long accountIdentifier);
}
