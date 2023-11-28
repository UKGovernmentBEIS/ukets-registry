package gov.uk.ets.publication.api.repository;

import gov.uk.ets.publication.api.domain.Section;
import gov.uk.ets.publication.api.model.SectionType;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    
    /**
     * Retrieves all sections of a specific type (KP or ETS).
     *
     * @param sectionType the section type (KP or ETS)
     * @return all sections of this type
     */
    List<Section> findBySectionType(SectionType sectionType);

    /**
     * Retrieves details of a specific section.
     *
     * @param id the section id
     * @return section details
     */
    Optional<Section> findById(Long id);
}
