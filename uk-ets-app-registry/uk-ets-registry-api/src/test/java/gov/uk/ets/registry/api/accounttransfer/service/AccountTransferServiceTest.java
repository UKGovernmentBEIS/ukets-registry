package gov.uk.ets.registry.api.accounttransfer.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferActionType;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferRequestDTO;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class AccountTransferServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private AccountService accountService;
    @Mock
    private PersistenceService persistenceService;
    @Mock
    private EventService eventService;
    @Mock
    private TaskEventService taskEventService;
    @Mock
    private Mapper mapper;
    @Mock
    private ConversionService conversionService;

    private AccountTransferService accountTransferService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        accountTransferService = new AccountTransferService(userService,
            accountService, persistenceService, eventService, taskEventService, mapper, conversionService);
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
        account.setAccountHolder(new AccountHolder());
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setActivityType(InstallationActivityType.COMBUSTION_OF_FUELS.toString());
        account.setCompliantEntity(installation);
        
        when(accountService.getAccount(accountIdentifier)).thenReturn(account);

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

}
