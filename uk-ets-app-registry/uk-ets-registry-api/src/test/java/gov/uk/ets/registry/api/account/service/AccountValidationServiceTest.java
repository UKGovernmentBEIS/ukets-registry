package gov.uk.ets.registry.api.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountStatusChangeDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AccountValidationServiceTest {

    @MockBean
    UserService userService;
    @MockBean
    AuthorizationService authorizationService;

    private AccountValidationService accountValidationService;
    private Account account;
    private AccountStatusChangeDTO request;
    private User currentUser;

    @BeforeEach
    void setUp() {
        accountValidationService = new AccountValidationService(authorizationService, userService);
        account = new Account();
        account.setIdentifier(1234L);
        request = new AccountStatusChangeDTO();
        request.setAccountStatus(AccountStatus.OPEN);
        currentUser = new User();
        currentUser.setIamIdentifier("9a4ae5f6-2a68-42b2-94db-786db27daaf4");
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    @DisplayName("Account with account closure eligibility, expected to return false")
    void testClosureRequestEligibilityWithStatusAllTransactionsRestricted() {
        account.setAccountStatus(AccountStatus.ALL_TRANSACTIONS_RESTRICTED);
        assertFalse(accountValidationService.checkAccountClosureRequestEligibility(account));
    }

    @Test
    @DisplayName("Account with account closure eligibility, expected to return true")
    void testClosureRequestEligibilityAsSeniorAdminAOHA() {
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("senior-registry-administrator");
        List<RoleRepresentation> list = new ArrayList<>();
        list.add(roleRepresentation);
        when(authorizationService.getClientLevelRoles(currentUser.getIamIdentifier())).thenReturn(list);
        assertTrue(accountValidationService.checkAccountClosureRequestEligibility(account));
    }

    @Test
    @DisplayName("Account with account closure eligibility, expected to return false")
    void testClosureRequestEligibilityAsAuthorisedRepresentativeAOHA() {
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("authorized-representative");
        List<RoleRepresentation> list = new ArrayList<>();
        list.add(roleRepresentation);
        when(authorizationService.getClientLevelRoles(currentUser.getIamIdentifier())).thenReturn(list);
        assertFalse(accountValidationService.checkAccountClosureRequestEligibility(account));
    }

    @Test
    @DisplayName("Accounts with status CLOSED cannot be updated, expected to fail")
    void changeAccountStatusWithClosedAccountRequest() {

        account.setAccountStatus(AccountStatus.CLOSED);
        request.setAccountStatus(AccountStatus.OPEN);
        request.setComment("Unblock a closed account");

        AccountActionException exception = assertThrows(
            AccountActionException.class,
            () -> accountValidationService.validateAccountStatusChange(account.getAccountStatus(), request.getAccountStatus()));

        assertTrue(exception.getMessage().contains("Accounts with status CLOSED cannot be updated."));
        assertTrue(
            exception.getAccountActionError().getMessage().contains("Accounts with status CLOSED cannot be updated."));
        assertTrue(
            exception.getAccountActionError().getCode().contains(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED));
    }

    @Test
    @DisplayName("Remove transaction restrictions of an account that has all of its transactions restricted , expected to fail")
    void unblockAccountBlockedBySystem() {
        account.setAccountStatus(AccountStatus.ALL_TRANSACTIONS_RESTRICTED);
        request.setComment("Unblock the account");

        AccountActionException exception = assertThrows(
            AccountActionException.class,
            () -> accountValidationService.validateAccountStatusChange(account.getAccountStatus(), request.getAccountStatus()));

        assertEquals("You cannot remove restrictions for an account for which all transactions have been restricted by the system.", exception.getMessage());
        assertEquals("You cannot remove restrictions for an account for which all transactions have been restricted by the system.", exception.getAccountActionError().getMessage());
        assertTrue(
            exception.getAccountActionError().getCode().contains(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED));
    }

    @Test
    @DisplayName("Unblock account with outstanding closure requests , expected to fail")
    void unblockAccountWithOutstandingClosureRequest() {
        account.setAccountStatus(AccountStatus.CLOSURE_PENDING);
        request.setAccountStatus(AccountStatus.OPEN);
        request.setComment("Unblock the account with outstanding closure requests");

        AccountActionException exception = assertThrows(
            AccountActionException.class,
            () -> accountValidationService.validateAccountStatusChange(account.getAccountStatus(), request.getAccountStatus()));

        assertTrue(exception.getMessage().contains("You cannot remove restrictions for an account with outstanding closure requests."));
        assertTrue(exception.getAccountActionError().getMessage()
                            .contains("You cannot remove restrictions for an account with outstanding closure requests."));
        assertTrue(
            exception.getAccountActionError().getCode().contains(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED));
    }

    @Test
    @DisplayName("Partially block account with outstanding closure requests , expected to fail")
    void partiallyBlockAccountWithOutstandingClosureRequest() {
        account.setAccountStatus(AccountStatus.CLOSURE_PENDING);
        request.setAccountStatus(AccountStatus.SOME_TRANSACTIONS_RESTRICTED);
        request.setComment("Partially block the account with outstanding closure requests");

        AccountActionException exception = assertThrows(
            AccountActionException.class,
            () -> accountValidationService.validateAccountStatusChange(account.getAccountStatus(), request.getAccountStatus()));

        assertTrue(exception.getMessage()
                            .contains("You cannot restrict some transactions for an account with outstanding closure requests."));
        assertTrue(exception.getAccountActionError().getMessage()
                            .contains("You cannot restrict some transactions for an account with outstanding closure requests."));
        assertTrue(
            exception.getAccountActionError().getCode().contains(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED));
    }

    @Test
    @DisplayName("Partially suspend account with outstanding closure requests , expected to fail")
    void partiallySuspendAccountWithOutstandingClosureRequest() {
        account.setAccountStatus(AccountStatus.CLOSURE_PENDING);
        request.setAccountStatus(AccountStatus.SUSPENDED_PARTIALLY);
        request.setComment("Partially suspend the account with outstanding closure requests");

        AccountActionException exception = assertThrows(
            AccountActionException.class,
            () -> accountValidationService.validateAccountStatusChange(account.getAccountStatus(), request.getAccountStatus()));

        assertTrue(exception.getMessage()
                            .contains("You cannot partially suspend an account with outstanding closure requests."));
        assertTrue(exception.getAccountActionError().getMessage()
                            .contains("You cannot partially suspend an account with outstanding closure requests."));
        assertTrue(
            exception.getAccountActionError().getCode().contains(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED));
    }

	@Test
	@DisplayName("Exclude OHA from billing process, expected to fail")
	void checkExcludedOHAFromBillingRequest() {
		account.setAccountStatus(AccountStatus.OPEN);
		account.setAccountType("ETS - Trading account");

		assertTrue(accountValidationService.checkExcludedFromBillingRequest(account));
	}

	@Test
	@DisplayName("Exclude Account with invalid type from billing process, expected to fail")
	void checkExcludedAccountWithInvalidTypeFromBillingRequest() {
		account.setAccountStatus(AccountStatus.OPEN);
		
		account.setAccountType("ETS - Operator holding account");
		assertFalse(accountValidationService.checkExcludedFromBillingRequest(account));
		
		account.setAccountType("ETS - Aircraft operator holding account");
		assertFalse(accountValidationService.checkExcludedFromBillingRequest(account));
	}

	@Test
	@DisplayName("Exclude Account with invalid status from billing process, expected to fail")
	void checkExcludedAccountWithInvalidStatusFromBillingRequest() {
		account.setAccountType("ETS - Operator holding account");
		
		account.setAccountStatus(AccountStatus.CLOSED);
		assertFalse(accountValidationService.checkExcludedFromBillingRequest(account));
		
		account.setAccountStatus(AccountStatus.TRANSFER_PENDING);
		assertFalse(accountValidationService.checkExcludedFromBillingRequest(account));
		
		account.setAccountStatus(AccountStatus.SUSPENDED);
		assertFalse(accountValidationService.checkExcludedFromBillingRequest(account));
	}

}
