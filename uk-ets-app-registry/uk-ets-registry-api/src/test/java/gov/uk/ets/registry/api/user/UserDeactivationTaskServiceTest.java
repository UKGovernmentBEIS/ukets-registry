package gov.uk.ets.registry.api.user;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.UserDeactivationTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.service.UserDeactivationTaskService;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserDeactivationTaskServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private AuthorizedRepresentativeService authorizedRepresentativeService;
    @Mock
    private AccountAccessRepository accountAccessRepository;
    @Mock
    private EventService eventService;
    @Mock
    private Mapper mapper;

    UserDeactivationTaskService userDeactivationTaskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userDeactivationTaskService =
            new UserDeactivationTaskService(userService, authorizedRepresentativeService, eventService,
                accountAccessRepository, mapper);
    }

    @DisplayName("Test deserialization")
    @Test
    void deserializeTaskDifferenceTest() throws JsonProcessingException {
        String diff = "{\"userDetails\":{\"username\":\"test@test.com\",\"email\":\"test@test.com\","
                + "\"attributes\":{\"memorablePhrase\":[\"testPhrase\"],\"urid\":[\"UK330013308786\"]}},"
                + "\"enrolmentKeyDetails\":{\"urid\":\"UK330013308786\",\"enrolmentKey\":null,\"enrolmentKeyDateCreated\":null,"
                + "\"enrolmentKeyDateExpired\":null},\"deactivationComment\":\"test comment\"}}";
        UserDeactivationDTO dto =
            new ObjectMapper().readValue(diff, UserDeactivationDTO.class);
        Assertions.assertEquals("test@test.com", dto.getUserDetails().getUsername());
        Assertions.assertEquals("UK330013308786", dto.getEnrolmentKeyDetails().getUrid());
        Assertions.assertEquals("test comment", dto.getDeactivationComment());
    }
    

    @DisplayName("Test approve")
    @Test
    void testApprove() throws JsonProcessingException {
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.USER_DEACTIVATION_REQUEST);
        UserDeactivationTaskDetailsDTO dto = new UserDeactivationTaskDetailsDTO(taskDetailsDTO);
        UserDeactivationDTO deactivationDto = new UserDeactivationDTO();
        EnrolmentKeyDTO enrolmentKey = new EnrolmentKeyDTO("urid", "REGISTRATION_CODE", new Date());
        deactivationDto.setEnrolmentKeyDetails(enrolmentKey);
        dto.setChanged(deactivationDto);
        dto.setReferredUserURID("urid");
        dto.setClaimantURID("claimantUrid");

        userDeactivationTaskService.complete(dto, TaskOutcome.APPROVED, "");

        then(userService).should(times(1)).deactivateUser("urid", "claimantUrid");
        then(authorizedRepresentativeService).should(times(1)).getARsByUser("urid");

    }

    @DisplayName("Test reject")
    @Test
    void testReject() throws JsonProcessingException {
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.USER_DEACTIVATION_REQUEST);
        UserDeactivationTaskDetailsDTO dto = new UserDeactivationTaskDetailsDTO(taskDetailsDTO);
        UserDeactivationDTO deactivationDto = new UserDeactivationDTO();
        EnrolmentKeyDTO enrolmentKey = new EnrolmentKeyDTO("urid", "REGISTRATION_CODE", new Date());
        deactivationDto.setEnrolmentKeyDetails(enrolmentKey);
        UserRepresentation userRepresentation = new UserRepresentation();
        Map<String, List<String>> attributes = new HashMap<String, List<String>>();
        attributes.put("state", Collections.singletonList("DEACTIVATION_PENDING"));
        userRepresentation.setAttributes(attributes);
        userRepresentation.setFirstName("deactivated1");
        userRepresentation.setLastName("user");
        userRepresentation.setEmail("deactivated1@mailinator.com");
        KeycloakUser user = new KeycloakUser(userRepresentation);
        deactivationDto.setUserDetails(user);
        dto.setChanged(deactivationDto);
        dto.setReferredUserURID("urid");
        dto.setClaimantURID("claimantUrid");

        userDeactivationTaskService.complete(dto, TaskOutcome.REJECTED, "");

        then(userService).should(times(0)).deactivateUser("urid", "claimantUrid");
        then(userService).should(times(1)).revertDeactivationPending("urid", "claimantUrid");

    }
}
