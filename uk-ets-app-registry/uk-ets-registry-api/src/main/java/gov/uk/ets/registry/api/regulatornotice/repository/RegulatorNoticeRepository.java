package gov.uk.ets.registry.api.regulatornotice.repository;

import gov.uk.ets.registry.api.regulatornotice.domain.RegulatorNotice;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegulatorNoticeRepository extends JpaRepository<RegulatorNotice, Long>, RegulatorNoticeSearchRepository {

    @Query(value = "select distinct notice.processType from regulator_notice notice ")
    List<String> findAllProcessTypes();

    @Query(value = "select notice from regulator_notice notice " +
            "where notice.requestId = :identifier")
    Optional<RegulatorNotice> findByIdentifier(@Param("identifier") Long identifier);

    /**
     * Checks if there are notices for specific account identifier and status.
     *
     * @param accountId The account identifier.
     * @param status    The status of the notice.
     * @return true if there are notices for the account identifier and status, false otherwise.
     */
    boolean existsByAccount_IdentifierAndStatus(Long accountId, RequestStateEnum status);
}
