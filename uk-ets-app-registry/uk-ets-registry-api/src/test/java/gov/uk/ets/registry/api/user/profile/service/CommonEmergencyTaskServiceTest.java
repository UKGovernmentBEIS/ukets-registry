package gov.uk.ets.registry.api.user.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.web.model.TokenTaskDetailsDTO;
import gov.uk.ets.registry.api.user.UserActionError;
import gov.uk.ets.registry.api.user.UserActionException;
import gov.uk.ets.registry.api.user.domain.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonEmergencyTaskServiceTest {

    private static final String TEST_URID = "UK12345678";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_REQUEST_ID = "123456";
    private static final String TEST_FIRST_NAME = "Name";

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskEventService taskEventService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ObjectMapper objectMapper;

    CommonEmergencyTaskService sut;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        sut = new CommonEmergencyTaskService(taskRepository, taskEventService, new Mapper(objectMapper));
    }

    @Test
    void shouldThrowIfPendingTaskExists() {
        given(taskRepository.findPendingTasksByTypeAndUser(RequestType.LOST_TOKEN, TEST_URID))
            .willReturn(List.of(new Task()));
        User user = new User();
        user.setUrid(TEST_URID);

        UserActionException exception =
            assertThrows(UserActionException.class, () -> sut.proposeRequest(user, TEST_EMAIL, RequestType.LOST_TOKEN));
        assertThat(exception.getUserActionError()).isEqualTo(UserActionError.LOST_KEY_TASK_PENDING);
    }

    @Test
    void shouldReturnCorrectRequestId() {
        given(taskRepository.getNextRequestId()).willReturn(Long.parseLong(TEST_REQUEST_ID));
        User user = new User();

        String requestId = sut.proposeRequest(user, TEST_EMAIL, RequestType.LOST_TOKEN);

        assertThat(requestId).isEqualTo(TEST_REQUEST_ID);
    }

    @Test
    void shouldCreateEvent() throws JsonProcessingException {
        User user = new User();
        user.setUrid(TEST_URID);
        user.setFirstName(TEST_FIRST_NAME);

        sut.proposeRequest(user, TEST_EMAIL, RequestType.LOST_TOKEN);

        ArgumentCaptor<Task> taskCapture = ArgumentCaptor.forClass(Task.class);
        ArgumentCaptor<String> uridCapture = ArgumentCaptor.forClass(String.class);
        verify(taskEventService, times(1))
            .createAndPublishTaskAndAccountRequestEvent(taskCapture.capture(), uridCapture.capture());

        Task capturedTask = taskCapture.getValue();
        assertThat(capturedTask.getType()).isEqualTo(RequestType.LOST_TOKEN);
        assertThat(capturedTask.getUser()).isEqualTo(user);
        assertThat(capturedTask.getInitiatedBy()).isEqualTo(user);

        TokenTaskDetailsDTO details = objectMapper.readValue(capturedTask.getDifference(), TokenTaskDetailsDTO.class);
        assertThat(details.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(details.getFirstName()).isEqualTo(TEST_FIRST_NAME);

        assertThat(uridCapture.getValue()).isEqualTo(TEST_URID);

    }

}
