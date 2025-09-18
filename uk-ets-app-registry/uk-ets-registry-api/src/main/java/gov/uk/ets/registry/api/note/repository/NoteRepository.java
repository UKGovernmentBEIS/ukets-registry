package gov.uk.ets.registry.api.note.repository;

import gov.uk.ets.registry.api.note.domain.Note;
import gov.uk.ets.registry.api.note.domain.NoteDomainType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Fetches notes for a specific DomainId and DomainType.
     * The results are sorted based on the creation date (latest first).
     *
     * @param domainId domain id
     * @param domainType domain type
     * @return a list of notes
     */
    List<Note> findByDomainIdAndDomainTypeOrderByCreationDateDesc(String domainId, NoteDomainType domainType);
}
