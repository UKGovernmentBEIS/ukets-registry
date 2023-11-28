package gov.uk.ets.registry.api.auditevent.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.auditevent.domain.DomainEventEntity;
import gov.uk.ets.registry.api.auditevent.domain.types.DomainAction;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRoleMapping;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
class DomainEventEntityRepositoryImplTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DomainEventEntityRepository domainEventRepository;

    @BeforeEach
    public void setUp() {
        DomainEventEntity domainEvent = DomainEventEntity.builder().
            domainId("GB100022").
            domainAction("Transaction proposal submitted").
            domainType("gov.uk.ets.registry.api.transaction.domain.Transaction").
            description("Issue AAU or RMU").
            creatorType("user").
            creator("UK873533391953").
            creationDate(new Date()).
            build();

        entityManager.persist(domainEvent);

        DomainEventEntity domainEvent2 = DomainEventEntity.builder().
            domainId("10000050").
            domainAction("Open account task completed.").
            domainType("gov.uk.ets.registry.api.account.domain.Account").
            description("Task requestId 1000014 APPROVED").
            creatorType("user").
            creator("UK873533391953").
            creationDate(new Date()).
            build();

        entityManager.persist(domainEvent2);

        DomainEventEntity domainEvent3 = DomainEventEntity.builder().
            domainId("10000050").
            domainAction("Open account task completed.").
            domainType("gov.uk.ets.registry.api.task.domain.Task").
            description("").
            creatorType("user").
            creator("UK873533391953").
            creationDate(new Date()).
            build();

        entityManager.persist(domainEvent3);

        DomainEventEntity domainEvent4 = DomainEventEntity.builder().
            domainId("10000050").
            domainAction("Document automatically deleted").
            domainType("gov.uk.ets.registry.api.task.domain.Task").
            description("test.pdf with ID 1").
            creatorType("system").
            creator(null).
            creationDate(new Date()).
            build();

        entityManager.persist(domainEvent4);

        DomainEventEntity domainEvent5 = DomainEventEntity.builder().
            domainId("GB100022").
            domainAction("Transaction completed").
            domainType("gov.uk.ets.registry.api.transaction.TransactionService").
            description("").
            creatorType("system").
            creator(null).
            creationDate(new Date()).
            build();

        entityManager.persist(domainEvent5);

        DomainEventEntity domainEvent6 = DomainEventEntity.builder().
            domainId("1000003").
            domainAction("Emissions table request approved").
            domainType("gov.uk.ets.registry.api.file.upload.domain.UploadedFile").
            description("").
            creatorType("user").
            creator("UK873533391953").
            creationDate(new Date()).
            build();

        entityManager.persist(domainEvent6);

        DomainEventEntity domainEvent7 = DomainEventEntity.builder().
            domainId("UK873533391953").
            domainAction("Change user status").
            domainType("gov.uk.ets.registry.api.user.domain.User").
            description("ENROLLED").
            creatorType("user").
            creator("UK873533391953").
            creationDate(new Date()).
            build();

        entityManager.persist(domainEvent7);

        DomainEventEntity domainEvent8 = DomainEventEntity.builder().
            domainId("UK873533391953").
            domainAction("Add User Role").
            domainType("gov.uk.ets.registry.api.user.domain.User").
            description("junior-registry-administrator").
            creatorType("system").
            creationDate(new Date()).
            build();

        entityManager.persist(domainEvent8);

        DomainEventEntity domainEvent9 = DomainEventEntity.builder()
                .domainId("1000092")
                .domainAction(DomainAction.SUBMIT_DOCS_FOR_AH_TASK_ASSIGNED_COMMENT.getAction())
                .domainType("gov.uk.ets.registry.api.user.domain.User")
                .description("Some test reason for assigning this task.")
                .creatorType("")
                .creationDate(new Date())
                .build();

        entityManager.persist(domainEvent9);

        DomainEventEntity domainEvent10 = DomainEventEntity.builder()
                .domainId("1000092")
                .domainAction(DomainAction.SUBMIT_DOCS_FOR_AH_TASK_ASSIGNED_COMMENT.getAction())
                .domainType("gov.uk.ets.registry.api.user.domain.User")
                .description("Older test reason for assigning this task.")
                .creatorType("")
                .creationDate(Date.from(LocalDateTime.now().minusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        entityManager.persist(domainEvent10);

        DomainEventEntity domainEvent11 = DomainEventEntity.builder()
                .domainId("1000093")
                .domainAction(DomainAction.SUBMIT_DOCS_FOR_USER_TASK_ASSIGNED_COMMENT.getAction())
                .domainType("gov.uk.ets.registry.api.user.domain.User")
                .description("Another test reason for assigning this task.")
                .creatorType("")
                .creationDate(Date.from(LocalDateTime.now().minusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()))
                .build();

        entityManager.persist(domainEvent11);

        DomainEventEntity domainEvent12 = DomainEventEntity.builder().
            domainId("1000003").
            domainAction("Error in Allocation Job execution").
            domainType("gov.uk.ets.registry.api.file.upload.domain.UploadedFile").
            description("Allocation Job not started due to business errors. See logs for more details.").
            creatorType("system").
            creationDate(Date.from(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant())).
            build();

        entityManager.persist(domainEvent12);

        Task task1 = new Task();
        task1.setRequestId(1000003L);
        task1.setType(RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST);
        task1.setStatus(RequestStateEnum.APPROVED);

        entityManager.persist(task1);         
        
        User user = new User();
        user.setUrid("UK873533391953");
        user.setFirstName("First name");
        user.setLastName("Last name");
        user.setDisclosedName("Disclosure name");

        entityManager.persist(user);

        IamUserRole role = new IamUserRole();
        role.setRoleName("authorized-representative");

        entityManager.persist(role);

        UserRoleMapping userRoleMapping = new UserRoleMapping();
        userRoleMapping.setUser(user);
        userRoleMapping.setRole(role);

        entityManager.persist(userRoleMapping);
    }

    @Test
    @DisplayName("Transaction system events success.")
    void test_system_transaction_event_success() {
        List<AuditEventDTO> auditEvents =
            domainEventRepository.getSystemEventsByDomainIdOrderByCreationDateDesc("GB100022",
                List.of(Transaction.class.getName(), TransactionService.class.getName()));
        assertEquals(1L, auditEvents.size());
        assertEquals("GB100022", auditEvents.get(0).getDomainId());
        assertEquals("Transaction completed", auditEvents.get(0).getDomainAction());
        assertEquals("", auditEvents.get(0).getDescription());
        assertEquals("system", auditEvents.get(0).getCreatorType());
        assertEquals("System", auditEvents.get(0).getCreator());
    }

    @Test
    @DisplayName("Task system events success.")
    void test_system_task_event_success() {
        List<AuditEventDTO> auditEvents =
            domainEventRepository.getSystemEventsByDomainIdOrderByCreationDateDesc("10000050",
                List.of(Task.class.getName()));
        assertEquals(1L, auditEvents.size());
        assertEquals("10000050", auditEvents.get(0).getDomainId());
        assertEquals("Document automatically deleted", auditEvents.get(0).getDomainAction());
        assertEquals("test.pdf with ID 1", auditEvents.get(0).getDescription());
        assertEquals("system", auditEvents.get(0).getCreatorType());
        assertEquals("System", auditEvents.get(0).getCreator());
    }

    @Test
    @DisplayName("Task events  where task and account happen to have a same identifier. Fetch only Task events success.")
    void test_task_event_for_user_success() {
        List<AuditEventDTO> auditEvents =
            domainEventRepository.getAuditEventsByDomainIdOrderByCreationDateDesc("10000050", false,
                List.of(Task.class.getName()));
        assertEquals(1L, auditEvents.size());
        assertEquals("10000050", auditEvents.get(0).getDomainId());
        assertEquals("Open account task completed.", auditEvents.get(0).getDomainAction());
        assertEquals("", auditEvents.get(0).getDescription());
        assertEquals("user", auditEvents.get(0).getCreatorType());
        assertEquals("Disclosure name", auditEvents.get(0).getCreator());
        assertEquals("UK873533391953", auditEvents.get(0).getCreatorUserIdentifier());
    }

    @Test
    @DisplayName("Task events where task and account happen to have a same identifier. Fetch only Task events with success.")
    void test_task_event_for_admin_success() {
        List<AuditEventDTO> auditEvents =
            domainEventRepository.getAuditEventsByDomainIdOrderByCreationDateDesc("10000050", true,
                List.of(Task.class.getName()));
        assertEquals(1L, auditEvents.size());
        assertEquals("10000050", auditEvents.get(0).getDomainId());
        assertEquals("Open account task completed.", auditEvents.get(0).getDomainAction());
        assertEquals("", auditEvents.get(0).getDescription());
        assertEquals("user", auditEvents.get(0).getCreatorType());
        assertEquals("First name Last name", auditEvents.get(0).getCreator());
        assertEquals("UK873533391953", auditEvents.get(0).getCreatorUserIdentifier());
    }

    @Test
    @DisplayName("Account events  where task and account happen to have a same identifier. Fetch only Account events success.")
    void test_account_event_for_user_success() {
        List<AuditEventDTO> auditEvents =
            domainEventRepository.getAuditEventsByDomainIdOrderByCreationDateDesc("10000050", false,
                List.of(Account.class.getName()));
        assertEquals(1L, auditEvents.size());
        assertEquals("10000050", auditEvents.get(0).getDomainId());
        assertEquals("Open account task completed.", auditEvents.get(0).getDomainAction());
        assertEquals("Task requestId 1000014 APPROVED", auditEvents.get(0).getDescription());
        assertEquals("user", auditEvents.get(0).getCreatorType());
        assertEquals("Disclosure name", auditEvents.get(0).getCreator());
        assertEquals("UK873533391953", auditEvents.get(0).getCreatorUserIdentifier());
    }

    @Test
    @DisplayName("Account events where task and account happen to have a same identifier. Fetch only Account events with success.")
    void test_account_event_for_admin_success() {
        List<AuditEventDTO> auditEvents =
            domainEventRepository.getAuditEventsByDomainIdOrderByCreationDateDesc("10000050", true,
                List.of(Account.class.getName()));
        assertEquals(1L, auditEvents.size());
        assertEquals("10000050", auditEvents.get(0).getDomainId());
        assertEquals("Open account task completed.", auditEvents.get(0).getDomainAction());
        assertEquals("Task requestId 1000014 APPROVED", auditEvents.get(0).getDescription());
        assertEquals("user", auditEvents.get(0).getCreatorType());
        assertEquals("First name Last name", auditEvents.get(0).getCreator());
        assertEquals("UK873533391953", auditEvents.get(0).getCreatorUserIdentifier());
    }

    @Test
    @DisplayName("Transaction Events for users are fetched correctly.")
    void test_transaction_event_for_user_success() {
        List<AuditEventDTO> auditEvents = domainEventRepository.
            getAuditEventsByDomainIdOrderByCreationDateDesc("GB100022", false,
                List.of(Transaction.class.getName(), TransactionService.class.getName()));
        assertEquals(1L, auditEvents.size());
        assertEquals("GB100022", auditEvents.get(0).getDomainId());
        assertEquals("Transaction proposal submitted", auditEvents.get(0).getDomainAction());
        assertEquals("Issue AAU or RMU", auditEvents.get(0).getDescription());
        assertEquals("user", auditEvents.get(0).getCreatorType());
        assertEquals("Disclosure name", auditEvents.get(0).getCreator());
        assertEquals("UK873533391953", auditEvents.get(0).getCreatorUserIdentifier());
        assertNotNull(auditEvents.get(0).getCreationDate());
    }

    @Test
    @DisplayName("Transaction Events for admins are fetched correctly.")
    void test_transaction_event_for_admin_success() {
        List<AuditEventDTO> auditEvents = domainEventRepository.
            getAuditEventsByDomainIdOrderByCreationDateDesc("GB100022", true,
                List.of(Transaction.class.getName(), TransactionService.class.getName()));
        assertEquals(1L, auditEvents.size());
        assertEquals("GB100022", auditEvents.get(0).getDomainId());
        assertEquals("Transaction proposal submitted", auditEvents.get(0).getDomainAction());
        assertEquals("Issue AAU or RMU", auditEvents.get(0).getDescription());
        assertEquals("user", auditEvents.get(0).getCreatorType());
        assertEquals("First name Last name", auditEvents.get(0).getCreator());
        assertEquals("UK873533391953", auditEvents.get(0).getCreatorUserIdentifier());
        assertNotNull(auditEvents.get(0).getCreationDate());
    }
    
    @Test
    @DisplayName("Events by DomainType and RequestType are fetched correctly.")
    void test_findByDomainTypeAndRequestTypeWithUserDetails() {
        List<AuditEventDTO> auditEvents =
            domainEventRepository.findByDomainTypeAndRequestTypeWithUserDetails(UploadedFile.class.getName(),RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST);
        assertEquals(1L, auditEvents.size());
        assertEquals("1000003", auditEvents.get(0).getDomainId());
        assertEquals("Emissions table request approved", auditEvents.get(0).getDomainAction());
        assertEquals("user", auditEvents.get(0).getCreatorType());
        assertEquals("First name Last name", auditEvents.get(0).getCreator());
        assertNotNull(auditEvents.get(0).getCreationDate());
    }

    @Test
    @DisplayName("Events by DomainType and RequestTypes are fetched correctly.")
    void test_findByDomainTypeAndRequestTypeWithOptionalUserDetails() {
        List<AuditEventDTO> auditEvents =
            domainEventRepository.findByDomainTypeAndRequestTypeWithOptionalUserDetails(UploadedFile.class.getName(),
                List.of(RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST, RequestType.ALLOCATION_REQUEST));
        assertEquals(2L, auditEvents.size());
        assertEquals("1000003", auditEvents.get(0).getDomainId());
        assertEquals("Error in Allocation Job execution", auditEvents.get(0).getDomainAction());
        assertEquals("system", auditEvents.get(0).getCreatorType());
        assertNull(auditEvents.get(0).getCreator());
        assertNotNull(auditEvents.get(0).getCreationDate());
        assertEquals("1000003", auditEvents.get(1).getDomainId());
        assertEquals("Emissions table request approved", auditEvents.get(1).getDomainAction());
        assertEquals("user", auditEvents.get(1).getCreatorType());
        assertEquals("First name Last name", auditEvents.get(1).getCreator());
        assertNotNull(auditEvents.get(1).getCreationDate());
    }

    @Test
    @DisplayName("Events by DomainType and RequestType are fetched correctly.")
    void test_getAuditEventsByCreatorOrderByCreationDateDesc() {
        List<AuditEventDTO> auditEvents =
            domainEventRepository.getAuditEventsByCreatorOrderByCreationDateDesc("UK873533391953");

        assertEquals(2L, auditEvents.size());

        AuditEventDTO userAuditEventDTO = auditEvents.stream()
            .filter(dto -> "user".equals(dto.getCreatorType()))
            .findFirst()
            .get();

        assertEquals("UK873533391953", userAuditEventDTO.getDomainId());
        assertEquals("Change user status", userAuditEventDTO.getDomainAction());
        assertEquals("First name Last name", userAuditEventDTO.getCreator());
        assertEquals("ENROLLED", userAuditEventDTO.getDescription());
        assertNotNull(userAuditEventDTO.getCreationDate());

        AuditEventDTO systemAuditEventDTO = auditEvents.stream()
            .filter(dto -> "system".equals(dto.getCreatorType()))
            .findFirst()
            .get();

        assertEquals("UK873533391953", systemAuditEventDTO.getDomainId());
        assertEquals("Add User Role", systemAuditEventDTO.getDomainAction());
        assertNull(systemAuditEventDTO.getCreator());
        assertEquals("junior-registry-administrator", systemAuditEventDTO.getDescription());
        assertNotNull(systemAuditEventDTO.getCreationDate());
    }

    @Test
    @DisplayName("Events by DomainId and DomainAction are fetched for submit ah request documents correctly.")
    void test_findByDomainIdAndDomainActionDescForSubmitAHDocuments() {
        Pageable topOne = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "creationDate"));
        List<AuditEventDTO> auditEvents =
                domainEventRepository.findByDomainIdAndDomainActionDesc("1000092",
                        List.of(DomainAction.SUBMIT_DOCS_FOR_AH_TASK_ASSIGNED_COMMENT.getAction(),
                                DomainAction.SUBMIT_DOCS_FOR_USER_TASK_ASSIGNED_COMMENT.getAction()),
                        topOne);
        assertEquals(1L, auditEvents.size());
        assertEquals("1000092", auditEvents.get(0).getDomainId());
        assertEquals("Some test reason for assigning this task.", auditEvents.get(0).getDescription());
        assertNotNull(auditEvents.get(0).getCreationDate());
    }

    @Test
    @DisplayName("Events by DomainId and DomainAction are fetched for submit user documents correctly.")
    void test_findByDomainIdAndDomainActionDescForSubmitUserDocuments() {
        Pageable topOne = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "creationDate"));
        List<AuditEventDTO> auditEvents =
                domainEventRepository.findByDomainIdAndDomainActionDesc("1000093",
                        List.of(DomainAction.SUBMIT_DOCS_FOR_AH_TASK_ASSIGNED_COMMENT.getAction(),
                                DomainAction.SUBMIT_DOCS_FOR_USER_TASK_ASSIGNED_COMMENT.getAction()),
                        topOne);
        assertEquals(1L, auditEvents.size());
        assertEquals("1000093", auditEvents.get(0).getDomainId());
        assertEquals("Another test reason for assigning this task.", auditEvents.get(0).getDescription());
        assertNotNull(auditEvents.get(0).getCreationDate());
    }
}
