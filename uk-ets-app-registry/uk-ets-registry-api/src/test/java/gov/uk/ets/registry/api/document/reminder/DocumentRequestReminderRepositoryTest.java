package gov.uk.ets.registry.api.document.reminder;

import gov.uk.ets.registry.api.document.reminder.domain.DocumentRequestReminder;
import gov.uk.ets.registry.api.document.reminder.repository.DocumentRequestReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
public class DocumentRequestReminderRepositoryTest {

    @Autowired
    private DocumentRequestReminderRepository repository;

    private static Long savedId;

    @BeforeEach
    public void setUp() throws Exception {

        DocumentRequestReminder documentRequestReminder1 = new DocumentRequestReminder();
        documentRequestReminder1.setRequestIdentifier(1L);
        documentRequestReminder1.setClaimantUrid("claimantId1");
        documentRequestReminder1.setReminderSentAt(new Date());
        savedId = repository.save(documentRequestReminder1).getId();

        DocumentRequestReminder documentRequestReminder2 = new DocumentRequestReminder();
        documentRequestReminder2.setRequestIdentifier(2L);
        documentRequestReminder2.setClaimantUrid("claimantId2");
        documentRequestReminder2.setReminderSentAt(null);
        repository.save(documentRequestReminder2);

        DocumentRequestReminder documentRequestReminder3 = new DocumentRequestReminder();
        documentRequestReminder3.setRequestIdentifier(3L);
        documentRequestReminder3.setClaimantUrid("claimantId3");
        repository.save(documentRequestReminder3);
    }

    @Test
    public void test_getCorrectNumberOfItems() {
        assertEquals(repository.findAll().size(), 3);
        repository.deleteById(savedId);
        assertEquals(repository.findAll().size(), 2);
    }

    @Test
    public void test_uniqueConstraint() {
        DocumentRequestReminder documentRequestReminder3 = new DocumentRequestReminder();
        documentRequestReminder3.setRequestIdentifier(3L);
        documentRequestReminder3.setClaimantUrid("claimantId3");
        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(documentRequestReminder3));
    }

    @Test
    public void test_getRemindersByRequestIdentifier1() {
        Optional<DocumentRequestReminder> documentRequestReminderOptional = repository.findByRequestIdentifier(1L);
        assertTrue(documentRequestReminderOptional.isPresent());
        DocumentRequestReminder documentRequestReminder = documentRequestReminderOptional.get();
        assertEquals(documentRequestReminder.getRequestIdentifier(), 1L);
        assertEquals(documentRequestReminder.getClaimantUrid(), "claimantId1");
        assertNotNull(documentRequestReminder.getReminderSentAt());
    }

    @Test
    public void test_getRemindersByRequestIdentifier2() {
        Optional<DocumentRequestReminder> documentRequestReminderOptional = repository.findByRequestIdentifier(2L);
        assertTrue(documentRequestReminderOptional.isPresent());
        DocumentRequestReminder documentRequestReminder = documentRequestReminderOptional.get();
        assertEquals(documentRequestReminder.getRequestIdentifier(), 2L);
        assertEquals(documentRequestReminder.getClaimantUrid(), "claimantId2");
        assertNull(documentRequestReminder.getReminderSentAt());
    }

    @Test
    public void test_getRemindersByRequestIdentifier3() {
        Optional<DocumentRequestReminder> documentRequestReminderOptional = repository.findByRequestIdentifier(3L);
        assertTrue(documentRequestReminderOptional.isPresent());
        DocumentRequestReminder documentRequestReminder = documentRequestReminderOptional.get();
        assertEquals(documentRequestReminder.getRequestIdentifier(), 3L);
        assertEquals(documentRequestReminder.getClaimantUrid(), "claimantId3");
        assertNull(documentRequestReminder.getReminderSentAt());
    }

}
