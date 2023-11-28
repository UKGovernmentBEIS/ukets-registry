package gov.uk.ets.registry.api.note.repository;

import gov.uk.ets.registry.api.note.domain.Note;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Fetches notes for a specific Account and its AccountHolder.
     * The results are sorted based on the creation date (lastest first).
     *
     * @param accountIdentifier account identifier
     * @return a list of notes
     */
    @Query("select n from Note n where (n.domainId = cast(?1 as string) and n.domainType = gov.uk.ets.registry.api.note.domain.NoteDomainType.ACCOUNT)" +
        " or (n.domainId in (select cast(a.accountHolder.identifier as string) from Account a where a.identifier = ?1)" +
        " and n.domainType = gov.uk.ets.registry.api.note.domain.NoteDomainType.ACCOUNT_HOLDER)" +
        " order by n.creationDate desc")
    List<Note> findByAccountIdentifierWithAccountHolder(Long accountIdentifier);
}
