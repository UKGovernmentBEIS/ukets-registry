package gov.uk.ets.registry.api.note.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.note.domain.Note;
import gov.uk.ets.registry.api.note.domain.NoteDomainType;
import gov.uk.ets.registry.api.note.repository.NoteRepository;
import gov.uk.ets.registry.api.note.web.model.NoteDto;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountHolderRepository accountHolderRepository;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private TaskRepository taskRepository;

    private NoteService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new NoteService(noteRepository, userRepository, accountRepository, accountHolderRepository, taskRepository, authorizationService);
        service.init();
    }

    @Test
    void testGetNotes() {
        // given
        Note note = new Note();
        note.setId(1L);
        note.setDescription("test");
        note.setDomainType(NoteDomainType.ACCOUNT);
        note.setDomainId("111");

        User user = getUser(null);
        note.setUser(user);

        Note note2 = new Note();
        note2.setId(2L);
        note2.setDescription("test2");
        note2.setDomainType(NoteDomainType.ACCOUNT);
        note2.setDomainId("111");

        User user2 = getUser("Unknown");
        note2.setUser(user2);

        Mockito.when(noteRepository.findByDomainIdAndDomainTypeOrderByCreationDateDesc("111", NoteDomainType.ACCOUNT))
            .thenReturn(List.of(note, note2));

        // when
        List<NoteDto> results = service.findNotes("111", NoteDomainType.ACCOUNT);

        // then
        assertEquals(2, results.size());
        NoteDto dto = results.get(0);
        assertEquals(1, dto.getId());
        assertEquals("test", dto.getDescription());
        assertEquals("111", dto.getDomainId());
        assertEquals("Tony Montana", dto.getUserName());
        assertEquals("UK123456", dto.getUserIdentifier());

        NoteDto dto2 = results.get(1);
        assertEquals(2, dto2.getId());
        assertEquals("test2", dto2.getDescription());
        assertEquals("111", dto2.getDomainId());
        assertEquals("Unknown", dto2.getUserName());
        assertEquals("UK123456", dto2.getUserIdentifier());
    }

    @Test
    void testAddNote() {
        // given
        NoteDto dto = new NoteDto();
        dto.setDescription("test");
        dto.setDomainType(NoteDomainType.ACCOUNT);
        dto.setDomainId("111");

        User user = getUser(null);

        Mockito.when(accountRepository.findByIdentifier(111L)).thenReturn(Optional.of(new Account()));
        Mockito.when(authorizationService.getUrid()).thenReturn("urid");
        Mockito.when(userRepository.findByUrid("urid")).thenReturn(user);

        Note note = new Note();
        note.setId(1L);
        note.setDescription("test");
        note.setDomainType(NoteDomainType.ACCOUNT);
        note.setDomainId("111");
        note.setUser(user);

        Mockito.when(noteRepository.save(any(Note.class))).thenReturn(note);

        // when
        NoteDto result = service.addNote(dto);

        // then
        assertEquals(1, result.getId());
        assertEquals("test", result.getDescription());
        assertEquals("111", result.getDomainId());
        assertEquals("Tony Montana", result.getUserName());
        assertEquals("UK123456", result.getUserIdentifier());
    }

    @Test
    void testDeleteNote() {
        // when
        service.deleteNote(111L);

        // then
        Mockito.verify(noteRepository, Mockito.times(1)).deleteById(111L);
    }

    private User getUser(String knownAs) {
        User user = new User();
        user.setFirstName("Tony");
        user.setLastName("Montana");
        user.setUrid("UK123456");
        user.setKnownAs(knownAs);
        return user;
    }
}
