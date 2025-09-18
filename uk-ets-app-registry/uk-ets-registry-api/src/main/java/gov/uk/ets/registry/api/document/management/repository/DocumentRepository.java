package gov.uk.ets.registry.api.document.management.repository;

import gov.uk.ets.registry.api.document.management.domain.Document;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query(value = "select d from Document d where d.category.id in (select dd.category.id from Document dd where dd.id = :documentId)")
    List<Document> findAllInTheSameCategory(Long documentId);
}
