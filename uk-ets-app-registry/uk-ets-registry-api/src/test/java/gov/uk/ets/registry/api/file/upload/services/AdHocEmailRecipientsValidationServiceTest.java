package gov.uk.ets.registry.api.file.upload.services;

import gov.uk.ets.registry.api.file.upload.adhoc.services.AdHocEmailRecipientsValidationService;
import gov.uk.ets.registry.api.file.upload.adhoc.services.AdhocFileValidationWrapper;
import gov.uk.ets.registry.api.file.upload.adhoc.services.error.AdHocEmailRecipientsError;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AdHocEmailRecipientsValidationServiceTest {

    @Mock
    private UserAdministrationService userAdministrationService;

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
        when(userAdministrationService.findByEmail(any())).thenReturn(Optional.of(new UserRepresentation()));
        AdhocFileValidationWrapper wrapper =
            adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream());
        assertTrue(wrapper.getErrors().isEmpty());
        assertEquals(2, wrapper.getTentativeRecipients());
    }

    @Test
    void validateFileContentTest_InvalidRecipient() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_NO_ERRORS.xlsx");
        assertTrue(resource.exists());
        when(userAdministrationService.findByEmail("test1@example.com")).thenReturn(Optional.of(new UserRepresentation()));
        when(userAdministrationService.findByEmail("test2@example.com")).thenReturn(Optional.empty());
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertTrue(errors.size() == 1);
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.INVALID_RECIPIENT));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.INVALID_RECIPIENT).iterator();
        assertEquals(2, iterator.next().intValue());
    }

    @Test
    void validateFileContentTest_InvalidRecipient2() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_NO_ERRORS.xlsx");
        assertTrue(resource.exists());
        when(userAdministrationService.findByEmail("test1@example.com")).thenReturn(Optional.empty());
        when(userAdministrationService.findByEmail("test2@example.com")).thenReturn(Optional.empty());
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertTrue(errors.size() == 1);
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.INVALID_RECIPIENT));
        assertEquals(errors.get(AdHocEmailRecipientsError.INVALID_RECIPIENT).size(), 2);
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.INVALID_RECIPIENT).iterator();
        assertEquals(1, iterator.next());
        assertEquals(2, iterator.next());
    }

    @Test
    void validateFileContentTest_HeaderSpaces() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_HEADER_SPACES.xlsx");
        assertTrue(resource.exists());
        when(userAdministrationService.findByEmail("test1@example.com")).thenReturn(Optional.of(new UserRepresentation()));
        when(userAdministrationService.findByEmail("test2@example.com")).thenReturn(Optional.of(new UserRepresentation()));
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertTrue(errors.size() == 1);
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.INVALID_HEADER_SPACES_NOT_ALLOWED));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.INVALID_HEADER_SPACES_NOT_ALLOWED).iterator();
        assertEquals(0, iterator.next());
    }

    @Test
    void validateFileContentTest_MissingHeader() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_MISSING_HEADER.xlsx");
        assertTrue(resource.exists());
        when(userAdministrationService.findByEmail("test1@example.com")).thenReturn(Optional.of(new UserRepresentation()));
        when(userAdministrationService.findByEmail("test2@example.com")).thenReturn(Optional.of(new UserRepresentation()));
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertTrue(errors.size() == 1);
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.MISSING_HEADER));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.MISSING_HEADER).iterator();
        assertEquals(0, iterator.next());
    }

    @Test
    void validateFileContentTest_DuplicateHeader() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_DUPLICATE_HEADER.xlsx");
        assertTrue(resource.exists());
        when(userAdministrationService.findByEmail("test1@example.com")).thenReturn(Optional.of(new UserRepresentation()));
        when(userAdministrationService.findByEmail("test2@example.com")).thenReturn(Optional.of(new UserRepresentation()));
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertTrue(errors.size() == 1);
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.DUPLICATE_COLUMNS));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.DUPLICATE_COLUMNS).iterator();
        assertEquals(0, iterator.next());
    }

    @Test
    void validateFileContentTest_DuplicateRows() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_DUPLICATE_ROWS.xlsx");
        assertTrue(resource.exists());
        when(userAdministrationService.findByEmail("test1@example.com")).thenReturn(Optional.of(new UserRepresentation()));
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertTrue(errors.size() == 1);
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.DUPLICATE_RECIPIENTS));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.DUPLICATE_RECIPIENTS).iterator();
        assertEquals(2, iterator.next());
    }

    @Test
    void validateFileContentTest_HeaderSpaces_And_InvalidRecipient() throws IOException {
        Resource resource = new ClassPathResource("AD_HOC_EMAIL_RECIPIENTS_HEADER_SPACES.xlsx");
        assertTrue(resource.exists());
        when(userAdministrationService.findByEmail("test1@example.com")).thenReturn(Optional.of(new UserRepresentation()));
        when(userAdministrationService.findByEmail("test2@example.com")).thenReturn(Optional.empty());
        Map<AdHocEmailRecipientsError, Set<Integer>> errors = adHocEmailRecipientsValidationService.validateFileContent(resource.getInputStream()).getErrors();
        assertTrue(errors.size() == 2);
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.INVALID_HEADER_SPACES_NOT_ALLOWED));
        assertTrue(errors.containsKey(AdHocEmailRecipientsError.INVALID_RECIPIENT));
        Iterator<Integer> iterator = errors.get(AdHocEmailRecipientsError.INVALID_HEADER_SPACES_NOT_ALLOWED).iterator();
        assertEquals(0, iterator.next());
        iterator = errors.get(AdHocEmailRecipientsError.INVALID_RECIPIENT).iterator();
        assertEquals(2, iterator.next());
    }
}
