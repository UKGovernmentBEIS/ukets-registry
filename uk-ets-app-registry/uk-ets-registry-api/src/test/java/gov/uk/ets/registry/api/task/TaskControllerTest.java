package gov.uk.ets.registry.api.task;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.task.service.TaskActionException;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.service.TaskService;
import gov.uk.ets.registry.api.task.web.mappers.TaskSearchPageableMapper;
import gov.uk.ets.registry.api.task.web.mappers.TaskSearchResultDTOMapper;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class TaskControllerTest {
    private MockMvc mockMvc;

    private TaskController controller;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskSearchResultDTOMapper dtoMapper;

    @Mock
    private TaskSearchPageableMapper pageableMapper;

    @Mock
    private UserAdministrationService userAdministrationService;

    @Mock
    private AccountAccessService accountAccessService;

    @Mock
    TaskEventService taskEventService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller =
            new TaskController(taskService, dtoMapper, pageableMapper,
                taskEventService, userAdministrationService, accountAccessService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new TaskControllerAdvice())
            .build();
    }

    @Test
    public void testClaimSuccessReponse() throws Exception {
        mockMvc.perform(post("/api-registry/tasks.claim")
                .param("requestIds", "1,2,3")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.updated", is(3)));
    }

    @Test
    public void testClaimErrorResponse() throws Exception {
        Mockito.doThrow(buildTestingError()).when(taskService).claim(any(), any());

        mockMvc.perform(post("/api-registry/tasks.claim")
                .param("requestIds", "1,2,3")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetails", hasSize(1)))
            .andExpect(jsonPath("$.errorDetails[0].code", is("test-code")))
            .andExpect(jsonPath("$.errorDetails[0].urid", is("test-urid")))
            .andExpect(jsonPath("$.errorDetails[0].message", is("test-message")))
            .andExpect(jsonPath("$.errorDetails[0].identifier", is("1")));
    }

    @Test
    public void testAssignErrorResponse() throws Exception {
        Mockito.doThrow(buildTestingError()).when(taskService).assign(any(), any(), any());

        mockMvc.perform(post("/api-registry/tasks.assign")
                .param("requestIds", "1,2,3")
                .param("urid", "test-urid")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorDetails", hasSize(1)))
            .andExpect(jsonPath("$.errorDetails[0].code", is("test-code")))
            .andExpect(jsonPath("$.errorDetails[0].urid", is("test-urid")))
            .andExpect(jsonPath("$.errorDetails[0].message", is("test-message")))
            .andExpect(jsonPath("$.errorDetails[0].identifier", is("1")));
    }

    @Test
    public void testAssignTasksToUserSuccessResponse() throws Exception {
        mockMvc.perform(post("/api-registry/tasks.assign")
                .param("requestIds", "1,2,3")
                .param("urid", "525412")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.updated", is(3)));
    }

    @Test
    public void testGetCandidateAssignees() throws Exception {
        mockMvc.perform(get("/api-registry/tasks.get.candidate-assignees")
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }

    @Test
    public void test_getTaskHistoryResponse() throws Exception {
        String comment = "test comment";
        EventType eventType = EventType.TASK_COMMENT;
        String creatorType = "user";
        String creator = "Senior Registry Administrator";
        Long requestId = 1000L;
        Calendar calendar = new GregorianCalendar(2019, Calendar.DECEMBER, 12);
        Date generatedOn = calendar.getTime();
        AuditEventDTO dto = new AuditEventDTO(String.valueOf(requestId), EventType.TASK_COMMENT.name(), comment,
            creator, creatorType, generatedOn);

        Mockito.when(taskEventService.getTaskHistory(any())).thenReturn(Stream.of(dto).collect(
            Collectors.toList()));

        mockMvc.perform(get("/api-registry/tasks.get.history")
                .param("requestId", "1000")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].creationDate", is(generatedOn.getTime())))
            .andExpect(jsonPath("$[0].creator", is(creator)))
            .andExpect(jsonPath("$[0].creatorType", is(creatorType)))
            .andExpect(jsonPath("$[0].domainAction", is(eventType.name())))
            .andExpect(jsonPath("$[0].domainId", is(String.valueOf(1000))))
            .andExpect(jsonPath("$[0].description", is(comment)));
    }

    @Test
    void test_addCommentToHistoryResponse() throws Exception {
        Integer requestId = 1000;
        mockMvc.perform(post("/api-registry/tasks.get.history")
                .param("comment", "a comment")
                .param("requestId", String.valueOf(requestId))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requestId", is(requestId)));
    }

    @Test
    void test_getTaskRequestDocumentFile() throws Exception {

        UploadedFile file = new UploadedFile();
        byte[] test = " Any String you want".getBytes();
        file.setFileData(test);
        file.setId(1L);
        file.setFileName("test");
        Mockito.when(taskService.getRequestedTaskFile(any())).thenReturn(file);

        mockMvc.perform(get("/api-registry/tasks.get.task-file")
                .param("taskType", "AH_REQUESTED_DOCUMENT_UPLOAD")
                .param("taskRequestId", "1")
                .param("fileId", "1")
                .accept(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(status().isOk());
    }

    private TaskActionException buildTestingError() {
        TaskActionException exception = new TaskActionException();
        exception.addError(TaskActionError.builder()
            .code("test-code")
            .requestId(1L)
            .urid("test-urid")
            .message("test-message")
            .build());
        return exception;
    }
}
