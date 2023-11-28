package gov.uk.ets.registry.api.user.admin.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.common.publication.PublicationRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.user.admin.shared.EnrolledUserDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorityAdministrationServiceTest {
    @InjectMocks
    private AuthorityAdministrationService authorityAdministrationService;

    @Mock
    UserRepository userRepository;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountAccessRepository accountAccessRepository;

    @Mock
    UserAdministrationService userAdministrationService;
    
    @Mock
    ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;

    @Mock
    PublicationRequestAddRemoveRoleService publicationRequestAddRemoveRoleService;

    @Test
    void getEnrolledUser() {
        // given
        String urid = "test-urid";
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email";
        String iamId = "test-iam-id";
        String knownAs = "Known as";
        User user = mock(User.class);
        given(user.getUrid()).willReturn(urid);
        given(user.getState()).willReturn(UserStatus.ENROLLED);
        given(user.getFirstName()).willReturn(firstName);
        given(user.getLastName()).willReturn(lastName);
        given(user.getIamIdentifier()).willReturn(iamId);
        given(user.getKnownAs()).willReturn(knownAs);
        given(userRepository.findByUrid(eq(urid))).willReturn(user);
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        given(userRepresentation.getEmail()).willReturn(email);
        given(userAdministrationService.findByIamId(iamId)).willReturn(userRepresentation);

        //when
        EnrolledUserDTO dto = authorityAdministrationService.getEnrolledUser(urid);

        //then
        assertEquals(urid, dto.getUserId());
        assertEquals(firstName, dto.getFirstName());
        assertEquals(lastName, dto.getLastName());
        assertEquals(email, dto.getEmail());
        assertEquals(knownAs, dto.getKnownAs());
    }

    @Test
    void givenEnrolledUserDoesNotExistGetEnrolledUserShouldThrowNotFoundException() {
        givenEnrolledUserDoesNotExistShouldThrowException(NotFoundException.class, urid -> {
            authorityAdministrationService.getEnrolledUser(urid);
        });
    }

    @Test
    void setAuthorityUser() {
        // given
        String urid = "test-urid";
        String iamId = "000ss-wqeqw-33213";
        User user = mock(User.class);
        given(user.getState()).willReturn(UserStatus.ENROLLED);
        given(user.getIamIdentifier()).willReturn(iamId);
        given(userRepository.findByUrid(eq(urid))).willReturn(user);
        // when
        authorityAdministrationService.setAuthorityUser(urid);

        // then
        then(userAdministrationService).should(times(1)).addUserRole(eq(iamId), eq(UserRole.AUTHORITY_USER.getKeycloakLiteral()));
        then(userAdministrationService).should(times(1)).addUserRole(eq(iamId), eq(UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral()));
    }

    @Test
    void givenEnrolledUserDoesNotExistSetAuthorityUserShouldThrowIllegalArgumentException() {
        givenEnrolledUserDoesNotExistShouldThrowException(IllegalArgumentException.class, urid -> {
            authorityAdministrationService.setAuthorityUser(urid);
        });
    }

    private void givenEnrolledUserDoesNotExistShouldThrowException(Class<? extends Exception> expectedExceptionType, Consumer<String> action) {
        // given
        String urid = "test-urid";
        given(userRepository.findByUrid(urid)).willReturn(null);

        // when then
        assertThrows(expectedExceptionType, () -> {
            action.accept(urid);
        });

        // given
        User user = mock(User.class);
        given(user.getState()).willReturn(UserStatus.REGISTERED);
        given(userRepository.findByUrid(urid)).willReturn(user);

        // when then
        assertThrows(expectedExceptionType, () -> {
            action.accept(urid);
        });
    }


}