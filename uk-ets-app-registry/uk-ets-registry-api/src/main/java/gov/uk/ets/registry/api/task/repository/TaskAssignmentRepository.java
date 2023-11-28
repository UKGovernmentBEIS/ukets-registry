package gov.uk.ets.registry.api.task.repository;

import gov.uk.ets.registry.api.task.domain.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
}
