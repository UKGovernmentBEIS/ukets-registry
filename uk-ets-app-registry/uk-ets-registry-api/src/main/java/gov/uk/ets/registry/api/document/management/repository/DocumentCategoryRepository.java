package gov.uk.ets.registry.api.document.management.repository;

import gov.uk.ets.registry.api.document.management.domain.DocumentCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long> {

    List<DocumentCategory> findAllByOrderByPosition();
}
