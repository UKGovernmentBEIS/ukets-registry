package gov.uk.ets.registry.api.authz.ruleengine.features.account.holder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.ar.rules.ARUpdateBRTestHelper;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountHolderSecurityStoreSliceLoaderTest {
    @Mock
    private AccountHolderRepository accountHolderRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private RuleInputStore ruleInputStore;

    private BusinessSecurityStore businessSecurityStore;

    private AccountHolderSecurityStoreSliceLoader accountHolderSecurityStoreSliceLoader;

    private ARUpdateBRTestHelper helper = new ARUpdateBRTestHelper();

    @BeforeEach
    void setUp() {
        businessSecurityStore = new BusinessSecurityStore();
        accountHolderSecurityStoreSliceLoader = new AccountHolderSecurityStoreSliceLoader(
            accountHolderRepository,
            taskRepository);
        accountHolderSecurityStoreSliceLoader.setBusinessSecurityStore(businessSecurityStore);
        accountHolderSecurityStoreSliceLoader.setRuleInputStore(ruleInputStore);
    }

    @Test
    @DisplayName("Given ACCOUNT_HOLDER_ID rule input type when loads then it loads the expected account holder business security store slice")
    void loadWithAccountIdAndCandidate() {
        // given
        Long accountHolderIdentifier = 123L;
        Task t = new Task();
        t.setRequestId(12345L);
        given(ruleInputStore.containsKey(RuleInputType.ACCOUNT_HOLDER_ID)).willReturn(true);
        given(ruleInputStore.get(RuleInputType.ACCOUNT_HOLDER_ID)).willReturn(accountHolderIdentifier);
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(accountHolderIdentifier);
        TestCommand command = TestCommand.builder()
            .accountHolder(accountHolder)
            .accountHolderId(accountHolderIdentifier)
            .tasks(Collections.singletonList(t))
            .build();
        givenExpected(command);
        // when
        accountHolderSecurityStoreSliceLoader.load();
        // then
        verifyCommonLoadBehavior(command);
        assertEquals(t.getRequestId(),
            businessSecurityStore.getAccountHolderSecurityStoreSlice().getTasksByAccountHolder().get(0).getRequestId());
    }

    private void verifyCommonLoadBehavior(TestCommand command) {
        then(accountHolderRepository).should(times(1)).getAccountHolder(command.accountHolderId);
        then(taskRepository).should(times(1)).countPendingTasksByAccountIdentifierInAndType(command.accountHolderId);
        AccountHolderSecurityStoreSlice slice = businessSecurityStore.getAccountHolderSecurityStoreSlice();
        assertEquals(command.accountHolder, slice.getAccountHolder());
        assertEquals(command.tasks, slice.getTasksByAccountHolder());
    }

    private void givenExpected(TestCommand command) {
        given(ruleInputStore.get(RuleInputType.ACCOUNT_HOLDER_ID)).willReturn(command.accountHolderId);
        given(ruleInputStore.containsKey(RuleInputType.ACCOUNT_HOLDER_ID)).willReturn(true);
        given(accountHolderRepository.getAccountHolder(command.accountHolderId))
            .willReturn(command.accountHolder);
        given(taskRepository.countPendingTasksByAccountIdentifierInAndType(command.accountHolderId))
            .willReturn(command.tasks);
    }

    @Builder
    private static class TestCommand {
        private Long accountHolderId;
        private AccountHolder accountHolder;
        private List<Task> tasks;
    }
}