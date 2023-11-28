package gov.uk.ets.registry.api.document.reminder.repository;

import gov.uk.ets.registry.api.document.reminder.domain.DocumentRequestReminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for tasks.
 */
public interface DocumentRequestReminderRepository extends JpaRepository<DocumentRequestReminder, Long> {

    Optional<DocumentRequestReminder> findByRequestIdentifier(Long requestIdentifier);

}
