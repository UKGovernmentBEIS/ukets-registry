package gov.uk.ets.registry.api.ar.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountCommand;
import gov.uk.ets.registry.api.helper.persistence.AccountModelTestHelper.AddAccountHolderCommand;
import gov.uk.ets.registry.api.helper.persistence.TaskModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.TaskModelTestHelper.AddTaskCommand;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
class ARUpdateActionRepositoryImplTest {
    @PersistenceContext
    EntityManager entityManager;

    private AccountModelTestHelper accountModelTestHelper;
    private TaskModelTestHelper taskModelTestHelper;
    private ARUpdateAction arUpdateAction;
    private Account account;

    private ARUpdateActionRepository repository;

    @Mock
    Mapper mapper;

    @BeforeEach
    void setup() {
        repository = new ARUpdateActionRepositoryImpl(entityManager, mapper);
        accountModelTestHelper = new AccountModelTestHelper(entityManager);
        taskModelTestHelper = new TaskModelTestHelper(entityManager);
        fillData();

    }

    private void fillData() {
        AccountHolder accountHolder = accountModelTestHelper.addAccountHolder(AddAccountHolderCommand
            .builder()
            .accountHolderType(AccountHolderType.INDIVIDUAL)
            .identifier(1000L)
            .build());

        account = accountModelTestHelper.addAccount(AddAccountCommand.builder()
            .accountHolder(accountHolder)
            .accountId(101L)
            .fullIdentifier("21321-2131-2321")
            .build());
    }


    @Test
    void fetchByAccountId() {
        arUpdateAction = new ARUpdateAction();
        arUpdateAction.setType(ARUpdateActionType.ADD);
        arUpdateAction.setAccountAccessRight(AccountAccessRight.INITIATE_AND_APPROVE);
        arUpdateAction.setUrid("UK123213");

        Task task = taskModelTestHelper.addTask(AddTaskCommand.builder()
            .account(account)
            .requestId(2322L)
            .requestType(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST)
            .status(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED)
            .difference(mapper.convertToJson(arUpdateAction))
            .build());

        when(mapper.convertToPojo(task.getDifference(), ARUpdateAction.class)).thenReturn(arUpdateAction);

        List<ARUpdateAction> pendingActions = repository.fetchByAccountId(account.getIdentifier());

        assertNotNull(pendingActions);
        assertEquals(1, pendingActions.size());
        ARUpdateAction storedAction = mapper.convertToPojo(task.getDifference(), ARUpdateAction.class);
        assertEquals(arUpdateAction, storedAction);
    }
}
