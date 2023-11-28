package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for Bank Holidays.
 */
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

}
