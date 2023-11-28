package gov.uk.ets.registry.api.user.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


import com.fasterxml.jackson.core.JsonProcessingException;

import gov.uk.ets.registry.api.auditevent.DomainEvent;
import gov.uk.ets.registry.api.auditevent.DomainObject;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRoleMapping;
import gov.uk.ets.registry.api.user.profile.domain.EmailChange;
import gov.uk.ets.registry.api.common.security.GenerateTokenCommand;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class EmailChangeServiceTest {
    private EmailChangeService changeEmailService;
    @Mock
    private UserService userService;
    @Mock
    private TokenVerifier tokenVerifier;
    @Mock
    private UserAdministrationService userAdministrationService;

    private Long expiration = 60L;
    private String applicationUrl = "localhost:8080";
    private String verificationPath = "verification-path";

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private EventService eventService;
    @Mock
    private EmailChangeChecker emailChangeChecker;
    @Mock
    private Mapper mapper;

    @BeforeEach
    void setUp() {
        changeEmailService = new EmailChangeService(
            userService,
            tokenVerifier,
            userAdministrationService,
            taskRepository,
            eventService,
            emailChangeChecker,
            applicationUrl,
            verificationPath,
            expiration,
            mapper);
    }

    @Test
    void requestEmailChangeWithoutSuppliedUser() throws JsonProcessingException {
        // given
        String currentUserIamIdentifier = "iamIdentifier";
        String currentUserUrid = "urid";
        String oldEmail = "test@test.old";
        String newEmail = "test@test.new";
        String verificationToken = "verificationToken";
        User currentUser = mock(User.class);
        EmailChangeDTO emailChangeDTO = EmailChangeDTO.builder()
                .newEmail(newEmail)
                .build();
        UserRepresentation currentKeycloakUser = mock(UserRepresentation.class);
        given(currentUser.getIamIdentifier()).willReturn(currentUserIamIdentifier);
        given(currentUser.getUrid()).willReturn(currentUserUrid);
        given(userService.getCurrentUser()).willReturn(currentUser);
        given(currentKeycloakUser.getEmail()).willReturn(oldEmail);
        given(userAdministrationService.findByIamId(currentUserIamIdentifier)).willReturn(currentKeycloakUser);
        given(tokenVerifier.generateToken(any())).willReturn(verificationToken);
        given(mapper.convertToJson(any())).willReturn("{\"urid\":\"urid\",\"newEmail\":\"test@test.new\",\"requesterUrid\":\"urid\"}");
        // when
        EmailChange verification = changeEmailService.requestEmailChange(emailChangeDTO);

        //then
        assertEquals(newEmail, verification.getNewEmail());
        assertEquals(oldEmail, verification.getOldEmail());
        
        ArgumentCaptor<GenerateTokenCommand> captor = ArgumentCaptor.forClass(GenerateTokenCommand.class);
        then(tokenVerifier).should(times(1)).generateToken(captor.capture());
        assertNotNull(captor.getValue().getPayload());
        EmailChangeDTO payload = new ObjectMapper().readValue(captor.getValue().getPayload(), EmailChangeDTO.class);
        assertEquals(newEmail, payload.getNewEmail());
        assertEquals(currentUserUrid, payload.getUrid());
        assertEquals(currentUserUrid, payload.getRequesterUrid());

        assertEquals(newEmail, verification.getNewEmail());
        assertEquals(oldEmail, verification.getOldEmail());
        assertEquals(applicationUrl + verificationPath + verificationToken, verification.getConfirmationUrl());
    }

    @Test
    void requestEmailChangeWithSuppliedCurrentUser() throws JsonProcessingException {
        // given
        String currentUserIamIdentifier = "iamIdentifier";
        String currentUserUrid = "urid";
        String oldEmail = "test@test.old";
        String newEmail = "test@test.new";
        String verificationToken = "verificationToken";
        User currentUser = mock(User.class);
        EmailChangeDTO emailChangeDTO = EmailChangeDTO.builder()
            .newEmail(newEmail)
            .urid(currentUserUrid)
            .build();
        UserRepresentation currentKeycloakUser = mock(UserRepresentation.class);
        given(currentUser.getIamIdentifier()).willReturn(currentUserIamIdentifier);
        given(currentUser.getUrid()).willReturn(currentUserUrid);
        given(userService.getCurrentUser()).willReturn(currentUser);
        given(currentKeycloakUser.getEmail()).willReturn(oldEmail);
        given(userAdministrationService.findByIamId(currentUserIamIdentifier)).willReturn(currentKeycloakUser);
        given(tokenVerifier.generateToken(any())).willReturn(verificationToken);
        given(mapper.convertToJson(any())).willReturn("{\"urid\":\"urid\",\"newEmail\":\"test@test.new\",\"requesterUrid\":\"urid\"}");
        // when
        EmailChange verification = changeEmailService.requestEmailChange(emailChangeDTO);

        //then
        assertEquals(newEmail, verification.getNewEmail());
        assertEquals(oldEmail, verification.getOldEmail());
        ArgumentCaptor<GenerateTokenCommand> captor = ArgumentCaptor.forClass(GenerateTokenCommand.class);
        then(tokenVerifier).should(times(1)).generateToken(captor.capture());
        assertNotNull(captor.getValue().getPayload());
        EmailChangeDTO payload = new ObjectMapper().readValue(captor.getValue().getPayload(), EmailChangeDTO.class);
        assertEquals(newEmail, payload.getNewEmail());
        assertEquals(currentUserUrid, payload.getUrid());
        assertEquals(currentUserUrid, payload.getRequesterUrid());

        assertEquals(newEmail, verification.getNewEmail());
        assertEquals(oldEmail, verification.getOldEmail());
        assertEquals(applicationUrl + verificationPath + verificationToken, verification.getConfirmationUrl());
    }

    @Test
    void requestEmailChangeBySeniorAdmin() throws JsonProcessingException {
        // given
        String otherUserIamIdentifier = "otherIamIdentifier";
        String currentUserUrid = "urid";
        String otherUserUrid = "otherUserUrid";
        String oldEmail = "test@test.old";
        String newEmail = "test@test.new";
        String verificationToken = "verificationToken";

        User currentUser = mock(User.class);
        UserRoleMapping userRoleMapping = mock(UserRoleMapping.class);
        IamUserRole iamUserRole = mock(IamUserRole.class);

        given(currentUser.getUrid()).willReturn(currentUserUrid);
        given(currentUser.getUserRoles()).willReturn(Set.of(userRoleMapping));
        given(userRoleMapping.getRole()).willReturn(iamUserRole);
        given(iamUserRole.getRoleName()).willReturn("senior-registry-administrator");

        User otherUser = mock(User.class);
        given(otherUser.getIamIdentifier()).willReturn(otherUserIamIdentifier);
        given(otherUser.getUrid()).willReturn(otherUserUrid);

        EmailChangeDTO emailChangeDTO = EmailChangeDTO.builder()
            .newEmail(newEmail)
            .urid(otherUserUrid)
            .build();

        UserRepresentation otherKeycloakUser = mock(UserRepresentation.class);

        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.getUserByUrid(otherUserUrid)).willReturn(otherUser);
        given(otherKeycloakUser.getEmail()).willReturn(oldEmail);
        given(userAdministrationService.findByIamId(otherUserIamIdentifier)).willReturn(otherKeycloakUser);
        given(tokenVerifier.generateToken(any())).willReturn(verificationToken);
        given(mapper.convertToJson(any())).willReturn("{\"urid\":\"otherUserUrid\",\"newEmail\":\"test@test.new\",\"requesterUrid\":\"urid\"}");
        // when
        EmailChange verification = changeEmailService.requestEmailChange(emailChangeDTO);

        //then
        assertEquals(newEmail, verification.getNewEmail());
        assertEquals(oldEmail, verification.getOldEmail());
        ArgumentCaptor<GenerateTokenCommand> captor = ArgumentCaptor.forClass(GenerateTokenCommand.class);
        then(tokenVerifier).should(times(1)).generateToken(captor.capture());
        assertNotNull(captor.getValue().getPayload());
        EmailChangeDTO payload = new ObjectMapper().readValue(captor.getValue().getPayload(), EmailChangeDTO.class);
        assertEquals(newEmail, payload.getNewEmail());
        assertEquals(otherUserUrid, payload.getUrid());
        assertEquals(currentUserUrid, payload.getRequesterUrid());

        assertEquals(newEmail, verification.getNewEmail());
        assertEquals(oldEmail, verification.getOldEmail());
        assertEquals(applicationUrl + verificationPath + verificationToken, verification.getConfirmationUrl());
    }

    @Test
    void requestEmailChangeByNonSeniorAdmin() {
        // given
        String currentUserUrid = "urid";
        String otherUserUrid = "otherUserUrid";
        String newEmail = "test@test.new";

        User currentUser = mock(User.class);
        UserRoleMapping userRoleMapping = mock(UserRoleMapping.class);
        IamUserRole iamUserRole = mock(IamUserRole.class);

        given(currentUser.getUrid()).willReturn(currentUserUrid);
        given(currentUser.getUserRoles()).willReturn(Set.of(userRoleMapping));
        given(userRoleMapping.getRole()).willReturn(iamUserRole);
        given(iamUserRole.getRoleName()).willReturn("junior-registry-administrator");

        EmailChangeDTO emailChangeDTO = EmailChangeDTO.builder()
            .newEmail(newEmail)
            .urid(otherUserUrid)
            .build();

        given(userService.getCurrentUser()).willReturn(currentUser);

        // when
        IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> changeEmailService.requestEmailChange(emailChangeDTO));

        // then
        assertEquals("Only a Senior Admin can request email update for someone else.", exception.getMessage());

    }
    
    
    @Test
    void openEmailChangeTask() throws JsonProcessingException {
        // given
        String currentUserUrid = "urid";
        String otherUserUrid = "otherUserUrid";
        String oldEmail = "test@test.old";
        String newEmail = "test@test.new";

        User currentUser = mock(User.class);
        given(currentUser.getUrid()).willReturn(currentUserUrid);
        given(userService.getUserByUrid(currentUserUrid)).willReturn(currentUser);
        
        User otherUser = mock(User.class);

        EmailChangeDTO emailChangeDTO = EmailChangeDTO.builder()
            .oldEmail(oldEmail)
            .newEmail(newEmail)
            .urid(otherUserUrid)
            .requesterUrid(currentUserUrid)
            .build();
        
        given(userService.getUserByUrid(otherUserUrid)).willReturn(otherUser);
        given(mapper.convertToJson(emailChangeDTO)).willReturn("{\"urid\":\"otherUserUrid\",\\\"newEmail\\\":\\\"test@test.new\\\",\\\"oldEmail\\\":\\\"test@test.old\\\",\"requesterUrid\":\"urid\"}");

        // when
        Long requestId = changeEmailService.openEmailChangeTask(emailChangeDTO);

        //then
        assertNotNull(requestId);
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        then(taskRepository).should(times(1)).save(taskCaptor.capture());
        //Task initiator
        assertEquals(currentUserUrid, taskCaptor.getValue().getInitiatedBy().getUrid());
        assertEquals(otherUser, taskCaptor.getValue().getUser());
        assertEquals(mapper.convertToJson(emailChangeDTO), taskCaptor.getValue().getDifference());

        ArgumentCaptor<DomainEvent<DomainObject>> domainEventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        then(eventService).should(times(1)).publishEvent(domainEventCaptor.capture());
        assertEquals(currentUserUrid, domainEventCaptor.getValue().getWho());
    }    
}
