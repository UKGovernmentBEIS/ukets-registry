package gov.uk.ets.registry.api.file.upload.services;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.auditevent.repository.DomainEventEntityRepository;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.reference.domain.ReferenceFile;
import gov.uk.ets.registry.api.file.reference.domain.type.ReferenceFileType;
import gov.uk.ets.registry.api.file.reference.service.ReferenceFileService;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsService;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.RequestDocumentUploadTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RequestDocumentUploadTaskServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RequestedDocsService requestedDocsService;
    @Mock
    private ReferenceFileService referenceFileService;
    @Mock
    private UploadedFilesRepository uploadedFilesRepository;
    @Mock
    private Mapper mapper;
    @Mock
    private DomainEventEntityRepository domainEventEntityRepository;

    RequestDocumentUploadTaskService requestDocumentUploadTaskService;

    ObjectMapper jacksonMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        requestDocumentUploadTaskService =
            new RequestDocumentUploadTaskService(userService, requestedDocsService, referenceFileService,
                uploadedFilesRepository, mapper, domainEventEntityRepository);
    }

    @DisplayName("Retrieve requested documents update values for Account Holder successfully.")
    @Test
    void deserializeAccountHolderRequestedDocumentsUpdateTest() throws JsonProcessingException {
        String diff =
            "{\"accountHolderIdentifier\": 1000036,\"userUrid\": null,\"documentNames\": [\"Proof of identity\",\"Proof of residence\"]}";
        RequestDocumentsTaskDifference dto =
            new ObjectMapper().readValue(diff, RequestDocumentsTaskDifference.class);
        Assertions.assertEquals(1000036L, dto.getAccountHolderIdentifier());
        Assertions.assertNull(dto.getUserUrid());
        Assertions.assertEquals(2, dto.getDocumentNames().size());
        Assertions.assertEquals("Proof of identity", dto.getDocumentNames().get(0));
        Assertions.assertEquals("Proof of residence", dto.getDocumentNames().get(1));
    }

    @DisplayName("Retrieve requested documents update values for User successfully.")
    @Test
    void deserializeUserRequestedDocumentsUpdateTest() throws JsonProcessingException {
        String diff =
            "{\"accountHolderIdentifier\": null,\"userUrid\": \"UK12345\",\"documentNames\": [\"Bank account details\",\"Proof of identity\",\"Proof of residence\"]}";
        RequestDocumentsTaskDifference dto =
            new ObjectMapper().readValue(diff, RequestDocumentsTaskDifference.class);
        Assertions.assertEquals("UK12345", dto.getUserUrid());
        Assertions.assertNull(dto.getAccountHolderIdentifier());
        Assertions.assertEquals(3, dto.getDocumentNames().size());
        Assertions.assertEquals("Bank account details", dto.getDocumentNames().get(0));
        Assertions.assertEquals("Proof of identity", dto.getDocumentNames().get(1));
        Assertions.assertEquals("Proof of residence", dto.getDocumentNames().get(2));
    }

    @DisplayName("Retrieve requested documents update values for User successfully.")
    @Test
    void getDetailsTest() throws JsonProcessingException {
        TaskDetailsDTO taskDetails = new TaskDetailsDTO();
        taskDetails.setAccountNumber("10000050");
        taskDetails.setDifference(
            "{\"accountHolderIdentifier\":1000008,\"userUrid\":\"UK694094547713\",\"documentNames\":[\"name1\",\"name2\",\"name3\"],\"documentIds\":[83,84,85],\"comment\":\"test comment\"}");
        List<ReferenceFile> referenceFiles = new ArrayList<>();
        taskDetails.setTaskType(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
        when(referenceFileService.getReferenceFileByType(ReferenceFileType.TASK_TEMPLATE_FILE))
            .thenReturn(referenceFiles);
        User user = new User();
        when(userService.getUserByUrid("UK694094547713"))
            .thenReturn(user);
        when(mapper.convertToPojo(taskDetails.getDifference(), RequestDocumentsTaskDifference.class)).thenReturn(
            jacksonMapper.readValue(taskDetails.getDifference(), RequestDocumentsTaskDifference.class));

        RequestDocumentUploadTaskDetailsDTO result = requestDocumentUploadTaskService.getDetails(taskDetails);
        Assertions.assertEquals("UK694094547713", result.getUserUrid());
        Assertions.assertEquals("name1", result.getDocumentNames().get(0));
        Assertions.assertEquals("name2", result.getDocumentNames().get(1));
        Assertions.assertEquals("name3", result.getDocumentNames().get(2));
        Assertions.assertEquals(83L, result.getDocumentIds().iterator().next());
        Assertions.assertEquals("test comment", result.getComment());
    }
}
