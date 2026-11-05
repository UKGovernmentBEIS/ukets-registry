package gov.uk.ets.registry.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import gov.uk.ets.registry.api.account.domain.types.AccountContactType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.RegistryContactInvitationStatusDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AccountRepresentativeInvitation;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.account.repository.AccountContactRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepresentativeInvitationRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountClaimDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.BulkClaimResult;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactChangeInvitationStatusDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.ContactInvitationStatusDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.RegistryContactDTO;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;

@ExtendWith(MockitoExtension.class)
public class AccountClaimServiceTest {

    private final String HELP_DESK_MAIL = "etregistryhelp@environment-agency.gov.uk";

    @Mock
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountSendInvitationService accountSendInvitationService;

    @Mock
    private AccountClaimProcessor processor;

    @Mock
    private AccountContactRepository accountContactRepository;

    @Mock
    private AccountRepresentativeInvitationRepository accountRepresentativeInvitationRepository;

    @InjectMocks
    private AccountClaimService accountClaimService;

    @Test
    void sendInvitation_shouldSendInvitation_whenAviationAccountClaimEnabled() {
        Long accountIdentifier = 1L;
        AccountContactSendInvitationDTO dto = new AccountContactSendInvitationDTO();

        Account account = new Account();
        account.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);

        when(processor.processSendInvitation(accountIdentifier, dto, true))
                .thenReturn("ACCOUNT_CLAIM_CODE");

        String result = accountClaimService.sendInvitation(accountIdentifier, dto, true);

        assertEquals("ACCOUNT_CLAIM_CODE", result);

        verify(processor).processSendInvitation(accountIdentifier, dto, true);
        verifyNoMoreInteractions(processor);
    }

    @Test
    void claimAccount_shouldClaimAccount_whenInstallationAccountEnabled() {
        AccountClaimDTO dto = new AccountClaimDTO();
        dto.setRegistryId("100");
        dto.setAccountClaimCode("CLAIM_CODE");

        Account account = new Account();
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);

        when(accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(
                Long.parseLong(dto.getRegistryId()), dto.getAccountClaimCode()))
                .thenReturn(Optional.of(account));

        when(accountService.claimAccount(dto)).thenReturn(99L);
        when(processor.isAccountClaimEnabled(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT))
                .thenReturn(true);

        Long result = accountClaimService.claimAccount(dto);

        assertEquals(99L, result);

        verify(accountRepository)
                .findByCompliantEntityIdentifierAndAccountClaimCode(
                        Long.parseLong(dto.getRegistryId()), dto.getAccountClaimCode());
        verify(accountService).claimAccount(dto);
    }

    @Test
    void claimAccount_shouldClaimAccount_whenAccountTypeIsNull() {
        AccountClaimDTO dto = new AccountClaimDTO();
        dto.setRegistryId("200");
        dto.setAccountClaimCode("INVALID");

        when(accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(
                Long.parseLong(dto.getRegistryId()), dto.getAccountClaimCode()))
                .thenReturn(Optional.empty());

        accountClaimService.claimAccount(dto);

        verify(accountRepository)
                .findByCompliantEntityIdentifierAndAccountClaimCode(
                        Long.parseLong(dto.getRegistryId()), dto.getAccountClaimCode());
        verify(accountService).claimAccount(dto);
    }

    @Test
    void claimAccount_invalid_registryId_format_shouldThrowException() {
        AccountClaimDTO dto = new AccountClaimDTO();
        dto.setRegistryId("MA00095");
        dto.setAccountClaimCode("CLAIM_CODE");

        AccountActionException exception = assertThrows(
                AccountActionException.class,
                () -> accountClaimService.claimAccount(dto));

        assertThat(exception.getMessage()).isEqualTo("The claim account code, or the METS Registry ID is incorrect.");

        verifyNoInteractions(accountRepository);
        verifyNoInteractions(accountService);
    }

    @Test
    void countEligibleBulkClaimAccounts_shouldReturnZero_whenAllFlagsDisabled() {

        when(processor.getEnabledAccountTypes())
                .thenReturn(Collections.emptyList());

        Long result = accountClaimService.countEligibleBulkClaimAccounts();

        assertEquals(0L, result);
        verifyNoInteractions(accountRepository);
    }

    @Test
    void countEligibleBulkClaimAccounts_shouldCallRepository_withInstallationTypeOnly() {

        when(accountRepository.countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT),
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        )).thenReturn(5L);
        when(processor.getEnabledAccountTypes())
                .thenReturn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT));

        Long result = accountClaimService.countEligibleBulkClaimAccounts();

        assertEquals(5L, result);

        verify(accountRepository).countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT),
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        );
    }

    @Test
    void countEligibleBulkClaimAccounts_shouldCallRepository_withAviationTypeOnly() {

        when(accountRepository.countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                List.of(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT),
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        )).thenReturn(3L);
        when(processor.getEnabledAccountTypes())
                .thenReturn(List.of(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT));

        Long result = accountClaimService.countEligibleBulkClaimAccounts();

        assertEquals(3L, result);
        verify(accountRepository).countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                List.of(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT),
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        );
    }

    @Test
    void countEligibleBulkClaimAccounts_shouldCallRepository_withMaritimeTypeOnly() {

        when(accountRepository.countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                List.of(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT),
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        )).thenReturn(2L);
        when(processor.getEnabledAccountTypes())
                .thenReturn(List.of(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT));

        Long result = accountClaimService.countEligibleBulkClaimAccounts();

        assertEquals(2L, result);
        verify(accountRepository).countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                List.of(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT),
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        );
    }

    @Test
    void countEligibleBulkClaimAccounts_shouldCallRepository_withAllEnabledTypes() {


        List<RegistryAccountType> expectedTypes = List.of(
                RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT
        );

        when(accountRepository.countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                expectedTypes,
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        )).thenReturn(10L);
        when(processor.getEnabledAccountTypes())
                .thenReturn(expectedTypes);

        Long result = accountClaimService.countEligibleBulkClaimAccounts();

        assertEquals(10L, result);

        verify(accountRepository).countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                expectedTypes,
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        );
    }

    @Test
    void countEligibleBulkClaimAccounts_shouldReturnZero_whenRepositoryReturnsZero() {

        when(accountRepository.countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT),
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        )).thenReturn(0L);
        when(processor.getEnabledAccountTypes())
                .thenReturn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT));

        Long result = accountClaimService.countEligibleBulkClaimAccounts();

        assertEquals(0L, result);
        verify(accountRepository).countAccountsWithoutActiveARsAndPendingTasksWithContacts(
                List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT),
                List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
                HELP_DESK_MAIL
        );
    }

    @Test
    void shouldReturnZeroWhenNoAccountTypesEnabled() {

        when(processor.getEnabledAccountTypes())
                .thenReturn(Collections.emptyList());

        BulkClaimResult result = accountClaimService.sendBulkClaimInvitations();

        assertEquals(0, result.getTotal());
        assertEquals(0, result.getSuccessful());
        assertEquals(0, result.getFailed());
        verifyNoInteractions(accountRepository, accountService, accountSendInvitationService);
    }

    @Test
    void shouldReturnZeroWhenNoAccountsFound() {

        when(processor.getEnabledAccountTypes())
                .thenReturn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT));

        when(accountRepository.findAccountIdentifierWithoutActiveARsAndPendingTaskWithContacts(
                anyList(),
                anyList(),
                anyString()
        )).thenReturn(List.of());

        BulkClaimResult result = accountClaimService.sendBulkClaimInvitations();

        assertEquals(0, result.getTotal());
        assertEquals(0, result.getSuccessful());
        assertEquals(0, result.getFailed());

        verifyNoInteractions(accountService, accountSendInvitationService);
    }

    @Test
    void shouldSendInvitationsSuccessfully() {

        Long accountIdentifier = 1L;

        when(processor.getEnabledAccountTypes())
                .thenReturn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT));

        AccountDTO dto = mock(AccountDTO.class);
        when(dto.getMetsContacts()).thenReturn(List.of(MetsContactDTO.builder().email("a").invitationStatus(true).build()));
        when(dto.getRegistryContacts()).thenReturn(List.of(RegistryContactDTO.builder().email("b").build()));

        when(accountRepository.findAccountIdentifierWithoutActiveARsAndPendingTaskWithContacts(
                anyList(),
                anyList(),
                anyString()
        )).thenReturn(List.of(accountIdentifier));

        when(accountService.getAccountDTOWithoutAuthorization(accountIdentifier)).thenReturn(dto);

        BulkClaimResult result = accountClaimService.sendBulkClaimInvitations();

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getSuccessful());
        assertEquals(0, result.getFailed());

        verify(accountSendInvitationService)
                .sendInvitation(eq(accountIdentifier), any(AccountContactSendInvitationDTO.class));
    }

    @Test
    void shouldCountFailedWhenExceptionOccurs() {

        Long accountIdentifier = 1L;
        when(processor.getEnabledAccountTypes())
                .thenReturn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT));

        AccountDTO dto = mock(AccountDTO.class);
        when(dto.getMetsContacts()).thenReturn(List.of(MetsContactDTO.builder().email("a").invitationStatus(true).build()));
        when(dto.getRegistryContacts()).thenReturn(List.of(RegistryContactDTO.builder().email("b").invitationStatus(true).build()));

        when(accountRepository.findAccountIdentifierWithoutActiveARsAndPendingTaskWithContacts(
                anyList(),
                anyList(),
                anyString()
        )).thenReturn(List.of(accountIdentifier));

        when(accountService.getAccountDTOWithoutAuthorization(accountIdentifier))
                .thenReturn(dto);

        doThrow(new AccountActionException())
                .when(accountSendInvitationService)
                .sendInvitation(eq(accountIdentifier), any(AccountContactSendInvitationDTO.class));

        BulkClaimResult result = accountClaimService.sendBulkClaimInvitations();

        assertEquals(1, result.getTotal());
        assertEquals(0, result.getSuccessful());
        assertEquals(1, result.getFailed());

        verify(accountSendInvitationService)
                .sendInvitation(eq(accountIdentifier), any(AccountContactSendInvitationDTO.class));
        verifyNoMoreInteractions(accountSendInvitationService);
    }

    @Test
    void shouldSkipAccountsWithNoContacts() {

        Long accountIdentifier = 1L;

        when(processor.getEnabledAccountTypes())
                .thenReturn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT));

        AccountDTO dto = mock(AccountDTO.class);
        when(dto.getMetsContacts()).thenReturn(List.of());
        when(dto.getRegistryContacts()).thenReturn(List.of());

        when(accountRepository.findAccountIdentifierWithoutActiveARsAndPendingTaskWithContacts(
                anyList(),
                anyList(),
                anyString()
        )).thenReturn(List.of(accountIdentifier));

        when(accountService.getAccountDTOWithoutAuthorization(accountIdentifier)).thenReturn(dto);

        BulkClaimResult result = accountClaimService.sendBulkClaimInvitations();

        assertEquals(1, result.getTotal());
        assertEquals(0, result.getSuccessful());
        assertEquals(0, result.getFailed());

        verifyNoInteractions(accountSendInvitationService);
    }

    @Test
    void shouldHandleMixedResults() {

        Long identifier1 = 1L;
        Long identifier2 = 2L;
        Long identifier3 = 3L;

        when(processor.getEnabledAccountTypes())
                .thenReturn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                        RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                        RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT));

        when(accountRepository.findAccountIdentifierWithoutActiveARsAndPendingTaskWithContacts(
                anyList(),
                anyList(),
                anyString()
        )).thenReturn(List.of(identifier1, identifier2, identifier3));

        // identifier1 → success
        AccountDTO dto1 = mock(AccountDTO.class);
        when(dto1.getMetsContacts()).thenReturn(List.of(MetsContactDTO.builder().email("a").invitationStatus(true).build()));
        when(dto1.getRegistryContacts()).thenReturn(List.of(RegistryContactDTO.builder().email("b").invitationStatus(true).build()));

        // id2 → exception
        AccountDTO dto2 = mock(AccountDTO.class);
        when(dto2.getMetsContacts()).thenReturn(List.of(MetsContactDTO.builder().email("a").invitationStatus(true).build()));
        when(dto2.getRegistryContacts()).thenReturn(List.of(RegistryContactDTO.builder().email("b").invitationStatus(true).build()));

        // id3 → no contacts
        AccountDTO dto3 = mock(AccountDTO.class);
        when(dto3.getMetsContacts()).thenReturn(List.of());
        when(dto3.getRegistryContacts()).thenReturn(List.of());

        when(accountService.getAccountDTOWithoutAuthorization(identifier1)).thenReturn(dto1);
        when(accountService.getAccountDTOWithoutAuthorization(identifier2)).thenReturn(dto2);
        when(accountService.getAccountDTOWithoutAuthorization(identifier3)).thenReturn(dto3);

        doNothing()
                .when(accountSendInvitationService)
                .sendInvitation(eq(identifier1), any(AccountContactSendInvitationDTO.class));

        doThrow(new AccountActionException())
                .when(accountSendInvitationService)
                .sendInvitation(eq(identifier2), any(AccountContactSendInvitationDTO.class));

        BulkClaimResult result = accountClaimService.sendBulkClaimInvitations();

        assertEquals(3, result.getTotal());
        assertEquals(1, result.getSuccessful());
        assertEquals(1, result.getFailed());

        verify(accountService, times(3)).getAccountDTOWithoutAuthorization(anyLong());
        verify(accountSendInvitationService, times(2)).sendInvitation(anyLong(), any(AccountContactSendInvitationDTO.class));
    }

    @Test
    void should_change_auto_send_invitation_Enabled() {

        final Long accountIdentifier = 1L;
        Installation installation = new Installation();
        installation.setAutoSendClaimInvitationEnabled(false);
        Account account = new Account();
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setCompliantEntity(installation);

        when(accountRepository.findByAccountIdentifierFetchCompliantEntity(accountIdentifier)).thenReturn(Optional.of(account));

        accountClaimService.changeAutoSendInvitationEnabled(accountIdentifier);
        assertTrue(installation.isAutoSendClaimInvitationEnabled());
        verify(accountRepository).findByAccountIdentifierFetchCompliantEntity(accountIdentifier);
    }

    @Test
    void change_auto_send_invitation_enabled_no_installation_should_throw_exception() {

        final Long accountIdentifier = 1L;
        Account account = new Account();
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);

        when(accountRepository.findByAccountIdentifierFetchCompliantEntity(accountIdentifier)).thenReturn(Optional.of(account));

        final AccountActionException accountActionException = assertThrows(AccountActionException.class,
                () -> accountClaimService.changeAutoSendInvitationEnabled(accountIdentifier));

        assertThat(accountActionException.getMessage())
                .isEqualTo("Cannot change auto-send invitation status. Account with identifier " + accountIdentifier + " has no compliant entity.");
        verify(accountRepository).findByAccountIdentifierFetchCompliantEntity(accountIdentifier);
    }

    @Test
    void change_auto_send_invitation_enabled_invalid_account_type_should_throw_exception() {

        final Long accountIdentifier = 1L;
        Account account = new Account();
        account.setRegistryAccountType(RegistryAccountType.TRADING_ACCOUNT);

        when(accountRepository.findByAccountIdentifierFetchCompliantEntity(accountIdentifier)).thenReturn(Optional.of(account));

        final AccountActionException accountActionException = assertThrows(AccountActionException.class,
                () -> accountClaimService.changeAutoSendInvitationEnabled(accountIdentifier));

        assertThat(accountActionException.getMessage())
                .isEqualTo("Cannot change auto-send invitation status. Account type "
                        + account.getRegistryAccountType() + " is not supported.");
        verify(accountRepository).findByAccountIdentifierFetchCompliantEntity(accountIdentifier);
    }
    
    @Test
    void changeContactsInvitationStatus() {

        final Long accountIdentifier = 1L;
        final String metsEmail = "mets@email.com";
        final String registryEmail = "registry@email.com";

        final AccountContactChangeInvitationStatusDTO accountContactChangeInvitationStatusDTO = AccountContactChangeInvitationStatusDTO.builder()
                .metsContacts(List.of(ContactInvitationStatusDTO.builder().email(metsEmail).invitationStatus(false).build()))
                .registryContacts(List.of(RegistryContactInvitationStatusDTO.builder().email(registryEmail).invitationStatus(true).build()))
                .build();

        final MetsAccountContact metsAccountContact = MetsAccountContact.builder()
                .emailAddress(metsEmail)
                .invitationStatus(true)
                .build();

        Contact contact = new Contact();
        contact.setEmailAddress(registryEmail);

        AccountHolderRepresentative representative =
            new AccountHolderRepresentative();
        representative.setContact(contact);
        
        final AccountRepresentativeInvitation representativeInvitation = AccountRepresentativeInvitation.builder()
                .accountHolderRepresentative(representative)
                .invitationStatus(false)
                .build();

        when(accountContactRepository.findByAccountIdentifier(accountIdentifier)).thenReturn(List.of(metsAccountContact));
        when(accountRepresentativeInvitationRepository.findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(accountIdentifier,
                Set.of(registryEmail)))
                .thenReturn(List.of(representativeInvitation));

        accountClaimService.changeContactsInvitationStatus(accountIdentifier, accountContactChangeInvitationStatusDTO);

        assertFalse(metsAccountContact.isInvitationStatus());
        assertTrue(representativeInvitation.isInvitationStatus());
    }


    @Test
    void changeContactsInvitationStatus_sameEmailOnlyPrimarySelected() {
        final Long accountIdentifier = 1L;
        final String registryEmail = "registry@email.com";

        final AccountContactChangeInvitationStatusDTO dto =
                AccountContactChangeInvitationStatusDTO.builder()
                        .registryContacts(List.of(
                                RegistryContactInvitationStatusDTO.builder()
                                        .email(registryEmail)
                                        .contactType(AccountContactType.PRIMARY)
                                        .invitationStatus(false)
                                        .build()))
                        .build();

        final AccountRepresentativeInvitation primaryInvitation =
                createInvitation(registryEmail, AccountContactType.PRIMARY, true);

        final AccountRepresentativeInvitation alternativeInvitation =
                createInvitation(registryEmail, AccountContactType.ALTERNATIVE, true);

        when(accountRepresentativeInvitationRepository
                .findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(
                        accountIdentifier, Set.of(registryEmail)))
                .thenReturn(List.of(primaryInvitation, alternativeInvitation));

        accountClaimService.changeContactsInvitationStatus(accountIdentifier, dto);

        assertFalse(primaryInvitation.isInvitationStatus());
        assertTrue(alternativeInvitation.isInvitationStatus());
    }


    @Test
    void changeContactsInvitationStatus_sameEmailOnlyAlternativeSelected() {
        final Long accountIdentifier = 1L;
        final String registryEmail = "registry@email.com";

        final AccountContactChangeInvitationStatusDTO dto =
                AccountContactChangeInvitationStatusDTO.builder()
                        .registryContacts(List.of(
                                RegistryContactInvitationStatusDTO.builder()
                                        .email(registryEmail)
                                        .contactType(AccountContactType.ALTERNATIVE)
                                        .invitationStatus(false)
                                        .build()))
                        .build();

        final AccountRepresentativeInvitation primaryInvitation =
                createInvitation(registryEmail, AccountContactType.PRIMARY, true);

        final AccountRepresentativeInvitation alternativeInvitation =
                createInvitation(registryEmail, AccountContactType.ALTERNATIVE, true);

        when(accountRepresentativeInvitationRepository
                .findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(
                        accountIdentifier, Set.of(registryEmail)))
                .thenReturn(List.of(primaryInvitation, alternativeInvitation));

        accountClaimService.changeContactsInvitationStatus(accountIdentifier, dto);

        assertTrue(primaryInvitation.isInvitationStatus());
        assertFalse(alternativeInvitation.isInvitationStatus());
    }


    @Test
    void changeContactsInvitationStatus_sameEmailBothContactsSelected() {
        final Long accountIdentifier = 1L;
        final String registryEmail = "registry@email.com";

        final AccountContactChangeInvitationStatusDTO dto =
                AccountContactChangeInvitationStatusDTO.builder()
                        .registryContacts(List.of(
                                RegistryContactInvitationStatusDTO.builder()
                                        .email(registryEmail)
                                        .contactType(AccountContactType.PRIMARY)
                                        .invitationStatus(false)
                                        .build(),
                                RegistryContactInvitationStatusDTO.builder()
                                        .email(registryEmail)
                                        .contactType(AccountContactType.ALTERNATIVE)
                                        .invitationStatus(true)
                                        .build()))
                        .build();

        final AccountRepresentativeInvitation primaryInvitation =
                createInvitation(registryEmail, AccountContactType.PRIMARY, true);

        final AccountRepresentativeInvitation alternativeInvitation =
                createInvitation(registryEmail, AccountContactType.ALTERNATIVE, false);

        when(accountRepresentativeInvitationRepository
                .findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(
                        accountIdentifier, Set.of(registryEmail)))
                .thenReturn(List.of(primaryInvitation, alternativeInvitation));

        accountClaimService.changeContactsInvitationStatus(accountIdentifier, dto);

        assertFalse(primaryInvitation.isInvitationStatus());
        assertTrue(alternativeInvitation.isInvitationStatus());
    }


    @Test
    void changeContactsInvitationStatus_differentEmails() {
        final Long accountIdentifier = 1L;
        final String primaryEmail = "primary@email.com";
        final String alternativeEmail = "alternative@email.com";

        final AccountContactChangeInvitationStatusDTO dto =
                AccountContactChangeInvitationStatusDTO.builder()
                        .registryContacts(List.of(
                                RegistryContactInvitationStatusDTO.builder()
                                        .email(primaryEmail)
                                        .contactType(AccountContactType.PRIMARY)
                                        .invitationStatus(false)
                                        .build(),
                                RegistryContactInvitationStatusDTO.builder()
                                        .email(alternativeEmail)
                                        .contactType(AccountContactType.ALTERNATIVE)
                                        .invitationStatus(false)
                                        .build()))
                        .build();

        final AccountRepresentativeInvitation primaryInvitation =
                createInvitation(primaryEmail, AccountContactType.PRIMARY, true);

        final AccountRepresentativeInvitation alternativeInvitation =
                createInvitation(alternativeEmail, AccountContactType.ALTERNATIVE, true);

        when(accountRepresentativeInvitationRepository
                .findByAccount_IdentifierAndAccountHolderRepresentative_Contact_EmailAddressIn(
                        accountIdentifier, Set.of(primaryEmail, alternativeEmail)))
                .thenReturn(List.of(primaryInvitation, alternativeInvitation));

        accountClaimService.changeContactsInvitationStatus(accountIdentifier, dto);

        assertFalse(primaryInvitation.isInvitationStatus());
        assertFalse(alternativeInvitation.isInvitationStatus());
    }


    @Test
    void changeContactsInvitationStatus_noRegistryContacts() {
        final Long accountIdentifier = 1L;
        final String metsEmail = "mets@email.com";

        final AccountContactChangeInvitationStatusDTO dto =
                AccountContactChangeInvitationStatusDTO.builder()
                        .metsContacts(List.of(
                                ContactInvitationStatusDTO.builder()
                                        .email(metsEmail)
                                        .invitationStatus(false)
                                        .build()))
                        .registryContacts(List.of())
                        .build();

        final MetsAccountContact metsAccountContact =
                MetsAccountContact.builder()
                        .emailAddress(metsEmail)
                        .invitationStatus(true)
                        .build();

        when(accountContactRepository.findByAccountIdentifier(accountIdentifier))
                .thenReturn(List.of(metsAccountContact));

        accountClaimService.changeContactsInvitationStatus(accountIdentifier, dto);

        assertFalse(metsAccountContact.isInvitationStatus());

        verifyNoInteractions(accountRepresentativeInvitationRepository);
    }


    private AccountRepresentativeInvitation createInvitation(String email, AccountContactType contactType,
                                                             boolean invitationStatus) {

        Contact contact = new Contact();
        contact.setEmailAddress(email);

        AccountHolderRepresentative representative =
                new AccountHolderRepresentative();
        representative.setContact(contact);
        representative.setAccountContactType(contactType);

        return AccountRepresentativeInvitation.builder()
                .accountHolderRepresentative(representative)
                .invitationStatus(invitationStatus)
                .build();
    }

}
