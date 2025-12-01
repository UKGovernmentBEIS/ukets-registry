package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AccountOwnership;
import gov.uk.ets.registry.api.account.domain.types.AccountOwnershipStatus;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.repository.AccountOwnershipRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountConversionService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderFileDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeAction;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeActionType;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeTaskDetailsDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.AccountHolderFileRepository;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountHolderChangeTaskServiceTest {

    @InjectMocks
    private AccountHolderChangeTaskService accountHolderChangeTaskService;

    @Mock
    private Mapper mapper;
    @Mock
    private AccountService accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountHolderRepository accountHolderRepository;
    @Mock
    private AccountConversionService accountConversionService;
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;
    @Mock
    private AccountHolderChangeValidationService accountHolderChangeValidationService;
    @Mock
    private AccountOwnershipRepository accountOwnershipRepository;
    @Mock
    private AccountHolderRepresentativeRepository accountHolderRepresentativeRepository;
    @Mock
    private AccountHolderFileRepository accountHolderFileRepository;

    @Test
    void appliesFor() {
        final Set<RequestType> actual = accountHolderChangeTaskService.appliesFor();
        assertThat(actual).containsExactly(RequestType.ACCOUNT_HOLDER_CHANGE);
    }

    @Test
    void getDetails_shouldUseMapperWhenBeforeExists() {
        // Given
        TaskDetailsDTO taskDetails = new TaskDetailsDTO();
        taskDetails.setAccountNumber("123");
        taskDetails.setBefore("before-json");
        taskDetails.setDifference("diff-json");

        AccountHolderChangeAction action = new AccountHolderChangeAction();
        AccountHolderDTO holderDTO = new AccountHolderDTO();

        AccountDTO accountDTO = new AccountDTO();
        AccountDetailsDTO detailsDTO = new AccountDetailsDTO();
        detailsDTO.setAccountNumber("123");
        accountDTO.setAccountDetails(detailsDTO);

        when(mapper.convertToPojo(eq("diff-json"), eq(AccountHolderChangeAction.class))).thenReturn(action);
        when(mapper.convertToPojo(eq("before-json"), eq(AccountHolderDTO.class))).thenReturn(holderDTO);
        when(accountService.getAccountDTO(123L)).thenReturn(accountDTO);

        // When
        AccountHolderChangeTaskDetailsDTO result = accountHolderChangeTaskService.getDetails(taskDetails);

        // Then
        assertThat(result.getAccount()).isEqualTo(detailsDTO);
        assertThat(result.getCurrentAccountHolder()).isEqualTo(holderDTO);
        assertThat(result.getAction()).isEqualTo(action);

        verify(mapper).convertToPojo("diff-json", AccountHolderChangeAction.class);
        verify(mapper).convertToPojo("before-json", AccountHolderDTO.class);
        verifyNoInteractions(accountHolderRepository);
    }

    @Test
    void getDetails_shouldFetchAccountHolderWhenBeforeIsNull() {
        // Given
        TaskDetailsDTO taskDetails = new TaskDetailsDTO();
        taskDetails.setAccountNumber("456");
        taskDetails.setDifference("diff-json");

        AccountHolderChangeAction action = new AccountHolderChangeAction();
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setId(1L);
        accountHolder.setName("Fetched Holder");

        AccountHolderDTO holderDTO = new AccountHolderDTO();
        holderDTO.setId(1L);

        AccountDTO accountDTO = new AccountDTO();
        AccountDetailsDTO detailsDTO = new AccountDetailsDTO();
        detailsDTO.setAccountNumber("456");
        accountDTO.setAccountDetails(detailsDTO);

        when(mapper.convertToPojo(eq("diff-json"), eq(AccountHolderChangeAction.class))).thenReturn(action);
        when(accountHolderRepository.getAccountHolderOfAccount(456L)).thenReturn(accountHolder);
        when(accountConversionService.convert(accountHolder)).thenReturn(holderDTO);
        when(accountService.getAccountDTO(456L)).thenReturn(accountDTO);

        // When
        AccountHolderChangeTaskDetailsDTO result = accountHolderChangeTaskService.getDetails(taskDetails);

        // Then
        assertThat(result.getAccount()).isEqualTo(detailsDTO);
        assertThat(result.getCurrentAccountHolder()).isEqualTo(holderDTO);
        assertThat(result.getAction()).isEqualTo(action);

        verify(accountHolderRepository).getAccountHolderOfAccount(456L);
        verify(accountConversionService).convert(accountHolder);
    }

    @Test
    void complete_shouldApproveAndChangeAccountHolder() {
        // Given
        Long accountId = 123L;
        User currentUser = new User();
        currentUser.setUrid("user123");

        AccountHolder oldHolder = new AccountHolder();
        oldHolder.setId(1L);
        oldHolder.setName("Old Holder");

        AccountHolder newHolder = new AccountHolder();
        newHolder.setId(2L);
        newHolder.setName("New Holder");

        Account account = new Account();
        account.setIdentifier(accountId);
        account.setAccountHolder(oldHolder);

        AccountOwnership ownership = new AccountOwnership();
        ownership.setAccount(account);
        ownership.setHolder(oldHolder);
        ownership.setStatus(AccountOwnershipStatus.ACTIVE);

        AccountHolderChangeAction action = mock(AccountHolderChangeAction.class);
        AccountHolderDTO newHolderDTO = new AccountHolderDTO();
        newHolderDTO.setId(2L);
        DetailsDTO detailsDTO = new DetailsDTO();
        detailsDTO.setName("New Holder Name");
        newHolderDTO.setDetails(detailsDTO);
        when(action.getAccountHolderDTO()).thenReturn(newHolderDTO);
        when(action.getType()).thenReturn(AccountHolderChangeActionType.ACCOUNT_HOLDER_CHANGE_TO_EXISTING_HOLDER);
        when(action.getAccountHolderDelete()).thenReturn(false);

        var taskDTO = new AccountHolderChangeTaskDetailsDTO(new TaskDetailsDTO());
        taskDTO.setAccountNumber(String.valueOf(accountId));
        taskDTO.setAction(action);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(accountRepository.findByIdentifier(accountId)).thenReturn(Optional.of(account));
        when(accountHolderRepository.getAccountHolder(newHolderDTO.getId())).thenReturn(newHolder);
        when(accountOwnershipRepository.findByAccountAndHolderAndStatus(account, oldHolder, AccountOwnershipStatus.ACTIVE))
                .thenReturn(List.of(ownership));

        // When
        TaskCompleteResponse response = accountHolderChangeTaskService.complete(taskDTO, TaskOutcome.APPROVED, "ok");

        // Then
        assertThat(response).isNotNull();
        assertThat(account.getAccountHolder()).isEqualTo(newHolder);

        verify(accountHolderChangeValidationService)
                .validateChangeAccountHolderTaskForAccountIdentifier(accountId, newHolder.getId(), false);

        verify(accountOwnershipRepository).save(any(AccountOwnership.class));
        verify(eventService).createAndPublishEvent(
                eq(accountId.toString()),
                eq(currentUser.getUrid()),
                contains("From"),
                any(),
                contains("approved")
        );

        verify(accountHolderRepository, never()).delete(any());
    }

    @Test
    void complete_shouldDeleteOldAccountHolderWhenFlagTrue() {
        // Given
        Long accountId = 321L;
        User currentUser = new User();
        currentUser.setUrid("user456");

        AccountHolder oldHolder = new AccountHolder();
        oldHolder.setId(5L);
        oldHolder.setName("Old Holder");
        oldHolder.setIdentifier(123456789L);

        AccountHolder newHolder = new AccountHolder();
        newHolder.setId(10L);
        newHolder.setName("New Holder");

        Account account = new Account();
        account.setIdentifier(accountId);
        account.setAccountHolder(oldHolder);

        AccountOwnership ownership = new AccountOwnership();
        ownership.setAccount(account);
        ownership.setHolder(oldHolder);
        ownership.setStatus(AccountOwnershipStatus.ACTIVE);

        AccountOwnership inactiveOwnership = new AccountOwnership();
        ownership.setAccount(account);
        ownership.setHolder(oldHolder);
        ownership.setStatus(AccountOwnershipStatus.INACTIVE);

        AccountHolderChangeAction action = mock(AccountHolderChangeAction.class);
        AccountHolderDTO newHolderDTO = new AccountHolderDTO();
        newHolderDTO.setId(10L);
        DetailsDTO detailsDTO = new DetailsDTO();
        detailsDTO.setName("New Holder Name");
        newHolderDTO.setDetails(detailsDTO);
        when(action.getAccountHolderDTO()).thenReturn(newHolderDTO);
        when(action.getType()).thenReturn(AccountHolderChangeActionType.ACCOUNT_HOLDER_CHANGE_TO_EXISTING_HOLDER);
        when(action.getAccountHolderDelete()).thenReturn(true);

        var taskDTO = new AccountHolderChangeTaskDetailsDTO(new TaskDetailsDTO());
        taskDTO.setAccountNumber(String.valueOf(accountId));
        taskDTO.setAction(action);

        final Long accountHolderFileId = 1L;

        final List<AccountHolderRepresentative> accountHolderRepresentatives = List.of(createAccountHolderRepresentative());
        final List<AccountOwnership> inactiveOwnerships = List.of(inactiveOwnership);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(accountRepository.findByIdentifier(accountId)).thenReturn(Optional.of(account));
        when(accountHolderRepository.getAccountHolder(newHolderDTO.getId())).thenReturn(newHolder);
        when(accountOwnershipRepository.findByAccountAndHolderAndStatus(account, oldHolder, AccountOwnershipStatus.ACTIVE))
                .thenReturn(List.of(ownership));
        when(accountHolderRepresentativeRepository.getAccountHolderRepresentatives(oldHolder.getIdentifier()))
                .thenReturn(accountHolderRepresentatives);
        when(accountHolderRepository.getAccountHolderFiles(oldHolder.getIdentifier()))
                .thenReturn(List.of(createAccountHolderFile(accountHolderFileId)));
        when(accountOwnershipRepository.findByHolderAndStatus(oldHolder, AccountOwnershipStatus.INACTIVE)).thenReturn(inactiveOwnerships);

        // When
        TaskCompleteResponse response = accountHolderChangeTaskService.complete(taskDTO, TaskOutcome.APPROVED, "ok");

        // Then
        assertThat(response).isNotNull();
        assertThat(account.getAccountHolder()).isEqualTo(newHolder);

        verify(accountOwnershipRepository).delete(ownership);
        verify(accountOwnershipRepository).findByHolderAndStatus(oldHolder, AccountOwnershipStatus.INACTIVE);
        verify(accountOwnershipRepository).deleteAll(inactiveOwnerships);
        verify(accountHolderRepository).delete(oldHolder);
        verify(eventService).createAndPublishEvent(
                eq(accountId.toString()),
                eq(currentUser.getUrid()),
                contains("From"),
                any(),
                contains("approved")
        );
        verify(accountHolderRepresentativeRepository).getAccountHolderRepresentatives(oldHolder.getIdentifier());
        verify(accountHolderRepresentativeRepository).deleteAll(accountHolderRepresentatives);
        verify(accountHolderFileRepository).deleteAllById(Collections.singleton(accountHolderFileId));
    }

    private AccountHolderFileDTO createAccountHolderFile(Long accountHolderFileId) {
        return new AccountHolderFileDTO(accountHolderFileId, "file", "SUBMITTED", LocalDateTime.now());
    }

    private AccountHolderRepresentative createAccountHolderRepresentative() {
        AccountHolderRepresentative representative = new AccountHolderRepresentative();
        representative.setAccountHolder(new AccountHolder());
        representative.getAccountHolder().setId(1L);
        representative.getAccountHolder().setName("Representative");
        return representative;
    }

    @Test
    void complete_shouldHandleRejectionProperly() {
        // Given
        Long accountId = 999L;
        User currentUser = new User();
        currentUser.setUrid("rejectUser");

        AccountHolder holder = new AccountHolder();
        holder.setId(55L);
        holder.setName("Holder");

        Account account = new Account();
        account.setIdentifier(accountId);
        account.setAccountHolder(holder);

        AccountHolderChangeAction action = mock(AccountHolderChangeAction.class);
        AccountHolderDTO dto = new AccountHolderDTO();
        dto.setId(77L);
        DetailsDTO detailsDTO = new DetailsDTO();
        detailsDTO.setName("New Holder Name");
        dto.setDetails(detailsDTO);
        when(action.getAccountHolderDTO()).thenReturn(dto);

        var taskDTO = new AccountHolderChangeTaskDetailsDTO(new TaskDetailsDTO());
        taskDTO.setAccountNumber(String.valueOf(accountId));
        taskDTO.setAction(action);

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(accountRepository.findByIdentifier(accountId)).thenReturn(Optional.of(account));

        // When
        TaskCompleteResponse response = accountHolderChangeTaskService.complete(taskDTO, TaskOutcome.REJECTED, "rejected");

        // Then
        assertThat(response).isNotNull();
        assertThat(account.getAccountHolder()).isEqualTo(holder); // holder unchanged

        verify(accountHolderChangeValidationService, never())
                .validateChangeAccountHolderTaskForAccountIdentifier(any(), any(), anyBoolean());
        verify(accountOwnershipRepository, never()).save(any());
        verify(accountHolderRepository, never()).delete(any());

        verify(eventService).createAndPublishEvent(
                eq(accountId.toString()),
                eq(currentUser.getUrid()),
                contains("From"),
                any(),
                contains("rejected")
        );
    }
}
