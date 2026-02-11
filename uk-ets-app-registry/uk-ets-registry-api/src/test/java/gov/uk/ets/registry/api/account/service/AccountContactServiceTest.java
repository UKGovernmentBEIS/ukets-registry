package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountContactRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountClaimDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.RegistryContactDTO;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.ar.service.dto.ARUpdateActionDTO;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountContactServiceTest {


    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountAccessRepository accountAccessRepository;
    @Mock
    private AccountContactRepository accountContactRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AccountHolderRepresentativeRepository accountHolderRepresentativeRepository;
    @Mock
    private AuthorizedRepresentativeService authorizedRepresentativeService;
    @Mock
    private UserService userService;
    @Captor
    ArgumentCaptor<ARUpdateActionDTO> updateActionCaptor;
    @InjectMocks
    private AccountContactService accountContactService;


    @Test
    @DisplayName("sendInvitation: successfully updates invitedOn timestamps")
    void test_sendInvitation_success() {

        final Long accountIdentifier = 999L;
        final String accountHolderId = "99";
        final String accountClaimCode = "ACC123456789";
        final String metsContactMail = "john@example.com";
        final String registryContactMail = "jane@example.com";

        MetsContactDTO metsContact = MetsContactDTO.builder()
                .fullName("John Doe")
                .email(metsContactMail)
                .build();
        RegistryContactDTO registryContact = RegistryContactDTO.builder()
                .fullName("Jane Doe")
                .email(registryContactMail)
                .build();

        AccountDetailsDTO accountDetails = new AccountDetailsDTO();
        accountDetails.setAccountHolderId(accountHolderId);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setIdentifier(accountIdentifier);
        accountDTO.setAccountDetails(accountDetails);
        accountDTO.setAccountClaimCode(accountClaimCode);
        accountDTO.setMetsContacts(List.of(metsContact));
        accountDTO.setRegistryContacts(List.of(registryContact));

        AccountContactSendInvitationDTO sendDTO = new AccountContactSendInvitationDTO();
        sendDTO.setMetsContacts(Set.of(metsContact));
        sendDTO.setRegistryContacts(Set.of(registryContact));

        // Mock repository entities
        MetsAccountContact metsAccountContactEntity = new MetsAccountContact();
        metsAccountContactEntity.setEmailAddress(metsContactMail);

        AccountHolderRepresentative rep = new AccountHolderRepresentative();
        Contact repContact = new Contact();
        repContact.setEmailAddress(registryContactMail);
        rep.setContact(repContact);

        when(accountContactRepository.findByAccountIdentifier(accountIdentifier))
                .thenReturn(List.of(metsAccountContactEntity));

        when(accountHolderRepresentativeRepository.getAccountHolderRepresentatives(Long.valueOf(accountHolderId)))
                .thenReturn(List.of(rep));
        when(accountRepository.findByIdentifier(accountIdentifier)).thenReturn(Optional.of(new Account()));

        String result = accountContactService.sendInvitation(accountIdentifier, accountDTO, sendDTO);

        assertEquals(accountClaimCode, result);
        assertNotNull(metsAccountContactEntity.getInvitedOn());
    }

    @Test
    @DisplayName("sendInvitation: fails when account has no contacts")
    void test_sendInvitation_fails_noContactsOnAccount() {

        AccountDTO accountDTO = mock(AccountDTO.class);
        when(accountDTO.getMetsContacts()).thenReturn(List.of());
        when(accountDTO.getRegistryContacts()).thenReturn(List.of());

        AccountContactSendInvitationDTO sendDTO = new AccountContactSendInvitationDTO();
        sendDTO.setMetsContacts(Set.of(MetsContactDTO.builder()
                .email("john@example.com")
                .build()));
        sendDTO.setRegistryContacts(Set.of(RegistryContactDTO.builder()
                .email("jane@example.com")
                .build()));

        AccountActionException ex = assertThrows(
                AccountActionException.class,
                () -> accountContactService.sendInvitation(1L, accountDTO, sendDTO)
        );

        assertThat(ex.getMessage()).isEqualTo("Account must have at least one METS or Registry contact to send invitation to.");
    }

    @Test
    @DisplayName("sendInvitation: fails when invitation has no contacts")
    void test_sendInvitation_fails_noContactsOnInvitation() {

        AccountDTO accountDTO = mock(AccountDTO.class);
        when(accountDTO.getMetsContacts()).thenReturn(List.of(MetsContactDTO.builder()
                .email("john@example.com")
                .build()));


        AccountContactSendInvitationDTO sendDTO = new AccountContactSendInvitationDTO();
        sendDTO.setMetsContacts(Collections.emptySet());
        sendDTO.setRegistryContacts(Collections.emptySet());

        AccountActionException ex = assertThrows(
                AccountActionException.class,
                () -> accountContactService.sendInvitation(1L, accountDTO, sendDTO)
        );

        assertThat(ex.getMessage()).isEqualTo("At least one contact must be specified.");
    }

    @Test
    @DisplayName("sendInvitation: fails when specified contacts are not related to the account")
    void test_sendInvitation_fails_mismatchedContacts() {

        AccountDTO accountDTO = mock(AccountDTO.class);
        when(accountDTO.getMetsContacts()).thenReturn(List.of(MetsContactDTO.builder()
                .fullName("John Doe")
                .email("joe@example.com")
                .build()));

        AccountContactSendInvitationDTO sendDTO = new AccountContactSendInvitationDTO();
        sendDTO.setMetsContacts(Set.of(MetsContactDTO.builder()
                .fullName("Non existing")
                .email("wrong@x.com")
                .build()));
        sendDTO.setRegistryContacts(Collections.emptySet());

        AccountActionException ex = assertThrows(
                AccountActionException.class,
                () -> accountContactService.sendInvitation(1L, accountDTO, sendDTO)
        );

        assertThat(ex.getMessage()).isEqualTo("The specified contacts are not related with this account.");
    }

    @Test
    @DisplayName("claimAccount: success path adds AR and places update request")
    void test_claimAccount_success() {

        final Long registryId = 100L;
        final Long accountId = 1L;
        final Long accountIdentifier = 2L;
        final String accountClaimCode = "ACC123456789";
        final String urid = "UK123456789";

        AccountClaimDTO claimDTO = new AccountClaimDTO();
        claimDTO.setRegistryId(registryId);
        claimDTO.setAccountClaimCode(accountClaimCode);

        Account account = new Account();
        account.setId(accountId);
        account.setIdentifier(accountIdentifier);

        when(accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(registryId, accountClaimCode))
                .thenReturn(Optional.of(account));

        when(accountAccessRepository.finARsByAccount_Identifier(accountIdentifier))
                .thenReturn(List.of());

        when(taskRepository.countPendingTasksByAccountIdInAndType(
                List.of(accountId), List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST)))
                .thenReturn(0L);

        User user = new User();
        user.setUrid(urid);
        when(userService.getCurrentUser()).thenReturn(user);

        when(authorizedRepresentativeService.placeUpdateRequest(any(ARUpdateActionDTO.class)))
                .thenReturn(777L);

        Long result = accountContactService.claimAccount(claimDTO);

        assertEquals(777L, result);
        verify(authorizedRepresentativeService).placeUpdateRequest(updateActionCaptor.capture());
        final ARUpdateActionDTO updateActionCaptorValue = updateActionCaptor.getValue();
        assertEquals(accountIdentifier, updateActionCaptorValue.getAccountIdentifier());
        assertEquals(urid, updateActionCaptorValue.getCandidate().getUrid());
        assertEquals(AccountAccessRight.SURRENDER_INITIATE_AND_APPROVE, updateActionCaptorValue.getCandidate().getRight());
        assertEquals(ARUpdateActionType.ADD, updateActionCaptorValue.getUpdateType());
    }

    @Test
    @DisplayName("claimAccount: fails when account not found")
    void test_claimAccount_accountNotFound() {

        when(accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        AccountClaimDTO claimDTO = new AccountClaimDTO();
        claimDTO.setRegistryId(1L);
        claimDTO.setAccountClaimCode("ACC000000000");

        final AccountActionException ex =
                assertThrows(AccountActionException.class, () -> accountContactService.claimAccount(claimDTO));
        assertThat(ex.getMessage()).isEqualTo("The claim account code, or the METS Registry ID is incorrect.");
        verifyNoInteractions(authorizedRepresentativeService, accountAccessRepository, taskRepository);
    }

    @Test
    @DisplayName("claimAccount: fails when account already claimed-already existing AR")
    void test_claimAccount_alreadyClaimed_existingAR() {

        final Long registryId = 100L;
        final Long accountId = 1L;
        final Long accountIdentifier = 2L;
        final String accountClaimCode = "ACC123456789";

        AccountClaimDTO claimDTO = new AccountClaimDTO();
        claimDTO.setRegistryId(registryId);
        claimDTO.setAccountClaimCode(accountClaimCode);

        Account account = new Account();
        account.setId(accountId);
        account.setIdentifier(accountIdentifier);

        when(accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(registryId, accountClaimCode))
                .thenReturn(Optional.of(account));

        AccountAccess access = new AccountAccess();
        access.setAccount(account);
        access.setState(AccountAccessState.ACTIVE);
        when(accountAccessRepository.finARsByAccount_Identifier(accountIdentifier))
                .thenReturn(List.of(access));

        when(taskRepository.countPendingTasksByAccountIdInAndType(
                List.of(accountId), List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST)))
                .thenReturn(0L);

        final AccountActionException ex =
                assertThrows(AccountActionException.class, () -> accountContactService.claimAccount(claimDTO));

        assertThat(ex.getMessage()).isEqualTo("The account has already been claimed.");
        verifyNoInteractions(authorizedRepresentativeService);
    }

    @Test
    @DisplayName("claimAccount: fails when account already claimed-pending AR tasks")
    void test_claimAccount_alreadyClaimed_pendingARTasks() {

        final Long registryId = 100L;
        final Long accountId = 1L;
        final Long accountIdentifier = 2L;
        final String accountClaimCode = "ACC123456789";

        AccountClaimDTO claimDTO = new AccountClaimDTO();
        claimDTO.setRegistryId(registryId);
        claimDTO.setAccountClaimCode(accountClaimCode);

        Account account = new Account();
        account.setId(accountId);
        account.setIdentifier(accountIdentifier);

        when(accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(registryId, accountClaimCode))
                .thenReturn(Optional.of(account));

        when(accountAccessRepository.finARsByAccount_Identifier(accountIdentifier))
                .thenReturn(Collections.emptyList());

        when(taskRepository.countPendingTasksByAccountIdInAndType(
                List.of(accountId), List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST)))
                .thenReturn(1L);

        final AccountActionException ex =
                assertThrows(AccountActionException.class, () -> accountContactService.claimAccount(claimDTO));

        assertThat(ex.getMessage()).isEqualTo("The account has already been claimed.");
        verifyNoInteractions(authorizedRepresentativeService);
    }
}
