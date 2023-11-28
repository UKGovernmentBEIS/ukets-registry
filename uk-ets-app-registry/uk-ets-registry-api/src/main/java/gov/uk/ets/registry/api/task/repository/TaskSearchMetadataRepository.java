package gov.uk.ets.registry.api.task.repository;

import gov.uk.ets.registry.api.task.searchmetadata.domain.TaskSearchMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskSearchMetadataRepository extends JpaRepository<TaskSearchMetadata, Long> {

}
