package gov.uk.ets.registry.api.file.upload.services;

import gov.uk.ets.registry.api.file.upload.adhoc.services.AdHocEmailRecipientsValidationService;
import gov.uk.ets.registry.api.file.upload.adhoc.services.AdhocFileValidationWrapper;
import gov.uk.ets.registry.api.file.upload.adhoc.services.error.AdHocEmailRecipientsError;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdHocEmailRecipientsValidationServiceTest {

    @InjectMocks
    private AdHocEmailRecipientsValidationService adHocEmailRecipientsValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void validateFileContentTest_NoErrors() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_NO_ERRORS.xlsx");
        assertTrue(resource.exists());
        AdhocFileValidationWrapper wrapper =
            adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream());
        assertTrue(wrapper.getErrors().isEmpty());
        assertEquals(2, wrapper.getTentativeRecipients());
    }

    @Test
    void validateFileContentTest_InvalidRecipient2() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_NO_EMAIL.xlsx");
        assertTrue(resource.exists());
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.INVALID_RECIPIENT));
        assertEquals(2, errors.get(AdHocEmailRecipientsError.INVALID_RECIPIENT).size());
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.INVALID_RECIPIENT).iterator();
        assertEquals(1, iterator.next());
        assertEquals(2, iterator.next());
    }

    @Test
    void validateFileContentTest_HeaderSpaces() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_HEADER_SPACES.xlsx");
        assertTrue(resource.exists());
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.INVALID_HEADER_SPACES_NOT_ALLOWED));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.INVALID_HEADER_SPACES_NOT_ALLOWED).iterator();
        assertEquals(0, iterator.next());
    }

    @Test
    void validateFileContentTest_MissingHeader() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_MISSING_HEADER.xlsx");
        assertTrue(resource.exists());
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.MISSING_HEADER));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.MISSING_HEADER).iterator();
        assertEquals(0, iterator.next());
    }

    @Test
    void validateFileContentTest_DuplicateHeader() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_DUPLICATE_HEADER.xlsx");
        assertTrue(resource.exists());
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.DUPLICATE_COLUMNS));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.DUPLICATE_COLUMNS).iterator();
        assertEquals(0, iterator.next());
    }

    @Test
    void validateFileContentTest_DuplicateRows() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_DUPLICATE_ROWS.xlsx");
        assertTrue(resource.exists());
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.DUPLICATE_RECIPIENTS));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.DUPLICATE_RECIPIENTS).iterator();
        assertEquals(2, iterator.next());
    }

}
