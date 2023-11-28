package gov.uk.ets.registry.api.user.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.web.model.EmailChangeTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailChangeTaskServiceTest {

    @Mock
    EmailChangeChecker emailChangeChecker;

    @Mock
    UserAdministrationService userAdministrationService;

    @Mock
    UserService userService;

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    Mapper mapper;

    EmailChangeTaskService emailChangeTaskService;

    User user1;

    @BeforeEach
    void setUp() {
        emailChangeTaskService = new EmailChangeTaskService(userAdministrationService, userService,userRepository,
            emailChangeChecker, taskRepository, mapper);

        user1 = new User();
        user1.setIamIdentifier("b4d410d6-c8d3-4274-ba1e-7e46038f01db");
        user1.setUrid("UK8536961392");
        user1.setState(UserStatus.ENROLLED);
        user1.setEmail("oldemail@mail.com");
    }

    @Test
    void appliesFor() {
        // when
        Set<RequestType> set = emailChangeTaskService.appliesFor();

        // then
        assertEquals(Set.of(RequestType.REQUESTED_EMAIL_CHANGE), set);
    }

    @Test
    void getDetails() {
        // given
        String requesterUrid = "UK8844";
        String urid = "UK213213";
        String newEmail = "newEmail@trasys.gr";
        String oldEmail = "oldEmail@trasys.gr";
        String userFirstName = "first name";
        String userLastName = "last name";
        
        EmailChangeDTO emailChange = EmailChangeDTO
            .builder()
            .urid(urid)
            .requesterUrid(requesterUrid)
            .oldEmail(oldEmail)
            .newEmail(newEmail)
            .build();
        String requestJSON = "{\"urid\":\"UK237778720220\",\"newEmail\":\"newEmail@trasys.gr\",\"oldEmail\":\"oldEmail@trasys.gr\",\"requesterUrid\":\"UK802061511788\"}";
        given(mapper.convertToJson(emailChange)).willReturn(requestJSON);
        
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setInitiatorUrid(requesterUrid);
        taskDetailsDTO.setDifference(mapper.convertToJson(emailChange));
        UserDTO user = new UserDTO();
        user.setUrid(urid);
        user.setFirstName(userFirstName);
        user.setLastName(userLastName);
        user.setKeycloakId("213213123");
        UserDTO requester = new UserDTO();
        requester.setUrid(requesterUrid);
        requester.setFirstName("admin first name");
        requester.setLastName("admin last name");
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userFirstName);
        userRepresentation.setLastName(userLastName);
        given(userAdministrationService.findByEmail(oldEmail)).willReturn(Optional.of(userRepresentation));

        given(mapper.convertToPojo(requestJSON,EmailChangeDTO.class)).willReturn(emailChange);
       

        //when
        EmailChangeTaskDetailsDTO dto = emailChangeTaskService.getDetails(taskDetailsDTO);

        //then
        assertEquals(user.getFirstName(), dto.getUserFirstName());
        assertEquals(user.getLastName(), dto.getUserLastName());
        assertEquals(oldEmail, dto.getUserCurrentEmail());
        assertEquals(newEmail, dto.getUserNewEmail());
        assertEquals(user.getUrid(), dto.getUserUrid());
        assertEquals(requesterUrid, dto.getInitiatorUrid());
    }
    
    @Test
    void getDetailsWithDiffNewEmail() {
        // given
        String initiatorUrid = "UK8844";
        String initiatorKeycloakId = "f8033fa4-8cca-4a6a-a477-027ed61ff1e0";
        String urid = "UK213213";
        String newEmail = "new_email@email.com";
        String oldEmail = "test@test.com";
        String userFirstName = "first name";
        String userLastName = "last name";
        
        EmailChangeDTO emailChange = EmailChangeDTO
            .builder()
            .urid(urid)
            .requesterUrid(initiatorUrid)
            .oldEmail(oldEmail)
            .newEmail(newEmail)
            .build();
        
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setInitiatorUrid(initiatorUrid);
        taskDetailsDTO.setDifference(newEmail);
        UserDTO user = new UserDTO();
        user.setUrid(urid);
        user.setFirstName(userFirstName);
        user.setLastName(userLastName);
        user.setKeycloakId("213213123");
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(userFirstName);
        userRepresentation.setLastName(userLastName);
        given(userAdministrationService.findByEmail(oldEmail)).willReturn(Optional.of(userRepresentation));
        
        UserDTO initiator = new UserDTO();
        initiator.setUrid(initiatorUrid);
        initiator.setFirstName("admin first name");
        initiator.setLastName("admin last name");
        initiator.setKeycloakId(initiatorKeycloakId);
        UserRepresentation initiatorRepresentation = new UserRepresentation();
        initiatorRepresentation.setFirstName(userFirstName);
        initiatorRepresentation.setLastName(userLastName);
        initiatorRepresentation.setEmail(oldEmail);
        
        given(userService.getUser(initiatorUrid)).willReturn(initiator);
        given(userAdministrationService.findByIamId(initiatorKeycloakId)).willReturn(initiatorRepresentation);

        given(mapper.convertToPojo(newEmail,EmailChangeDTO.class)).willCallRealMethod();      

        //when
        EmailChangeTaskDetailsDTO dto = emailChangeTaskService.getDetails(taskDetailsDTO);

        //then
        assertEquals(user.getFirstName(), dto.getUserFirstName());
        assertEquals(user.getLastName(), dto.getUserLastName());
        assertEquals(oldEmail, dto.getUserCurrentEmail());
        assertEquals(newEmail, dto.getUserNewEmail());
        assertEquals(initiatorUrid, dto.getUserUrid());
        assertEquals(initiatorUrid, dto.getInitiatorUrid());
    }

    @Test
    void approve() {
        //given
        EmailChangeTaskDetailsDTO dto = new EmailChangeTaskDetailsDTO();
        dto.setRequestId(1000L);
        dto.setUserCurrentEmail("oldEmail@test.test");
        dto.setUserNewEmail("test@test.test");
        dto.setUserUrid("231123213");

        Task task = new Task();
        given(taskRepository.findByRequestId(any())).willReturn(task);

        // Create a new User object that represents the returned user with the new email.
        User returnedUser = new User();
        returnedUser.setIamIdentifier("b4d410d6-c8d3-4274-ba1e-7e46038f01db");
        returnedUser.setUrid(dto.getUserUrid());
        returnedUser.setState(UserStatus.ENROLLED);
        returnedUser.setEmail("oldmail@mail.com");

        given(userRepository.findByUrid(dto.getUserUrid())).willReturn(returnedUser);

        //when
        emailChangeTaskService.complete(dto, TaskOutcome.APPROVED, "a comment");

        // then
        verify(userAdministrationService, times(1))
                .updateUserEmail(eq(dto.getUserCurrentEmail()), eq(dto.getUserNewEmail()));

        verify(userRepository, times(1)).findByUrid(dto.getUserUrid());

        User user = userRepository.findByUrid(dto.getUserUrid());
        assertEquals(user.getEmail(), dto.getUserNewEmail());
    }
}
