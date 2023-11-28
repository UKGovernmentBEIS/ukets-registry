package gov.uk.ets.publication.api.repository;

import gov.uk.ets.publication.api.domain.PublicationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationScheduleRepository extends JpaRepository<PublicationSchedule, Long> {

}
