package gov.uk.ets.registry.api.user;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsTaskService;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.UserDetailsUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import gov.uk.ets.registry.api.user.service.UserDetailsUpdateTaskService;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserDetailsUpdateTaskServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private UserStatusService userStateService;
    @Mock
    private RequestedDocsTaskService requestedDocsTaskService;
    @Mock
    private Mapper mapper;

    UserDetailsUpdateTaskService userDetailsUpdateTaskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        userDetailsUpdateTaskService =
            new UserDetailsUpdateTaskService(userService, userStateService, requestedDocsTaskService, mapper);
    }

    @DisplayName("Test deserialization")
    @Test
    void deserializeTaskDifferenceTest() throws JsonProcessingException {
        String diff = "{\"firstName\":\"TestName\",\"birthDate\":{\"day\":10,\"month\":1,\"year\":1951},\"workBuildingAndStreet\":\"Street\"}";
        UserDetailsDTO dto =
            new ObjectMapper().readValue(diff, UserDetailsDTO.class);
        Assertions.assertEquals("TestName", dto.getFirstName());
        Assertions.assertEquals(10, dto.getBirthDate().getDay());
        Assertions.assertEquals(1, dto.getBirthDate().getMonth());
        Assertions.assertEquals(1951, dto.getBirthDate().getYear());
        Assertions.assertEquals("Street", dto.getWorkBuildingAndStreet());
    }

    @DisplayName("Test approve")
    @Test
    void testApprove() throws JsonProcessingException {
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.USER_DETAILS_UPDATE_REQUEST);
        UserDetailsUpdateTaskDetailsDTO dto = new UserDetailsUpdateTaskDetailsDTO(taskDetailsDTO);
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUserId("UK123456789");
        dto.setCurrent(userDetailsDTO);
        userDetailsUpdateTaskService.complete(dto, TaskOutcome.APPROVED, "");

        ArgumentCaptor<UserDetailsUpdateTaskDetailsDTO> captor = ArgumentCaptor.forClass(UserDetailsUpdateTaskDetailsDTO.class);
        then(userService).should(times(1)).updateUserDetails(captor.capture());

    }

    @DisplayName("Test reject")
    @Test
    void testReject() throws JsonProcessingException {
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.USER_DETAILS_UPDATE_REQUEST);
        UserDetailsUpdateTaskDetailsDTO dto = new UserDetailsUpdateTaskDetailsDTO(taskDetailsDTO);

        userDetailsUpdateTaskService.complete(dto, TaskOutcome.REJECTED, "");

        ArgumentCaptor<UserDetailsUpdateTaskDetailsDTO> captor = ArgumentCaptor.forClass(UserDetailsUpdateTaskDetailsDTO.class);
        then(userService).should(times(0)).updateUserDetails(captor.capture());

    }
}
