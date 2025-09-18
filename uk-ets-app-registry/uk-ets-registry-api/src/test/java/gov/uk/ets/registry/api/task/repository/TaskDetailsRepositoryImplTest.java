package gov.uk.ets.registry.api.task.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import gov.uk.ets.registry.api.common.ConversionServiceImpl;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.user.domain.User;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
class TaskDetailsRepositoryImplTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TaskRepository taskDetailsRepository;

    Long requestId = 123456L;
    RequestType requestType = RequestType.PRINT_ENROLMENT_LETTER_REQUEST;
    String initiatorFirstName = "Bruce";
    String initiatorLastName = "Banner";
    Date initiatedDate = new Date();
    String claimantFirstName = "Peter";
    String claimantLastName = "Parker";
    String claimantDisclosedName = "Peter Parker";
    Date claimedDate = new Date(new Date().getTime() + 100000);
    String completedByFirstName = "Tony";
    String completedByLastName = "Stark";
    Date completedDate = new Date(new Date().getTime() + 200000);
    RequestStateEnum status = RequestStateEnum.SUBMITTED_NOT_YET_APPROVED;
    String referredUserFirstName = "Thor";
    String referredUserLastName = "Odinson";
    byte[] file = "This is a file".getBytes();

    Long requestId2 = 1234567L;
    RequestType requestType2 = RequestType.ACCOUNT_OPENING_REQUEST;

    Long requestId3 = 12345678L;
    RequestType requestType3 = RequestType.AH_REQUESTED_DOCUMENT_UPLOAD;

    Long requestId4 = 123456789L;

    @BeforeEach
    public void setUp() {
        createTask(requestId, requestType, initiatorFirstName, initiatorLastName, initiatedDate,
                claimantFirstName, claimantLastName, claimedDate, completedByFirstName, completedByLastName, completedDate,
                status, referredUserFirstName, referredUserLastName, file, null);

        Task parentTask = createTask(requestId2, requestType2, initiatorFirstName, initiatorLastName, initiatedDate,
                claimantFirstName, claimantLastName, claimedDate, completedByFirstName, completedByLastName, null,
                status, referredUserFirstName, referredUserLastName, null, null);

        createTask(requestId3, requestType3, initiatorFirstName, initiatorLastName, initiatedDate,
                claimantFirstName, claimantLastName, claimedDate, completedByFirstName, completedByLastName, null,
                status, referredUserFirstName, referredUserLastName, null, parentTask);

        createTask(requestId4, requestType3, initiatorFirstName, initiatorLastName, initiatedDate,
                claimantFirstName, claimantLastName, claimedDate, completedByFirstName, completedByLastName, null,
                status, referredUserFirstName, referredUserLastName, null, parentTask);
    }

    @Test
    void test_get_task_details() {
        TaskDetailsDTO taskDetailsDTO = taskDetailsRepository.getTaskDetails(123456L);
        assertEquals(taskDetailsDTO.getRequestId(), requestId);
        assertEquals(taskDetailsDTO.getTaskType(), requestType);
        assertEquals(taskDetailsDTO.getInitiatorName(), Utils.concat(" ", initiatorFirstName, initiatorLastName));
        assertEquals(taskDetailsDTO.getClaimantName(), claimantDisclosedName);
        assertEquals(TaskStatus.CLAIMED, taskDetailsDTO.getTaskStatus());
        assertEquals(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED, taskDetailsDTO.getRequestStatus());
        assertTrue(initiatedDate.equals(taskDetailsDTO.getInitiatedDate()));
        assertTrue(claimedDate.equals(taskDetailsDTO.getClaimedDate()));
        assertEquals(taskDetailsDTO.getReferredUserFirstName(), referredUserFirstName);
        assertEquals(taskDetailsDTO.getReferredUserLastName(), referredUserLastName);
        assertEquals(taskDetailsDTO.getFileName(), "registry_activation_code_" + initiatedDate.getTime() + ".pdf");
        assertEquals(taskDetailsDTO.getFileSize(), new ConversionServiceImpl().convertByteAmountToHumanReadable(file.length));
    }

    @Test
    void test_get_sub_task_details() {
        List<TaskDetailsDTO> taskDetailsDTO = taskDetailsRepository.getSubTaskDetails(1234567L);
        assertTrue(taskDetailsDTO.stream().map(TaskDetailsDTO::getRequestId).collect(Collectors.toList()).contains(requestId3));
        assertTrue(taskDetailsDTO.stream().map(TaskDetailsDTO::getRequestId).collect(Collectors.toList()).contains(requestId4));
    }


    private Task createTask(Long requestId, RequestType requestType, String initiatorFirstName, String initiatorLastName,
                            Date initiatedDate, String claimantFirstName, String claimantLastName, Date claimedDate,
                            String completedByFirstName, String completedByLastName, Date completedDate,
                            RequestStateEnum status, String referredUserFirstName, String referredUserLastName,
                            byte[] file, Task parentTask){
        Task task = new Task();
        task.setRequestId(requestId);
        task.setType(requestType);

        User initiator = new User();
        initiator.setFirstName(initiatorFirstName);
        initiator.setLastName(initiatorLastName);
        entityManager.persist(initiator);

        task.setInitiatedBy(initiator);
        task.setInitiatedDate(initiatedDate);

        User claimant = new User();
        claimant.setFirstName(claimantFirstName);
        claimant.setLastName(claimantLastName);
        claimant.setDisclosedName(claimantFirstName + " " + claimantLastName);
        entityManager.persist(claimant);

        task.setClaimedBy(claimant);
        task.setClaimedDate(claimedDate);

        User completer = new User();
        completer.setFirstName(completedByFirstName);
        completer.setLastName(completedByLastName);
        entityManager.persist(completer);

        task.setCompletedBy(completer);
        task.setCompletedDate(completedDate);

        task.setStatus(status);

        User referredUser = new User();
        referredUser.setFirstName(referredUserFirstName);
        referredUser.setLastName(referredUserLastName);
        entityManager.persist(referredUser);

        task.setUser(referredUser);

        task.setFile(file);
        task.setParentTask(parentTask);

        entityManager.persist(task);

        return task;
    }
}
