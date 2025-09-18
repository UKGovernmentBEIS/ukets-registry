package gov.uk.ets.registry.api.file.upload.requesteddocs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.file.upload.requesteddocs.model.DocumentsRequestDTO;
import gov.uk.ets.registry.api.file.upload.requesteddocs.model.DocumentsRequestType;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.service.TaskService;
import gov.uk.ets.registry.api.task.web.model.AccountHolderUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.AccountOpeningTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Log4j2
class RequestDocumentsWizardServiceTest {

    @Mock
    private EventService eventService;
    @Mock
    private PersistenceService persistenceService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskService taskService;
    @Mock
    private UserService userService;
    @Mock
    private AccountService accountService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountHolderService accountHolderService;
    @Mock
    private TaskEventService taskEventService;
    @Mock
    private Mapper mapper;

    private RequestDocumentsWizardService requestDocumentsWizardService;

    private ObjectMapper jacksonMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        requestDocumentsWizardService =
            new RequestDocumentsWizardService(eventService, persistenceService, taskRepository, taskService,
                userService, accountService, userRepository, accountHolderService, taskEventService, mapper);
    }

    @DisplayName("Test submit Account holder documents request with account holder Identifier")
    @Test
    void test_submitDocumentsRequest() throws JsonProcessingException {
        List<String> testDocuments = List.of("Document 1", "Document 2");
        DocumentsRequestDTO documentsRequest = new DocumentsRequestDTO();
        documentsRequest.setType(DocumentsRequestType.ACCOUNT_HOLDER);
        documentsRequest.setAccountHolderIdentifier(100001L);
        documentsRequest.setRecipientUrid("UK12345");
        documentsRequest.setDocumentNames(testDocuments);
        documentsRequest.setDeadline(Date.from(Instant.now().plus(28, ChronoUnit.DAYS)));
        //comment is not set during the submittion.

        given(userRepository.findByUrid(documentsRequest.getRecipientUrid())).willReturn(getTaskAssignee());

        Long generatedTaskIdentifier = 10003L;
        given(persistenceService.getNextBusinessIdentifier(Task.class)).willReturn(generatedTaskIdentifier);

        given(userService.getCurrentUser()).willReturn(getCurrentUser());

        given(taskRepository.findByRequestId(documentsRequest.getParentRequestId())).willReturn(null);


        AccountHolderDTO accountHolder = new AccountHolderDTO();
        DetailsDTO details = new DetailsDTO();
        String expectedAccountHolderName = "Account Holder Name!";
        details.setName(expectedAccountHolderName);
        accountHolder.setDetails(details);
        given(accountHolderService.getAccountHolder(documentsRequest.getAccountHolderIdentifier()))
            .willReturn(accountHolder);

        Account account = new Account();
        account.setIdentifier(100000000L);
        given(accountService.getAccountFullIdentifier(documentsRequest.getAccountFullIdentifier()))
            .willReturn(account);

        RequestDocumentsTaskDifference documentsTaskDifference = new RequestDocumentsTaskDifference();
        documentsTaskDifference.setAccountHolderIdentifier(documentsRequest.getAccountHolderIdentifier());
        documentsTaskDifference.setDocumentNames(testDocuments);
        Map<String, Long> fileNameIdMap = new HashMap<>();
        testDocuments.forEach(d -> fileNameIdMap.put(d, null));
        documentsTaskDifference
            .setUploadedFileNameIdMap(fileNameIdMap);
        documentsTaskDifference.setAccountHolderName(expectedAccountHolderName);

        // when
        when(mapper.convertToJson(documentsTaskDifference)).thenReturn(jacksonMapper.writeValueAsString(documentsTaskDifference));
        Long taskRequestId = requestDocumentsWizardService.submitDocumentsRequest(documentsRequest);

        // then

        // Verify event creation
        then(eventService).should(times(1)).createAndPublishEvent("100000000",
            getCurrentUser().getUrid(), expectedAccountHolderName, EventType.ACCOUNT_TASK_REQUESTED,
            "Documents requested for Account Holder.");

        Task expectedTask = new Task();
        expectedTask.setRequestId(generatedTaskIdentifier);
        expectedTask.setType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD);
        expectedTask.setInitiatedBy(getCurrentUser());
        expectedTask.setUser(getTaskAssignee());
        expectedTask.setDifference(jacksonMapper.writeValueAsString(documentsTaskDifference));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        then(persistenceService).should(times(1)).save(captor.capture());
        Task actualTask = captor.getValue();
        log.info(actualTask.toString());
        assertEquals(expectedTask.getRequestId(), actualTask.getRequestId());
        assertEquals(expectedTask.getType(), actualTask.getType());
        assertEquals(expectedTask.getInitiatedBy(), actualTask.getInitiatedBy());
        assertEquals(expectedTask.getUser(), actualTask.getUser());
        assertEquals(expectedTask.getDifference(), actualTask.getDifference());
        assertEquals(generatedTaskIdentifier, taskRequestId);
    }

    @DisplayName("Test submit Account holder documents request with account holder Identifier under AH details update")
    @Test
    void test_submitDocumentsRequesUnderAccountHolderDetailsUpdate() throws JsonProcessingException {
        List<String> testDocuments = List.of("Document 1", "Document 2");
        DocumentsRequestDTO documentsRequest = new DocumentsRequestDTO();
        documentsRequest.setType(DocumentsRequestType.ACCOUNT_HOLDER);
        documentsRequest.setAccountHolderIdentifier(100001L);
        documentsRequest.setRecipientUrid("UK12345");
        documentsRequest.setDocumentNames(testDocuments);
        documentsRequest.setParentRequestId(1234567890L);
        documentsRequest.setDeadline(Date.from(Instant.now().plus(28, ChronoUnit.DAYS)));
        //comment is not set during the submission.

        given(userRepository.findByUrid(documentsRequest.getRecipientUrid())).willReturn(getTaskAssignee());

        Long generatedTaskIdentifier = 10003L;
        given(persistenceService.getNextBusinessIdentifier(Task.class)).willReturn(generatedTaskIdentifier);

        given(userService.getCurrentUser()).willReturn(getCurrentUser());

        Task parentTask = new Task();
        parentTask.setId(123L);
        parentTask.setRequestId(1234567890L);
        Date parentDeadline = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));
        parentTask.setDeadline(parentDeadline);
        given(taskRepository.findByRequestId(documentsRequest.getParentRequestId())).willReturn(parentTask);

        AccountHolderUpdateTaskDetailsDTO taskDetailsDTO = new AccountHolderUpdateTaskDetailsDTO(new TaskDetailsDTO());
        AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
        String expectedAccountName = "Account Name";
        accountDetailsDTO.setName(expectedAccountName);
        taskDetailsDTO.setAccountDetails(accountDetailsDTO);
        given(taskService.getTaskDetails(documentsRequest.getParentRequestId())).willReturn(taskDetailsDTO);

        AccountHolderDTO accountHolder = new AccountHolderDTO();
        DetailsDTO details = new DetailsDTO();
        String expectedAccountHolderName = "Account Holder Name!";
        details.setName(expectedAccountHolderName);
        accountHolder.setDetails(details);
        given(accountHolderService.getAccountHolder(documentsRequest.getAccountHolderIdentifier()))
            .willReturn(accountHolder);

        RequestDocumentsTaskDifference documentsTaskDifference = new RequestDocumentsTaskDifference();
        documentsTaskDifference.setAccountHolderIdentifier(documentsRequest.getAccountHolderIdentifier());
        documentsTaskDifference.setDocumentNames(testDocuments);
        Map<String, Long> fileNameIdMap = new HashMap<>();
        testDocuments.forEach(d -> fileNameIdMap.put(d, null));
        documentsTaskDifference
            .setUploadedFileNameIdMap(fileNameIdMap);
        documentsTaskDifference.setAccountHolderName(expectedAccountHolderName);
        documentsTaskDifference.setAccountName(expectedAccountName);

        // when
        when(mapper.convertToJson(documentsTaskDifference)).thenReturn(jacksonMapper.writeValueAsString(documentsTaskDifference));
        Long taskRequestId = requestDocumentsWizardService.submitDocumentsRequest(documentsRequest);

        // then

        // Verify event creation
        then(eventService).should(times(1)).createAndPublishEvent(null,
            getCurrentUser().getUrid(), expectedAccountHolderName, EventType.ACCOUNT_TASK_REQUESTED,
            "Documents requested for Account Holder.");

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        then(persistenceService).should(times(1)).save(captor.capture());
        Task actualTask = captor.getValue();
        log.info(actualTask.toString());
        assertEquals(generatedTaskIdentifier, actualTask.getRequestId());
        assertEquals(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD, actualTask.getType());
        assertEquals(getCurrentUser(), actualTask.getInitiatedBy());
        assertEquals(getTaskAssignee(), actualTask.getUser());
        assertEquals(jacksonMapper.writeValueAsString(documentsTaskDifference), actualTask.getDifference());
        assertEquals(generatedTaskIdentifier, taskRequestId);
        assertEquals(parentDeadline, actualTask.getParentTask().getDeadline());
    }


    @DisplayName("Test submit  user documents request")
    @Test
    void testSubmitUserDocumentsRequest() throws JsonProcessingException {
        DocumentsRequestDTO documentsRequest = new DocumentsRequestDTO();
        documentsRequest.setType(DocumentsRequestType.USER);
        List<String> testDocuments = List.of("test");
        documentsRequest.setAccountHolderIdentifier(100001L);
        documentsRequest.setRecipientUrid("UK12345");
        documentsRequest.setDocumentNames(testDocuments);
        documentsRequest.setDeadline(Date.from(Instant.now().plus(28, ChronoUnit.DAYS)));

        RequestDocumentsTaskDifference requestDocumentsTaskDifference = new RequestDocumentsTaskDifference();
        requestDocumentsTaskDifference.setDocumentNames(testDocuments);
        requestDocumentsTaskDifference.setUploadedFileNameIdMap(Collections.singletonMap("test", null));
        requestDocumentsTaskDifference.setUserUrid("UK12345");

        given(userRepository.findByUrid(documentsRequest.getRecipientUrid())).willReturn(getTaskAssignee());

        Long generatedTaskIdentifier = 10003L;
        given(persistenceService.getNextBusinessIdentifier(Task.class)).willReturn(generatedTaskIdentifier);


        given(userService.getCurrentUser()).willReturn(getCurrentUser());

        given(taskRepository.findByRequestId(documentsRequest.getParentRequestId())).willReturn(null);


        UserDTO userDTO = new UserDTO();
        userDTO.setUrid(documentsRequest.getRecipientUrid());
        userDTO.setFirstName("Firstname");
        userDTO.setLastName("Lastname");
        given(userService.getUser(documentsRequest.getRecipientUrid())).willReturn(userDTO);


        //when
        when(mapper.convertToJson(requestDocumentsTaskDifference))
            .thenReturn(jacksonMapper.writeValueAsString(requestDocumentsTaskDifference));
        Long taskRequestId = requestDocumentsWizardService.submitDocumentsRequest(documentsRequest);
        String action = "Documents requested for user.";


        then(eventService).should(times(1)).createAndPublishEvent(documentsRequest.getRecipientUrid(),
            getCurrentUser().getUrid(), "Firstname Lastname", EventType.USER_DOCUMENTS_REQUESTED, action);

        Task expectedTask = new Task();
        expectedTask.setRequestId(generatedTaskIdentifier);
        expectedTask.setType(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
        expectedTask.setInitiatedBy(getCurrentUser());
        expectedTask.setUser(getTaskAssignee());

        RequestDocumentsTaskDifference documentsTaskDifference = new RequestDocumentsTaskDifference();
        documentsTaskDifference.setUserUrid(getTaskAssignee().getUrid());
        documentsTaskDifference.setDocumentNames(testDocuments);
        documentsTaskDifference
            .setUploadedFileNameIdMap(Collections.singletonMap("test", null));

        expectedTask.setDifference(jacksonMapper.writeValueAsString(documentsTaskDifference));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        then(persistenceService).should(times(1)).save(captor.capture());
        Task actualTask = captor.getValue();
        log.info(actualTask.toString());
        assertEquals(expectedTask.getRequestId(), actualTask.getRequestId());
        assertEquals(expectedTask.getType(), actualTask.getType());
        assertEquals(expectedTask.getInitiatedBy(), actualTask.getInitiatedBy());
        assertEquals(expectedTask.getUser(), actualTask.getUser());
        assertEquals(expectedTask.getDifference(), actualTask.getDifference());
        assertEquals(generatedTaskIdentifier, taskRequestId);

    }


    /**
     * https://pmo.trasys.be/jira/browse/UKETS-4769
     * The task has a parent task in this case.
     */
    @DisplayName("Submit Document Request for new Account Holder")
    @Test
    void testSubmitDocumentRequestForNewAccountHolder() throws JsonProcessingException {

        //GIVEN
        DocumentsRequestDTO documentsRequest = new DocumentsRequestDTO();
        List<String> testDocuments = List.of("test");
        documentsRequest.setType(DocumentsRequestType.ACCOUNT_HOLDER);
        documentsRequest.setAccountHolderIdentifier(null);
        documentsRequest.setRecipientUrid("UK12345");
        documentsRequest.setDocumentNames(testDocuments);
        Date deadline = Date.from(Instant.now().plus(28, ChronoUnit.DAYS));
        documentsRequest.setDeadline(deadline);

        RequestDocumentsTaskDifference requestDocumentsTaskDifference = new RequestDocumentsTaskDifference();
        requestDocumentsTaskDifference.setDocumentNames(testDocuments);
        requestDocumentsTaskDifference.setUploadedFileNameIdMap(Collections.singletonMap("test", null));
        requestDocumentsTaskDifference.setAccountHolderName("New Account Holder First Name Last Name");
        requestDocumentsTaskDifference.setAccountName("Nrg");

        given(userRepository.findByUrid(documentsRequest.getRecipientUrid())).willReturn(getTaskAssignee());
        Long generatedTaskIdentifier = 10003L;
        given(persistenceService.getNextBusinessIdentifier(Task.class)).willReturn(generatedTaskIdentifier);
        given(userService.getCurrentUser()).willReturn(getCurrentUser());
        Long parentTaskIdentifier = 1L;
        Task parentTask = new Task();
        parentTask.setId(123L);

        parentTask.setType(RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST);
        parentTask.setRequestId(parentTaskIdentifier);
        given(taskRepository.findByRequestId(documentsRequest.getParentRequestId())).willReturn(parentTask);
        AccountOpeningTaskDetailsDTO accountOpeningWithTransferDetails =
            new AccountOpeningTaskDetailsDTO(new TaskDetailsDTO());
        AccountDTO parentAccountDTO = new AccountDTO();
        AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
        String accountName = "Nrg";
        accountDetailsDTO.setName(accountName);
        parentAccountDTO.setAccountDetails(accountDetailsDTO);
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        accountHolderDTO.setType(AccountHolderType.INDIVIDUAL);
        DetailsDTO detailsDTO = new DetailsDTO();
        String newAccountHolderFirstName = "New Account Holder First Name";
        String newAccountHolderLastName = "Last Name";
        detailsDTO.setFirstName(newAccountHolderFirstName);
        detailsDTO.setLastName(newAccountHolderLastName);
        accountHolderDTO.setDetails(detailsDTO);
        parentAccountDTO
            .setAccountHolder(accountHolderDTO);
        accountOpeningWithTransferDetails.setAccount(parentAccountDTO);
        given(taskService.getTaskDetails(parentTaskIdentifier)).willReturn(accountOpeningWithTransferDetails);


        // WHEN
        when(mapper.convertToJson(requestDocumentsTaskDifference))
            .thenReturn(jacksonMapper.writeValueAsString(requestDocumentsTaskDifference));
        Long taskRequestId = requestDocumentsWizardService.submitDocumentsRequest(documentsRequest);


        // THEN
        //verify task creation and events.


        then(eventService).should(times(1)).createAndPublishEvent(null,
            getCurrentUser().getUrid(), newAccountHolderFirstName + " " + newAccountHolderLastName,
            EventType.ACCOUNT_TASK_REQUESTED,
            "Documents requested for Account Holder.");

        Task expectedTask = new Task();
        expectedTask.setType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD);
        expectedTask.setInitiatedBy(getCurrentUser());
        expectedTask.setRequestId(generatedTaskIdentifier);
        expectedTask.setInitiatedBy(getCurrentUser());
        expectedTask.setUser(getTaskAssignee());
        RequestDocumentsTaskDifference documentsTaskDifference = new RequestDocumentsTaskDifference();
        documentsTaskDifference.setAccountHolderName(newAccountHolderFirstName + " " + newAccountHolderLastName);
        documentsTaskDifference.setAccountName("Nrg");
        documentsTaskDifference.setDocumentNames(testDocuments);
        Map<String, Long> fileNameIdMap = new HashMap<>();
        testDocuments.forEach(d -> fileNameIdMap.put(d, null));
        documentsTaskDifference
            .setUploadedFileNameIdMap(fileNameIdMap);
        expectedTask.setDifference(jacksonMapper.writeValueAsString(documentsTaskDifference));
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        then(persistenceService).should(times(1)).save(captor.capture());
        Task actualTask = captor.getValue();
        log.info(actualTask.toString());
        assertEquals(expectedTask.getRequestId(), actualTask.getRequestId());
        assertEquals(expectedTask.getType(), actualTask.getType());

        assertEquals(expectedTask.getInitiatedBy(), actualTask.getInitiatedBy());
        assertEquals(expectedTask.getUser(), actualTask.getUser());
        assertEquals(expectedTask.getDifference(), actualTask.getDifference());
        assertEquals(generatedTaskIdentifier, taskRequestId);
        assertEquals(deadline, actualTask.getDeadline());
        assertEquals(deadline, actualTask.getParentTask().getDeadline());
    }

    private User getCurrentUser() {
        User currentUser = new User();
        currentUser.setUrid("UK987654");
        currentUser.setFirstName("Registry Senior");
        currentUser.setLastName("Admin");
        return currentUser;
    }

    private User getTaskAssignee() {
        User recipient = new User();
        recipient.setUrid("UK12345");
        recipient.setFirstName("John");
        recipient.setLastName("Recipient For ");
        return recipient;
    }
}


