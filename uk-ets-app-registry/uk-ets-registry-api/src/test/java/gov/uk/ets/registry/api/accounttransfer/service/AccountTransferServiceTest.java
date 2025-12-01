package gov.uk.ets.registry.api.accounttransfer.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderService;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferAction;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferActionType;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferRequestDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;

import java.util.Set;

class AccountTransferServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private AccountService accountService;
    @Mock
    private AccountHolderService accountHolderService;
    @Mock
    private PersistenceService persistenceService;
    @Mock
    private EventService eventService;
    @Mock
    private TaskEventService taskEventService;
    @Mock
    private Mapper mapper;

    private AccountTransferService accountTransferService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        accountTransferService = new AccountTransferService(userService,
            accountService, accountHolderService, persistenceService, eventService, taskEventService, mapper);
    }


    @Test
    void accountTransfer() {
        Long taskRequestId = 10001L;
        when(persistenceService.getNextBusinessIdentifier(Task.class)).thenReturn(taskRequestId);
        User currentUser = new User();
        currentUser.setUrid("UK888756");
        when(userService.getCurrentUser()).thenReturn(currentUser);
        Long accountIdentifier = 10000002L;
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setAccountStatus(AccountStatus.OPEN);
        Long accountHolderIdentifier = 10000002L;
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(accountHolderIdentifier);
        account.setAccountHolder(accountHolder);
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        ActivityType activityType = new ActivityType();
        activityType.setDescription("COMBUSTION_OF_FUELS");
        activityType.setInstallation(installation);
        installation.setActivityTypes(Set.of(activityType));
        account.setCompliantEntity(installation);
        
        when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        when(mapper.convertToJson(any(AccountTransferAction.class))).thenReturn("difference");
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        when(accountHolderService.getAccountHolder(accountHolderIdentifier)).thenReturn(accountHolderDTO);
        when(mapper.convertToJson(accountHolderDTO)).thenReturn("before");

        AccountTransferRequestDTO request = new AccountTransferRequestDTO();
        request.setAccountIdentifier(accountIdentifier);
        request.setAccountTransferType(AccountTransferActionType.ACCOUNT_TRANSFER_TO_EXISTING_HOLDER);
        request.setExistingAcquiringAccountHolderIdentifier(862663L);
        AccountHolderDTO acquiringAccountHolder = new AccountHolderDTO();
        acquiringAccountHolder.setDetails(new DetailsDTO());
        request.setAcquiringAccountHolder(acquiringAccountHolder);

        Long returnedTaskRequestId = accountTransferService.accountTransfer(request);
        Assert.assertEquals(taskRequestId, returnedTaskRequestId);

        //Save Status Transfer Pending
        account = verify(persistenceService, Mockito.times(1)).save(account);

        //Save Task
        Task task = new Task();
        task.setType(RequestType.ACCOUNT_TRANSFER);
        task.setRequestId(taskRequestId);
        task.setAccount(account);

        verify(persistenceService, Mockito.times(1)).save(task);

        verify(taskEventService, times(1))
            .createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

    }
    
    @Test
    void invalidAccountTransfer() {
        Long taskRequestId = 10001L;
        when(persistenceService.getNextBusinessIdentifier(Task.class)).thenReturn(taskRequestId);
        User currentUser = new User();
        currentUser.setUrid("UK888756");
        when(userService.getCurrentUser()).thenReturn(currentUser);
        Long accountIdentifier = 10000002L;
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setAccountStatus(AccountStatus.OPEN);
        Long accountHolderIdentifier = 10000002L;
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(accountHolderIdentifier);
        account.setAccountHolder(accountHolder);
        MaritimeOperator operator = new MaritimeOperator();
        operator.setIdentifier(1234L);
        operator.setMaritimeMonitoringPlanIdentifier("MPID_1");
        account.setCompliantEntity(operator);
        
        when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        when(mapper.convertToJson(any(AccountTransferAction.class))).thenReturn("difference");
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        when(accountHolderService.getAccountHolder(accountHolderIdentifier)).thenReturn(accountHolderDTO);
        when(mapper.convertToJson(accountHolderDTO)).thenReturn("before");

        AccountTransferRequestDTO request = new AccountTransferRequestDTO();
        request.setAccountIdentifier(accountIdentifier);
        request.setAccountTransferType(AccountTransferActionType.ACCOUNT_TRANSFER_TO_EXISTING_HOLDER);
        request.setExistingAcquiringAccountHolderIdentifier(862663L);
        AccountHolderDTO acquiringAccountHolder = new AccountHolderDTO();
        acquiringAccountHolder.setDetails(new DetailsDTO());
        request.setAcquiringAccountHolder(acquiringAccountHolder); 

        Exception exception = assertThrows(AccountActionException.class, () -> {
        	accountTransferService.accountTransfer(request);
        });

        String expectedMessage = "You can only transfer Operator Holding Accounts.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
