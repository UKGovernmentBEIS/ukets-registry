package gov.uk.ets.publication.api.migration.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MigratorHistoryRepository extends JpaRepository<MigratorHistory, Long> {

    List<MigratorHistory> findByMigratorName(MigratorName migratorName);
}
