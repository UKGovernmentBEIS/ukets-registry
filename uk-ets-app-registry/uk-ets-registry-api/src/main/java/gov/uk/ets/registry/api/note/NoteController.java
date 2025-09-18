package gov.uk.ets.registry.api.note;

import static gov.uk.ets.registry.api.authz.ruleengine.RuleInputType.NOTE;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.features.AnyAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorOrJuniorAdministratorRule;
import gov.uk.ets.registry.api.note.domain.NoteDomainType;
import gov.uk.ets.registry.api.note.service.NoteService;
import gov.uk.ets.registry.api.note.web.model.NoteDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    /**
     * Retrieve notes for a specific DomainId and DomainType.
     *
     * @param domainId domain id
     * @param domainType domain type
     * @return a list of notes
     */
    @Protected({
        AnyAdminRule.class
    })
    @GetMapping(path = "/notes.get", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NoteDto> getNotes(@RequestParam String domainId, @RequestParam NoteDomainType domainType) {
        return noteService.findNotes(domainId, domainType);
    }

    /**
     * Add a new note for the specified domain type.
     *
     * @param noteDto supplied note
     * @return the persisted note
     */
    @Protected({
        SeniorOrJuniorAdministratorRule.class
    })
    @PostMapping(path = "/notes.add", produces = MediaType.APPLICATION_JSON_VALUE)
    public NoteDto addNote(@RuleInput(NOTE) @RequestBody NoteDto noteDto) {
        return noteService.addNote(noteDto);
    }

    /**
     * Deletes a note.
     * This action is idempotent.
     *
     * @param noteId the note id for deletion
     */
    @Protected({
        SeniorAdminRule.class
    })
    @DeleteMapping(path = "/notes.delete/{noteId}")
    public void deleteNote(@PathVariable Long noteId) {
        noteService.deleteNote(noteId);
    }
}
