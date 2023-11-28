package gov.uk.ets.registry.api.file.reference.repository;

import gov.uk.ets.registry.api.file.reference.domain.ReferenceFile;
import gov.uk.ets.registry.api.file.reference.domain.type.ReferenceFileType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * This repository fetches data from the reference_files database table.
 */
public interface ReferenceFileRepository extends JpaRepository<ReferenceFile, Long> {

    @Query(value = "select rf from ReferenceFile rf where rf.referenceType = ?1 order by rf.document")
    List<ReferenceFile> findByReferenceFileType(ReferenceFileType referenceType);

}
