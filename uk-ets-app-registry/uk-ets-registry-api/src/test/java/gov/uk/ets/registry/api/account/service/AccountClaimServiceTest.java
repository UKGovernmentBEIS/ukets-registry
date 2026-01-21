package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountClaimDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static gov.uk.ets.registry.api.transaction.domain.type.AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT;
import static gov.uk.ets.registry.api.transaction.domain.type.AccountType.OPERATOR_HOLDING_ACCOUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountClaimServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountClaimService accountClaimService;

    @Test
    void sendInvitation_shouldSendInvitation_whenAviationAccountClaimEnabled() {
        ReflectionTestUtils.setField(accountClaimService, "aviationAccountClaimEnabled", true);
        Long accountIdentifier = 1L;
        AccountContactSendInvitationDTO dto = new AccountContactSendInvitationDTO();

        Account account = new Account();
        account.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);

        when(accountRepository.findByIdentifier(accountIdentifier))
                .thenReturn(Optional.of(account));
        when(accountService.sendInvitation(accountIdentifier, dto))
                .thenReturn("SUCCESS");

        String result = accountClaimService.sendInvitation(accountIdentifier, dto);

        assertEquals("SUCCESS", result);

        verify(accountRepository).findByIdentifier(accountIdentifier);
        verify(accountService).sendInvitation(accountIdentifier, dto);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    void sendInvitation_shouldThrowException_whenAccountClaimDisabled() {

        Long accountIdentifier = 2L;
        AccountContactSendInvitationDTO dto = new AccountContactSendInvitationDTO();

        Account account = new Account();
        account.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);

        when(accountRepository.findByIdentifier(accountIdentifier))
                .thenReturn(Optional.of(account));

        AccountActionException exception = assertThrows(
                AccountActionException.class,
                () -> accountClaimService.sendInvitation(accountIdentifier, dto)
        );

        assertNotNull(exception);

        verify(accountRepository).findByIdentifier(accountIdentifier);
        verifyNoInteractions(accountService);
    }

    @Test
    void claimAccount_shouldClaimAccount_whenInstallationAccountEnabled() {
        ReflectionTestUtils.setField(accountClaimService, "installationAccountClaimEnabled", true);
        AccountClaimDTO dto = new AccountClaimDTO();
        dto.setRegistryId(100L);
        dto.setAccountClaimCode("CLAIM_CODE");

        Account account = new Account();
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);

        when(accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(
                dto.getRegistryId(), dto.getAccountClaimCode()))
                .thenReturn(Optional.of(account));

        when(accountService.claimAccount(dto)).thenReturn(99L);

        Long result = accountClaimService.claimAccount(dto);

        assertEquals(99L, result);

        verify(accountRepository)
                .findByCompliantEntityIdentifierAndAccountClaimCode(
                        dto.getRegistryId(), dto.getAccountClaimCode());
        verify(accountService).claimAccount(dto);
    }

    @Test
    void claimAccount_shouldThrowException_whenAccountTypeIsNull() {
        AccountClaimDTO dto = new AccountClaimDTO();
        dto.setRegistryId(200L);
        dto.setAccountClaimCode("INVALID");

        when(accountRepository.findByCompliantEntityIdentifierAndAccountClaimCode(
                dto.getRegistryId(), dto.getAccountClaimCode()))
                .thenReturn(Optional.empty());

        assertThrows(AccountActionException.class,
                () -> accountClaimService.claimAccount(dto));

        verify(accountRepository)
                .findByCompliantEntityIdentifierAndAccountClaimCode(
                        dto.getRegistryId(), dto.getAccountClaimCode());
        verifyNoInteractions(accountService);
    }
}
