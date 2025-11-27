package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.DateInfo;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderContactUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDiffDTO;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private AccountHolderRepository holderRepository;
    @Mock
    private  PersistenceService persistenceService;
    @Mock
    private TaskEventService taskEventService;
    @Mock
    private Mapper mapper;
    @Mock
    private TrustedAccountRepository trustedAccountRepository;
    @Mock
    private AuthorizedRepresentativeService authorizedRepresentativeService;

    private AccountHolderUpdateService accountHolderUpdateService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        accountHolderUpdateService = new AccountHolderUpdateService(userService,
            accountService, accountRepository, taskRepository, holderRepository,
            persistenceService, taskEventService, mapper, trustedAccountRepository, authorizedRepresentativeService);
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

    @DisplayName("Test submit account holder change request must be success")
    @Test
    void submitChangeAccountHolderRequest_createsNewHolder_success() {
        Long accountId = 123L;
        AccountHolderChangeDTO dto = new AccountHolderChangeDTO();
        dto.setAccountIdentifier(accountId);
        dto.setAcquiringAccountHolder(new AccountHolderDTO()); // No ID → new
        dto.setAcquiringAccountHolderContactInfo(new AccountHolderRepresentativeDTO());

        Account account = new Account();
        account.setId(accountId);
        account.setAccountHolder(new AccountHolder());
        when(accountRepository.findByIdentifier(accountId)).thenReturn(Optional.of(account));
        when(taskRepository.countPendingTasksByAccountIdInAndType(anyList(), any())).thenReturn(0L); // No invalid tasks

        AccountHolder newHolder = new AccountHolder();
        newHolder.setId(2025L);
        when(accountService.createHolder(any())).thenReturn(newHolder);

        Boolean result = accountHolderUpdateService.submitChangeAccountHolderRequest(dto);

        assertTrue(result);
        verify(accountService).insertContact(eq(newHolder), any(), eq(true));
        verify(persistenceService).save(account);
    }

    @DisplayName("Test submit account holder should fail for the same account holder")
    @Test
    void submitChangeAccountHolderRequest_createsNewHolder_fail_forTheSameAccountHolder() {
        Long accountId = 123L;
        AccountHolderChangeDTO dto = new AccountHolderChangeDTO();
        dto.setAccountIdentifier(accountId);
        dto.setAcquiringAccountHolder(new AccountHolderDTO()); // No ID → new
        dto.setAcquiringAccountHolderContactInfo(new AccountHolderRepresentativeDTO());

        Account account = new Account();
        account.setId(accountId);
        AccountHolder existingAccountHolder = new AccountHolder();
        existingAccountHolder.setId(1L);

        account.setAccountHolder(existingAccountHolder);
        when(accountRepository.findByIdentifier(accountId)).thenReturn(Optional.of(account));
        when(taskRepository.countPendingTasksByAccountIdInAndType(anyList(), any())).thenReturn(0L); // No invalid tasks

        AccountHolder newHolder = new AccountHolder();
        newHolder.setId(2002L);
        when(accountService.createHolder(any())).thenReturn(newHolder);

        Boolean result = accountHolderUpdateService.submitChangeAccountHolderRequest(dto);

        assertTrue(result);
        verify(accountService).insertContact(eq(newHolder), any(), eq(true));
        verify(persistenceService).save(account);
    }

    @DisplayName("Test submit account holder should be failed due to missing account")
    @Test
    void submitChangeAccountHolderRequest_throwsWhenAccountMissing() {
        Long accountId = 123L;
        AccountHolderChangeDTO dto = new AccountHolderChangeDTO();
        dto.setAccountIdentifier(accountId);
        when(accountRepository.findByAccountIdentifierWithCompliantEntity(accountId)).thenReturn(Optional.empty());

        AccountActionException ex = assertThrows(AccountActionException.class, () -> {
            accountHolderUpdateService.submitChangeAccountHolderRequest(dto);
        });

        Assertions.assertTrue(ex.getMessage().contains("Missing account"));
    }

    @DisplayName("Test submit account holder change request should fail when there are pending requests")
    @Test
    void submitChangeAccountHolderRequest_throwsOnInvalidTasksForSameAH() {
        Long accountId = 123L;
        AccountHolderChangeDTO dto = new AccountHolderChangeDTO();
        dto.setAccountIdentifier(accountId);
        dto.setAcquiringAccountHolder(new AccountHolderDTO());

        Account account = new Account();
        account.setId(accountId);
        when(accountRepository.findByAccountIdentifierWithCompliantEntity(accountId)).thenReturn(Optional.of(account));

        // First validation: passes
        when(taskRepository.countPendingTasksByAccountIdInAndType(
                eq(List.of(accountId)), eq(AccountHolderChangeRules.changeAccountHolderInvalidPendingTasks())
        )).thenReturn(0L);

        // Second validation: fails
        when(taskRepository.countPendingTasksByAccountIdInAndType(
                eq(List.of(accountId)), argThat(argument ->
                        argument.containsAll(RequestType.getARUpdateTasks()) &&
                                argument.containsAll(AccountHolderChangeRules.changeAccountHolderInvalidRequestTypes())
                )
        )).thenReturn(1L);

        AccountActionException ex = assertThrows(AccountActionException.class, () -> {
            accountHolderUpdateService.submitChangeAccountHolderRequest(dto);
        });

        assertTrue(ex.getMessage().contains("You cannot change the AccountHolder"));
    }


    @DisplayName("Test submit account holder change request should fail when there are invalid pending tasks")
    @Test
    void submitChangeAccountHolderRequest_throwsOnInvalidTasksForAccount() {
        Long accountId = 123L;
        AccountHolderChangeDTO dto = new AccountHolderChangeDTO();
        dto.setAccountIdentifier(accountId);
        dto.setAcquiringAccountHolder(new AccountHolderDTO());

        Account account = new Account();
        account.setId(accountId);
        AccountHolder existingAccountHolder = new AccountHolder();
        existingAccountHolder.setId(111L);
        account.setAccountHolder(existingAccountHolder);
        when(accountRepository.findByIdentifier(accountId)).thenReturn(Optional.of(account));
        // First validation OK
        when(taskRepository.countPendingTasksByAccountIdInAndType(anyList(), any())).thenReturn(0L)
                .thenReturn(2L); // second validation fails

        AccountActionException ex = assertThrows(AccountActionException.class, () -> {
            accountHolderUpdateService.submitChangeAccountHolderRequest(dto);
        });

        assertTrue(ex.getMessage().contains("there are pending update Requests"));
    }

    private AccountHolderDTO getCurrentAccountHolder(AccountHolderType type){
        AccountHolderDTO currentAccountHolder = new AccountHolderDTO();
        currentAccountHolder.setId(10000L);
        currentAccountHolder.setStatus(Status.ACTIVE);
        currentAccountHolder.setType(type);

        EmailAddressDTO emailDto = new EmailAddressDTO();
        emailDto.setEmailAddress("existing@gmail.com");
        emailDto.setEmailAddressConfirmation("existing@gmail.com");
        currentAccountHolder.setEmailAddress(emailDto);

        PhoneNumberDTO phoneDto = new PhoneNumberDTO();
        phoneDto.setPhoneNumber1("07956111222");
        phoneDto.setPhoneNumber2("07956222333");
        phoneDto.setCountryCode1("44");
        phoneDto.setCountryCode2("44");
        currentAccountHolder.setPhoneNumber(phoneDto);

        DetailsDTO details = new DetailsDTO();
        if(type == AccountHolderType.INDIVIDUAL){
            details.setFirstName("Existing");
            details.setLastName("Individual");
            details.setBirthDateInfo(DateInfo.of(Date.valueOf("1980-10-10")));
            details.setBirthYear(1980);
            details.setBirthCountry("UK");
        } else if(type == AccountHolderType.ORGANISATION){
            details.setName("Existing Organisation");
            details.setRegistrationNumber("1234556");
            details.setRegNumTypeRadio(0);
        }
        currentAccountHolder.setDetails(details);

        AddressDTO addressDto = new AddressDTO();
        addressDto.setLine1("1 Existing Street");
        addressDto.setCity("Manchester");
        addressDto.setCountry("United Kingdom");
        addressDto.setPostCode("M9 RTH");
        currentAccountHolder.setAddress(addressDto);

        return currentAccountHolder;
    }

    private AccountHolderRepresentativeDTO getAccountHolderDetailsUpdateDTO(){
        AccountHolderRepresentativeDTO dto = new AccountHolderRepresentativeDTO();

        AccountHolderDetailsUpdateDiffDTO accountHolderDiff = new AccountHolderDetailsUpdateDiffDTO();
        LegalRepresentativeDetailsDTO legalRepresentativeDetailsDTO = new LegalRepresentativeDetailsDTO();
        legalRepresentativeDetailsDTO.setFirstName("New");
        legalRepresentativeDetailsDTO.setLastName("Individual");
        legalRepresentativeDetailsDTO.setBirthDateInfo(DateInfo.of(Date.valueOf("1990-10-10")));

        AddressDTO newAHAddressDto = new AddressDTO();
        newAHAddressDto.setLine1("12 Test Street");
        newAHAddressDto.setCity("London");
        newAHAddressDto.setCountry("United Kingdom");
        newAHAddressDto.setPostCode("NW9 RTH");
        dto.setAddress(newAHAddressDto);

        PhoneNumberDTO newAHPhoneDto = new PhoneNumberDTO();
        newAHPhoneDto.setPhoneNumber1("07956111222");
        newAHPhoneDto.setCountryCode1("44");
        dto.setPhoneNumber(newAHPhoneDto);

        EmailAddressDTO newAHEmail = new EmailAddressDTO();
        newAHEmail.setEmailAddress("new-account-holder@gmail.com");
        newAHEmail.setEmailAddressConfirmation("new-account-holder@gmail.com");
        dto.setEmailAddress(newAHEmail);

        return dto;
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
            Arguments.of(new AccountHolderDetailsUpdateDTO(), RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS),
            Arguments.of(new AccountHolderContactUpdateDTO(), RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS)
        );
    }
}
