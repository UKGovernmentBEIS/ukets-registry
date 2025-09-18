package gov.uk.ets.registry.api.tal.service;


import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.TrustedAccountTaskDifference;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TrustedAccountTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.service.TransactionDelayService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


class TrustedAccountTaskServiceTest {


    @Mock
    private AccountService mockAccountService;
    @Mock
    private TransactionDelayService mockTransactionDelayService;
    @Mock
    private TrustedAccountConversionService trustedAccountConversionService;
    @Mock
    private TrustedAccountRepository mockTrustedAccountRepository;
    @Mock
    private TrustedAccountCandidateRuleApplier applier;
    @Mock
    private Mapper mapper;

    TrustedAccountTaskService trustedAccountTaskService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        trustedAccountTaskService =
            new TrustedAccountTaskService(mockAccountService, trustedAccountConversionService,
                mockTrustedAccountRepository,
                mockTransactionDelayService,
                applier,
                mapper);
    }

//    @DisplayName("A junior or senior admin can view the task details in any case")
//    @Test
//    @Disabled
//    void canViewTaskAsASeniorOrJuniorAdmin() {
//        when(mockAuthorizationService.getScopes())
//            .thenReturn(Set.of(Scope.SCOPE_TASK_TRUSTED_ACCOUNTS_LIST_READ.getScopeName()));
//        Assertions.assertTrue(trustedAccountTaskService.canView(null));
//    }

//    @EnumSource(
//        value = AccountAccessRight.class,
//        names = {"INITIATE_AND_APPROVE", "APPROVE"})
//    @DisplayName("An authorized representative with the right access rights on the account can view the task")
//    @ParameterizedTest(name = "{0} access right ")
//    @Disabled
//    void canViewTaskAsAnAuthorizedRepresentativeWithAppropriateAccessRights(AccountAccessRight accountAccessRight) {
//        when(mockAuthorizationService.getScopes())
//            .thenReturn(Set.of(Scope.SCOPE_TASK_TRUSTED_ACCOUNTS_LIST_WRITE.getScopeName()));
//        when(mockAccountAuthorizationService.checkAccountAccess(1L)).thenReturn(accountAccessRight);
//        Assertions.assertTrue(trustedAccountTaskService.canView(1L));
//    }


//    @EnumSource(
//        value = AccountAccessRight.class, mode = EnumSource.Mode.EXCLUDE,
//        names = {"INITIATE_AND_APPROVE", "APPROVE"})
//    @DisplayName("An authorized representative with no correct rights cannot view the account")
//    @ParameterizedTest(name = " {0} access right")
//    @Disabled
//    void cannotViewTaskAsAnAuthorizedRepresentativeWithNoCorrectRights(AccountAccessRight accountAccessRight) {
//        when(mockAuthorizationService.getScopes())
//            .thenReturn(Set.of(Scope.SCOPE_TASK_TRUSTED_ACCOUNTS_LIST_WRITE.getScopeName()));
//        when(mockAccountAuthorizationService.checkAccountAccess(1L)).thenReturn(accountAccessRight);
//
//        TaskActionException exception = Assertions.assertThrows(TaskActionException.class, () -> {
//            trustedAccountTaskService.canView(1L);
//        });
//        String expectedMessage = "You do not have the permission to view this task.";
//        String actualMessage = exception.getMessage();
//        Assertions.assertEquals(expectedMessage, actualMessage);
//    }


//    @DisplayName("An authorized representative with no rights for the account")
//    @Disabled
//    void cannotViewTaskAsAnAuthorizedRepresentativeWithNoRights() {
//        when(mockAuthorizationService.getScopes())
//            .thenReturn(Set.of(Scope.SCOPE_TASK_TRUSTED_ACCOUNTS_LIST_WRITE.getScopeName()));
//        when(mockAccountAuthorizationService.checkAccountAccess(1L)).thenReturn(null);
//
//        TaskActionException exception = Assertions.assertThrows(TaskActionException.class, () -> {
//            trustedAccountTaskService.canView(1L);
//        });
//        String expectedMessage = "You do not have the permission to view this task.";
//        String actualMessage = exception.getMessage();
//        Assertions.assertEquals(expectedMessage, actualMessage);
//    }

    @Test
    void deserializeTaskDifferenceTest() throws JsonProcessingException {
        String diff = "{\"ids\": [10001,10002]}";
        TrustedAccountTaskDifference trustedAccountTaskDifference =
            new ObjectMapper().readValue(diff, TrustedAccountTaskDifference.class);
        Assertions.assertEquals(2L, trustedAccountTaskDifference.getIds().size());
    }

    @DisplayName("Approve an addition of an account to the TAL list should set the activation date and status to Pending Activation")
    @Test
    void testApproveAdd() {
        //
        List<TrustedAccount> trustedAccountList =
            createTrustedAccount(List.of(10002L), TrustedAccountStatus.PENDING_ACTIVATION);

        when(mockTrustedAccountRepository.findByIdIn(Lists.newArrayList(10002L)))
            .thenReturn(trustedAccountList);
        LocalDateTime activationDate = LocalDateTime.now();
        when(mockTransactionDelayService.calculateTrustedAccountListDelay())
            .thenReturn(activationDate);
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST);
        taskDetailsDTO.setDifference("{\"ids\": [10002]}");
        TrustedAccountTaskDetailsDTO trustedAccountTaskDetailsDTO = new TrustedAccountTaskDetailsDTO(taskDetailsDTO);
        when(mapper.convertToPojo(trustedAccountTaskDetailsDTO.getDifference(), TrustedAccountTaskDifference.class))
                .thenReturn(TrustedAccountTaskDifference.builder().ids(List.of(10002L)).build());

        trustedAccountTaskService.complete(trustedAccountTaskDetailsDTO, TaskOutcome.APPROVED, null);
        Assertions.assertEquals(TrustedAccountStatus.PENDING_ACTIVATION, trustedAccountList.get(0).getStatus());
        Assertions.assertEquals(activationDate, trustedAccountList.get(0).getActivationDate());
    }

    @DisplayName("Reject an addition of an account to the TAL list should set status to Rejected")
    @Test
    void testRejectAdd() {
        //
        List<TrustedAccount> trustedAccountList =
            createTrustedAccount(List.of(10002L), TrustedAccountStatus.PENDING_ACTIVATION);

        when(mockTrustedAccountRepository.findByIdIn(Lists.newArrayList(10002L)))
            .thenReturn(trustedAccountList);
        LocalDateTime activationDate = LocalDateTime.now();
        when(mockTransactionDelayService.calculateTrustedAccountListDelay())
            .thenReturn(activationDate);
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST);
        taskDetailsDTO.setDifference("{\"ids\": [10002]}");
        TrustedAccountTaskDetailsDTO trustedAccountTaskDetailsDTO = new TrustedAccountTaskDetailsDTO(taskDetailsDTO);
        when(mapper.convertToPojo(trustedAccountTaskDetailsDTO.getDifference(), TrustedAccountTaskDifference.class))
                .thenReturn(TrustedAccountTaskDifference.builder().ids(List.of(10002L)).build());

        trustedAccountTaskService.complete(trustedAccountTaskDetailsDTO, TaskOutcome.REJECTED, null);
        Assertions.assertEquals(TrustedAccountStatus.REJECTED, trustedAccountList.get(0).getStatus());
        Assertions.assertNull(trustedAccountList.get(0).getActivationDate());
    }

    @DisplayName("Approve an deletion task requests shall not set the Trusted account status to REJECTED")
    @Test
    void testApproveDeleteAccount() {
        TrustedAccountStatus initialStatus = TrustedAccountStatus.ACTIVE;
        List<TrustedAccount> trustedAccountList = createTrustedAccount(List.of(10002L, 10002L), initialStatus);

        when(mockTrustedAccountRepository.findByIdIn(Lists.newArrayList(10002L, 10002L)))
            .thenReturn(trustedAccountList);
        LocalDateTime activationDate = LocalDateTime.now();
        when(mockTransactionDelayService.calculateTrustedAccountListDelay())
            .thenReturn(activationDate);
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST);
        taskDetailsDTO.setDifference("{\"ids\": [10002,10002]}");
        TrustedAccountTaskDetailsDTO trustedAccountTaskDetailsDTO = new TrustedAccountTaskDetailsDTO(taskDetailsDTO);
        when(mapper.convertToPojo(trustedAccountTaskDetailsDTO.getDifference(), TrustedAccountTaskDifference.class))
                .thenReturn(TrustedAccountTaskDifference.builder().ids(List.of(10002L, 10002L)).build());

        trustedAccountTaskService.complete(trustedAccountTaskDetailsDTO, TaskOutcome.APPROVED, null);
        taskDetailsDTO.setDifference("{\"ids\": [10002]}");
        Assertions.assertEquals(TrustedAccountStatus.REJECTED, trustedAccountList.get(0).getStatus());
        Assertions.assertEquals(TrustedAccountStatus.REJECTED, trustedAccountList.get(1).getStatus());
    }

    @DisplayName("Reject an deletion task requests shall not change the status")
    @Test
    void testRejectDeleteAccounts() {
        TrustedAccountStatus initialStatus = TrustedAccountStatus.ACTIVE;
        List<TrustedAccount> trustedAccountList = createTrustedAccount(List.of(10002L, 10002L), initialStatus);

        when(mockTrustedAccountRepository.findByIdIn(Lists.newArrayList(10002L, 10002L)))
            .thenReturn(trustedAccountList);
        LocalDateTime activationDate = LocalDateTime.now();
        when(mockTransactionDelayService.calculateTrustedAccountListDelay())
            .thenReturn(activationDate);
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST);
        taskDetailsDTO.setDifference("{\"ids\": [10002,10002]}");
        TrustedAccountTaskDetailsDTO trustedAccountTaskDetailsDTO = new TrustedAccountTaskDetailsDTO(taskDetailsDTO);
        when(mapper.convertToPojo(trustedAccountTaskDetailsDTO.getDifference(), TrustedAccountTaskDifference.class))
                .thenReturn(TrustedAccountTaskDifference.builder().ids(List.of(10002L, 10002L)).build());

        trustedAccountTaskService.complete(trustedAccountTaskDetailsDTO, TaskOutcome.REJECTED, null);
        taskDetailsDTO.setDifference("{\"ids\": [10002]}");
        Assertions.assertEquals(initialStatus, trustedAccountList.get(0).getStatus());
        Assertions.assertEquals(initialStatus, trustedAccountList.get(1).getStatus());
    }

    @DisplayName("Allow Rejection when TAL list is empty")
    @Test
    void testRejectWhenTALIsZero() {
        //
        Long REQUEST_ID = 567876L;
        List<TrustedAccount> trustedAccountList =
                createTrustedAccount(List.of(), TrustedAccountStatus.PENDING_ACTIVATION);

        when(mockTrustedAccountRepository.findByIdIn(Lists.newArrayList()))
                .thenReturn(trustedAccountList);
        LocalDateTime activationDate = LocalDateTime.now();
        when(mockTransactionDelayService.calculateTrustedAccountListDelay())
                .thenReturn(activationDate);
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST);
        taskDetailsDTO.setRequestId(REQUEST_ID);
        taskDetailsDTO.setDifference("{\"ids\": [10002]}");
        TrustedAccountTaskDetailsDTO trustedAccountTaskDetailsDTO = new TrustedAccountTaskDetailsDTO(taskDetailsDTO);
        when(mapper.convertToPojo(trustedAccountTaskDetailsDTO.getDifference(), TrustedAccountTaskDifference.class))
                .thenReturn(TrustedAccountTaskDifference.builder().ids(List.of(10002L)).build());

        var response = trustedAccountTaskService.complete(trustedAccountTaskDetailsDTO, TaskOutcome.REJECTED, null);
        Assertions.assertEquals(TaskCompleteResponse.class, response.getClass());
        Assertions.assertEquals(REQUEST_ID, response.getRequestIdentifier());
    }

    private List<TrustedAccount> createTrustedAccount(List<Long> ids, TrustedAccountStatus trustedAccountStatus) {
        List<TrustedAccount> trustedAccountList = new ArrayList<>();
        ids.forEach(id -> {
            TrustedAccount trustedAccountToAdd = new TrustedAccount();
            trustedAccountToAdd.setId(id);
            trustedAccountToAdd.setStatus(trustedAccountStatus);
            trustedAccountList.add(trustedAccountToAdd);
        });
        return trustedAccountList;
    }
}
