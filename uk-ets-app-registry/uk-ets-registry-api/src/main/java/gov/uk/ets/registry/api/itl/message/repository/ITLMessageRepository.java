package gov.uk.ets.registry.api.itl.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;

public interface ITLMessageRepository extends JpaRepository<AcceptMessageLog, Long>,ITLMessageSearchRepository {

}
