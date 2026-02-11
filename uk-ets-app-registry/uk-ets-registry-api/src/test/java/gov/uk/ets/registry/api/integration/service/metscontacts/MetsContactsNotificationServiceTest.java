package gov.uk.ets.registry.api.integration.service.metscontacts;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountContactService;
import gov.uk.ets.registry.api.account.service.AccountDTOFactory;
import gov.uk.ets.registry.api.account.service.AccountGeneratorService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MetsContactsNotificationServiceTest {

    @Mock
    private AccountDTOFactory accountDTOFactory;

    @Mock
    private AccountContactService accountContactService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MetsContactMapper metsContactMapper;

    @Mock
    private AccountGeneratorService accountGeneratorService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private MetsContactsNotificationService service;

    @Test
    public void shouldNotSendInvitationWhenNoEligibleContacts() throws Exception {

        Account account = new Account();
        account.setIdentifier(100L);

        MetsAccountContact invited = new MetsAccountContact();
        invited.setEmailAddress("test@test.com");
        invited.setInvitedOn(LocalDateTime.now());
        account.setMetsAccountContacts(List.of(invited));
        MetsContactDTO incoming = new MetsContactDTO();
        incoming.setEmail("test@test.com");
        service.sendInvitation(account, List.of(incoming));
        verifyNoInteractions(accountContactService);
    }

    @Test
    void shouldSendInvitationForEligibleContacts() throws Exception {

        Account account = new Account();
        account.setId(1L);
        account.setIdentifier(100L);
        account.setAccountClaimCode("CLAIM123");

        MetsAccountContact contact = new MetsAccountContact();
        contact.setEmailAddress("invite@test.com");
        contact.setInvitedOn(null);

        account.setMetsAccountContacts(List.of(contact));

        MetsContactDTO incoming = new MetsContactDTO();
        incoming.setEmail("invite@test.com");

        AccountDTO accountDTO = new AccountDTO();
        MetsContactDTO metsDTO = new MetsContactDTO();
        metsDTO.setEmail("invite@test.com");

        when(accountDTOFactory.createMetsContactDTO(contact)).thenReturn(metsDTO);
        when(accountDTOFactory.create(account, true)).thenReturn(accountDTO);
        when(accountService.isAccountClaimApplicable(1L, 100L)).thenReturn(true);

        service.sendInvitation(account, List.of(incoming));

        verify(accountContactService).sendInvitation(
                eq(100L),
                eq(accountDTO),
                argThat(dto ->
                        dto.getMetsContacts().size() == 1 &&
                                dto.getMetsContacts().iterator().next().getEmail().equals("invite@test.com")
                )
        );
    }

    @Test
    void shouldGenerateClaimCodeWhenMissing() throws Exception {

        Account account = new Account();
        account.setId(1L);
        account.setIdentifier(100L);
        account.setAccountClaimCode(null);

        MetsAccountContact contact = new MetsAccountContact();
        contact.setEmailAddress("invite@test.com");
        contact.setInvitedOn(null);

        account.setMetsAccountContacts(List.of(contact));

        MetsContactDTO incoming = new MetsContactDTO();
        incoming.setEmail("invite@test.com");

        when(accountGeneratorService.generateAccountClaimCode())
                .thenReturn("NEWCLAIM");

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(accountDTOFactory.create(any(Account.class), eq(true)))
                .thenReturn(new AccountDTO());

        when(accountDTOFactory.createMetsContactDTO(contact))
                .thenReturn(incoming);

        when(accountService.isAccountClaimApplicable(1L, 100L)).thenReturn(true);

        service.sendInvitation(account, List.of(incoming));

        verify(accountGeneratorService).generateAccountClaimCode();
        verify(accountRepository).save(argThat(acc ->
                "NEWCLAIM".equals(acc.getAccountClaimCode())
        ));
    }

    @Test
    void shouldNotUpdateAccountWhenClaimCodeExists() throws Exception {

        Account account = new Account();
        account.setId(1L);
        account.setIdentifier(100L);
        account.setAccountClaimCode("EXISTING");

        MetsAccountContact contact = new MetsAccountContact();
        contact.setEmailAddress("invite@test.com");
        contact.setInvitedOn(null);

        account.setMetsAccountContacts(List.of(contact));

        MetsContactDTO incoming = new MetsContactDTO();
        incoming.setEmail("invite@test.com");

        when(accountDTOFactory.createMetsContactDTO(contact))
                .thenReturn(incoming);

        when(accountDTOFactory.create(account, true))
                .thenReturn(new AccountDTO());

        when(accountService.isAccountClaimApplicable(1L, 100L)).thenReturn(true);

        service.sendInvitation(account, List.of(incoming));

        verify(accountRepository, never()).save(any());
        verify(accountGeneratorService, never()).generateAccountClaimCode();
    }


}