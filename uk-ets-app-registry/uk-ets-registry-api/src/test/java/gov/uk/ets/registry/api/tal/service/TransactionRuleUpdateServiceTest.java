package gov.uk.ets.registry.api.tal.service;

import static gov.uk.ets.registry.api.task.domain.types.RequestType.TRANSACTION_RULES_UPDATE_REQUEST;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionRuleUpdateServiceTest {

    @Mock
    AccountService accountService;
    @Mock
    UserService userService;
    @Mock
    PersistenceService persistenceService;
    @Mock
    TaskEventService taskEventService;
    @Mock
    TaskRepository taskRepository;
    @Mock
    private Mapper mapper;

    TransactionRuleUpdateService transactionRuleUpdateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Account account = new Account();
        User user = new User();
        transactionRuleUpdateService = new TransactionRuleUpdateService(accountService, userService,
            persistenceService, taskEventService, taskRepository, mapper);
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setTransfersToAccountsNotOnTheTrustedListAreAllowed(false);
        when(accountService.getAccount(anyLong())).thenReturn(account);
        when(userService.getCurrentUser()).thenReturn(user);
    }

    @Test
    void testCreateTrustedAccountListRulesDtoFromAccount() {

        TrustedAccountListRulesDTO dto = new TrustedAccountListRulesDTO();
        dto.setRule1(true);
        dto.setRule2(false);
        Assertions.assertEquals(dto.getRule1(), transactionRuleUpdateService.createTrustedAccountListRulesDtoFromAccount(100000000L).getRule1());
        Assertions.assertEquals(dto.getRule2(), transactionRuleUpdateService.createTrustedAccountListRulesDtoFromAccount(100000000L).getRule2());
    }

    @Test
    void testUpdateTalTransactionRules() {

        TrustedAccountListRulesDTO dto = new TrustedAccountListRulesDTO();
        dto.setRule1(true);
        dto.setRule2(false);

        Long accountId = 1L;
        Long accountIdentifier = 10000000L;
        Account account = new Account();
        account.setId(accountId);

        when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        when(taskRepository.countPendingTasksByAccountIdInAndType(List.of(accountId),
            List.of(TRANSACTION_RULES_UPDATE_REQUEST))).thenReturn(0L);

        Assertions.assertNotNull(transactionRuleUpdateService.updateTalTransactionRules(dto, 10000000L));
    }

    @Test
    void testUpdateTalTransactionRules_other_task_pending_approval() {

        TrustedAccountListRulesDTO dto = new TrustedAccountListRulesDTO();
        dto.setRule1(true);
        dto.setRule2(false);

        Long accountId = 1L;
        Long accountIdentifier = 10000000L;
        Account account = new Account();
        account.setId(accountId);

        when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        when(taskRepository.countPendingTasksByAccountIdInAndType(List.of(accountId),
            List.of(TRANSACTION_RULES_UPDATE_REQUEST))).thenReturn(1L);
        assertThrows(BusinessRuleErrorException.class, () ->
            transactionRuleUpdateService.updateTalTransactionRules(dto, accountIdentifier));
    }
}
