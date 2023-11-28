package gov.uk.ets.registry.api.accountholder.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderContactUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@DisplayName("Testing account holder update service methods")
class AccountHolderUpdateServiceTest {

    @Mock
    private  UserService userService;
    @Mock
    private  AccountService accountService;
    @Mock
    private  PersistenceService persistenceService;
    @Mock
    private TaskEventService taskEventService;
    @Mock
    private Mapper mapper;

    private AccountHolderUpdateService accountHolderUpdateService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        accountHolderUpdateService= new AccountHolderUpdateService(userService,
            accountService, persistenceService, taskEventService, mapper);
    }

    @DisplayName("Test submit account holder update request")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {1}")
    void test_submitAccountHolderUpdateRequest(Object dto, RequestType requestType) {
        Long taskRequestId = 10001L;
        Mockito.when(persistenceService.getNextBusinessIdentifier(Task.class)).thenReturn(taskRequestId);
        User currentUser = new User();
        currentUser.setUrid("UK12345");
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Long accountIdentifier = 10000002L;
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        Mockito.when(accountService.getAccount(accountIdentifier)).thenReturn(account);

        Long returnedTaskRequestId;
        if (dto instanceof AccountHolderDetailsUpdateDTO) {
            returnedTaskRequestId = accountHolderUpdateService
                .submitAccountHolderDetailsUpdateRequest((AccountHolderDetailsUpdateDTO) dto, accountIdentifier);
        } else {
            returnedTaskRequestId = accountHolderUpdateService
                .submitAccountHolderContactUpdateRequest((AccountHolderContactUpdateDTO) dto, accountIdentifier);
        }

        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(persistenceService, Mockito.times(1)).save(argument.capture());

        Task task = new Task();
        task.setType(requestType);
        task.setRequestId(taskRequestId);
        task.setAccount(account);
        verify(taskEventService, times(1))
            .createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        Assert.assertEquals(taskRequestId, returnedTaskRequestId);
        if (dto instanceof AccountHolderDetailsUpdateDTO) {
            Assert.assertEquals(RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS, task.getType());
        } else {
            Assert.assertEquals(RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS, task.getType());
        }
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
            Arguments.of(new AccountHolderDetailsUpdateDTO(), RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS),
            Arguments.of(new AccountHolderContactUpdateDTO(), RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS)
        );
    }
}
