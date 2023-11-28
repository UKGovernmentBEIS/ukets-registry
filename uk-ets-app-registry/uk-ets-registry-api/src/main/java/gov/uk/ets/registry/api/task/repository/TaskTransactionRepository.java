package gov.uk.ets.registry.api.task.repository;

import gov.uk.ets.registry.api.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import gov.uk.ets.registry.api.task.domain.TaskTransaction;

import java.util.List;
import java.util.Optional;

public interface TaskTransactionRepository extends JpaRepository<TaskTransaction, Long> {
    Optional<TaskTransaction> findTaskTransactionByTransactionIdentifier(String transactionIdentifier);
    List<TaskTransaction> findTaskTransactionsByTask(Task task);
}
