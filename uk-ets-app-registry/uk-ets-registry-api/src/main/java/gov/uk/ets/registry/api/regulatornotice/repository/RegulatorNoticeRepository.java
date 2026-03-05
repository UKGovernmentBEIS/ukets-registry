package gov.uk.ets.registry.api.regulatornotice.repository;

import gov.uk.ets.registry.api.regulatornotice.domain.RegulatorNotice;
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
}
