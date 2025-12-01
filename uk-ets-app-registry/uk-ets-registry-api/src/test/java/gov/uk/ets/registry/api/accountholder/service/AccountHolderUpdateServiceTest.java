package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeActionType;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderContactUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@DisplayName("Testing account holder update service methods")
class AccountHolderUpdateServiceTest {

    @Mock
    private  UserService userService;
    @Mock
    private  AccountService accountService;
    @Mock
    private AccountHolderService accountHolderService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private  PersistenceService persistenceService;
    @Mock
    private TaskEventService taskEventService;
    @Mock
    private Mapper mapper;
    @Mock
    private EventService eventService;
    @Mock
    private AccountHolderChangeValidationService accountHolderChangeValidationService;

    private AccountHolderUpdateService accountHolderUpdateService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        accountHolderUpdateService = new AccountHolderUpdateService(userService,
            accountService, accountRepository,
            persistenceService, taskEventService, mapper,
            accountHolderService, eventService, accountHolderChangeValidationService);
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

    @Test
    void accountHolderChange_successful() {
        // Arrange
        Account account = new Account();
        account.setIdentifier(123L);
        AccountHolder currentHolder = new AccountHolder();
        account.setAccountHolder(currentHolder);

        User user = new User();
        user.setUrid("user123");

        AccountHolderDTO acquiringHolder = new AccountHolderDTO();
        DetailsDTO details = new DetailsDTO();
        details.setName("New Holder Name");
        acquiringHolder.setId(999L);
        acquiringHolder.setDetails(details);

        AccountHolderChangeDTO dto = new AccountHolderChangeDTO();
        dto.setAccountIdentifier(123L);
        dto.setAcquiringAccountHolder(acquiringHolder);
        dto.setAccountHolderChangeActionType(AccountHolderChangeActionType.ACCOUNT_HOLDER_CHANGE_TO_EXISTING_HOLDER);
        dto.setAccountHolderDelete(false);

        when(accountRepository.findByIdentifier(123L)).thenReturn(Optional.of(account));
        when(persistenceService.getNextBusinessIdentifier(Task.class)).thenReturn(456L);
        when(userService.getCurrentUser()).thenReturn(user);
        when(accountHolderService.getAccountHolder(anyLong())).thenReturn(acquiringHolder);
        when(mapper.convertToJson(any())).thenReturn("{}");
        when(persistenceService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(accountHolderChangeValidationService)
                .validateAccountHolderChangeRequestForAccountIdentifier(anyLong(), anyLong(), anyBoolean(), anyBoolean());
        doNothing().when(taskEventService).createAndPublishTaskAndAccountRequestEvent(any(Task.class), anyString());

        // Act
        Long result = accountHolderUpdateService.accountHolderChange(dto);

        // Assert
        assertThat(result).isEqualTo(456L);

        verify(accountHolderChangeValidationService).validateAccountHolderChangeRequestForAccountIdentifier(
                eq(123L), eq(999L), eq(true), eq(false)
        );
        verify(accountRepository).findByIdentifier(123L);
        verify(mapper, atLeastOnce()).convertToJson(any());
        verify(taskEventService).createAndPublishTaskAndAccountRequestEvent(any(Task.class), eq("user123"));
        verify(eventService).createAndPublishEvent(eq("123"), eq("user123"), anyString(), any(), anyString());
        verify(persistenceService, atLeast(1)).save(any());
    }

    @Test
    void accountHolderChange_shouldThrowException_whenAccountNotFound() {
        // Arrange
        AccountHolderDTO acquiringHolder = new AccountHolderDTO();
        acquiringHolder.setId(999L);

        AccountHolderChangeDTO dto = new AccountHolderChangeDTO();
        dto.setAccountIdentifier(123L);
        dto.setAcquiringAccountHolder(acquiringHolder);
        dto.setAccountHolderChangeActionType(AccountHolderChangeActionType.ACCOUNT_HOLDER_CHANGE_TO_EXISTING_HOLDER);
        dto.setAccountHolderDelete(false);

        when(accountRepository.findByIdentifier(123L)).thenReturn(Optional.empty());

        // Act + Assert
        AccountActionException exception = assertThrows(AccountActionException.class,
                () -> accountHolderUpdateService.accountHolderChange(dto));

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage())
                .contains("You cannot change the AccountHolder - Missing account");

        verify(accountRepository).findByIdentifier(123L);
        verifyNoInteractions(taskEventService, eventService);
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
            Arguments.of(new AccountHolderDetailsUpdateDTO(), RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS),
            Arguments.of(new AccountHolderContactUpdateDTO(), RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS)
        );
    }
}
