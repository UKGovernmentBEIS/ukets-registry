package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.accountcontact.AccountContactSendInvitationDTO;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountClaimProcessorTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountClaimProcessor processor;

    private final Long ACCOUNT_ID = 1L;

    private Account account;

    @BeforeEach
    void setup() {
        account = new Account();
    }

    @Test
    void processSendInvitation_shouldCallService_whenAccountTypeEnabled() {

        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);

        when(accountRepository.findByIdentifier(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        ReflectionTestUtils.setField(processor, "installationAccountClaimEnabled", true);

        AccountContactSendInvitationDTO dto = new AccountContactSendInvitationDTO();

        when(accountService.sendInvitation(ACCOUNT_ID, dto)).thenReturn("OK");

        String result = processor.processSendInvitation(ACCOUNT_ID, dto);

        assertEquals("OK", result);
        verify(accountService).sendInvitation(ACCOUNT_ID, dto);
    }

    @Test
    void processSendInvitation_shouldThrowException_whenAccountTypeDisabled() {

        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);

        when(accountRepository.findByIdentifier(ACCOUNT_ID))
                .thenReturn(Optional.of(account));

        ReflectionTestUtils.setField(processor, "installationAccountClaimEnabled", false);

        AccountContactSendInvitationDTO dto = new AccountContactSendInvitationDTO();

        assertThrows(AccountActionException.class,
                () -> processor.processSendInvitation(ACCOUNT_ID, dto));

        verify(accountService, never()).sendInvitation(any(), any());
    }

    @Test
    void processSendInvitation_shouldThrowException_whenAccountNotFound() {

        when(accountRepository.findByIdentifier(ACCOUNT_ID))
                .thenReturn(Optional.empty());

        AccountContactSendInvitationDTO dto = new AccountContactSendInvitationDTO();

        assertThrows(AccountActionException.class,
                () -> processor.processSendInvitation(ACCOUNT_ID, dto));

        verify(accountService, never()).sendInvitation(any(), any());
    }

    @Test
    void isAccountClaimEnabled_shouldReturnTrue_forEnabledType() {

        ReflectionTestUtils.setField(processor, "aviationAccountClaimEnabled", true);

        boolean result = processor.isAccountClaimEnabled(
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);

        assertTrue(result);
    }

    @Test
    void isAccountClaimEnabled_shouldReturnFalse_forDisabledType() {

        ReflectionTestUtils.setField(processor, "aviationAccountClaimEnabled", false);

        boolean result = processor.isAccountClaimEnabled(
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);

        assertFalse(result);
    }

    @Test
    void isAccountClaimEnabled_shouldReturnFalse_forNull() {

        boolean result = processor.isAccountClaimEnabled(null);

        assertFalse(result);
    }

    @Test
    void getEnabledAccountTypes_shouldReturnAllEnabledTypes() {

        ReflectionTestUtils.setField(processor, "installationAccountClaimEnabled", true);
        ReflectionTestUtils.setField(processor, "aviationAccountClaimEnabled", true);
        ReflectionTestUtils.setField(processor, "maritimeAccountClaimEnabled", true);

        List<RegistryAccountType> result = processor.getEnabledAccountTypes();

        assertEquals(3, result.size());
        assertTrue(result.contains(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT));
        assertTrue(result.contains(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT));
        assertTrue(result.contains(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT));
    }

    @Test
    void getEnabledAccountTypes_shouldReturnEmpty_whenAllDisabled() {

        ReflectionTestUtils.setField(processor, "installationAccountClaimEnabled", false);
        ReflectionTestUtils.setField(processor, "aviationAccountClaimEnabled", false);
        ReflectionTestUtils.setField(processor, "maritimeAccountClaimEnabled", false);

        List<RegistryAccountType> result = processor.getEnabledAccountTypes();

        assertTrue(result.isEmpty());
    }

    @Test
    void getEnabledAccountTypes_shouldReturnPartialEnabled() {

        ReflectionTestUtils.setField(processor, "installationAccountClaimEnabled", true);
        ReflectionTestUtils.setField(processor, "aviationAccountClaimEnabled", false);
        ReflectionTestUtils.setField(processor, "maritimeAccountClaimEnabled", true);

        List<RegistryAccountType> result = processor.getEnabledAccountTypes();

        assertEquals(2, result.size());
        assertTrue(result.contains(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT));
        assertTrue(result.contains(RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT));
    }
}