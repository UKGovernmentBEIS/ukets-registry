package gov.uk.ets.registry.api.task.repository;

import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskARStatus;
import gov.uk.ets.registry.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskARStatusRepository extends JpaRepository<TaskARStatus, Long> {
    Optional<TaskARStatus> findByTaskAndUser(Task task, User user);
}
