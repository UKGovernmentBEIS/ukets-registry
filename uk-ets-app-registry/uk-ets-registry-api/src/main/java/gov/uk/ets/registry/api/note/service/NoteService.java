package gov.uk.ets.registry.api.note.service;

import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.note.domain.Note;
import gov.uk.ets.registry.api.note.domain.NoteDomainType;
import gov.uk.ets.registry.api.note.repository.NoteRepository;
import gov.uk.ets.registry.api.note.web.model.NoteDto;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final TaskRepository taskRepository;
    private final AuthorizationService authorizationService;

    // Contains the validations for the domain objects.
    private Map<NoteDomainType, Predicate<String>> entityCheck;

    /**
     * Initialize the validations.
     */
    @PostConstruct
    public void init() {
        entityCheck = new EnumMap<>(NoteDomainType.class);
        entityCheck.put(NoteDomainType.ACCOUNT, id -> accountRepository.findByIdentifier(Long.parseLong(id)).isPresent());
        entityCheck.put(NoteDomainType.ACCOUNT_HOLDER, id -> accountHolderRepository.getAccountHolder(Long.parseLong(id)) != null);
        entityCheck.put(NoteDomainType.TASK, id -> taskRepository.findByRequestId(Long.parseLong(id)) != null);
    }

    /**
     * Retrieve notes for a specific DomainId and DomainType.
     *
     * @param domainId domain id
     * @param domainType domain type
     * @return a list of notes
     */
    public List<NoteDto> findNotes(String domainId, NoteDomainType domainType) {

        return noteRepository.findByDomainIdAndDomainTypeOrderByCreationDateDesc(domainId, domainType).stream()
            .map(this::convert)
            .toList();
    }

    /**
     * Add a note for a specific domain type.
     *
     * @param noteDto supplied note
     * @return persisted note
     */
    public NoteDto addNote(NoteDto noteDto) {

        if (!entityCheck.get(noteDto.getDomainType()).test(noteDto.getDomainId())){
            throw new NotFoundException("Domain Type " + noteDto.getDomainType() + " with Domain Id: " + noteDto.getDomainId() + " does not exists.");
        }
        User user = userRepository.findByUrid(authorizationService.getUrid());

        Note note = new Note();
        note.setUser(user);
        note.setDomainType(noteDto.getDomainType());
        note.setDomainId(noteDto.getDomainId());
        note.setDescription(noteDto.getDescription());
        note.setCreationDate(new Date());

        Note saved = noteRepository.save(note);
        return convert(saved);
    }

    /**
     * Delete note by id.
     *
     * @param noteId supplied note id
     */
    public void deleteNote(Long noteId) {

        noteRepository.deleteById(noteId);
    }

    private NoteDto convert(Note note) {

        NoteDto dto = new NoteDto();

        dto.setId(note.getId());
        dto.setDomainType(note.getDomainType());
        dto.setDomainId(note.getDomainId());
        dto.setDescription(note.getDescription());
        dto.setCreationDate(note.getCreationDate());

        User user = note.getUser();
        String userName = Optional.ofNullable(user.getKnownAs())
            .filter(knownAs -> !knownAs.isBlank())
            .orElse(user.getFirstName() + " " + user.getLastName());
        dto.setUserName(userName);
        dto.setUserIdentifier(user.getUrid());

        return dto;
    }
}
